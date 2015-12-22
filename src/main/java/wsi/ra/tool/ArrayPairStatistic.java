/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
/*
 *    PairedStats.java
 *    Copyright (C) 1999 Len Trigg
 *
 */
package wsi.ra.tool;


/**
 */
/**
 * A class for storing stats on a paired comparison (t-test and correlation)
 *
 * @author Len Trigg (trigg@cs.waikato.ac.nz)
 * @version $Revision: 1.1 $
 */
public class ArrayPairStatistic
{
    //~ Instance fields ////////////////////////////////////////////////////////

    /** The stats associated with the paired differences */
    public ArrayStatistic differencesArrayStatistic;

    /** The stats associated with the data in column 1 */
    public ArrayStatistic xArrayStatistic;

    /** The stats associated with the data in column 2 */
    public ArrayStatistic yArrayStatistic;

    /** The correlation coefficient */
    public double correlation;

    /** The number of data points seen */
    public double count;

    /** The probability of obtaining the observed differences */
    public double differencesProbability;

    /** The significance level for comparisons */
    public double sigLevel;

    /** The sum of the products */
    public double xySum;

    /**
    * A significance indicator:
    * 0 if the differences are not significant
    * > 0 if x significantly greater than y
    * < 0 if x significantly less than y
    */
    public int differencesSignificance;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
    * Creates a new PairedArrayStatistic object with the supplied significance level.
    *
    * @param sig the significance level for comparisons
    */
    public ArrayPairStatistic(double sig)
    {
        xArrayStatistic = new ArrayStatistic();
        yArrayStatistic = new ArrayStatistic();
        differencesArrayStatistic = new ArrayStatistic();
        sigLevel = sig;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
    * Add an observed pair of values.
    *
    * @param value1 the value from column 1
    * @param value2 the value from column 2
    */
    public void add(double value1, double value2)
    {
        xArrayStatistic.add(value1);
        yArrayStatistic.add(value2);
        differencesArrayStatistic.add(value1 - value2);
        xySum += (value1 * value2);
        count++;
    }

    /**
    * Calculates the derived statistics (significance etc).
    */
    public void calculateDerived()
    {
        xArrayStatistic.calculateDerived();
        yArrayStatistic.calculateDerived();
        differencesArrayStatistic.calculateDerived();

        correlation = Double.NaN;

        if (!Double.isNaN(xArrayStatistic.stdDev) &&
                !Double.isNaN(yArrayStatistic.stdDev) &&
                !StatisticUtils.eq(xArrayStatistic.stdDev, 0))
        {
            double slope = (xySum -
                ((xArrayStatistic.sum * yArrayStatistic.sum) / count)) / (xArrayStatistic.sumSq -
                (xArrayStatistic.sum * xArrayStatistic.mean));

            if (!StatisticUtils.eq(yArrayStatistic.stdDev, 0))
            {
                correlation = (slope * xArrayStatistic.stdDev) / yArrayStatistic.stdDev;
            }
            else
            {
                correlation = 1.0;
            }
        }

        if (StatisticUtils.gr(differencesArrayStatistic.stdDev, 0))
        {
            double tval = (differencesArrayStatistic.mean * Math.sqrt(count)) / differencesArrayStatistic.stdDev;
            differencesProbability = Statistics.FProbability(tval * tval, 1,
                    (int) count - 1);
        }
        else
        {
            if (differencesArrayStatistic.sumSq == 0)
            {
                differencesProbability = 1.0;
            }
            else
            {
                differencesProbability = 0.0;
            }
        }

        differencesSignificance = 0;

        if (differencesProbability <= sigLevel)
        {
            if (xArrayStatistic.mean > yArrayStatistic.mean)
            {
                differencesSignificance = 1;
            }
            else
            {
                differencesSignificance = -1;
            }
        }
    }

    /**
    * Tests the paired stats object from the command line.
    * reads line from stdin, expecting two values per line.
    *
    * @param args ignored.
    */
    public static void main(String[] args)
    {
        try
        {
            ArrayPairStatistic ps = new ArrayPairStatistic(0.05);
            java.io.LineNumberReader r = new java.io.LineNumberReader(new java.io.InputStreamReader(
                        System.in));
            String line;

            while ((line = r.readLine()) != null)
            {
                line = line.trim();

                if (line.equals("") || line.startsWith("@") ||
                        line.startsWith("%"))
                {
                    continue;
                }

                java.util.StringTokenizer s = new java.util.StringTokenizer(line,
                        " ,\t\n\r\f");
                int count = 0;
                double v1 = 0;
                double v2 = 0;

                while (s.hasMoreTokens())
                {
                    double val = (new Double(s.nextToken())).doubleValue();

                    if (count == 0)
                    {
                        v1 = val;
                    }
                    else if (count == 1)
                    {
                        v2 = val;
                    }
                    else
                    {
                        System.err.println("MSG: Too many values in line \"" +
                            line + "\", skipped.");

                        break;
                    }

                    count++;
                }

                if (count == 2)
                {
                    ps.add(v1, v2);
                }
            }

            ps.calculateDerived();
            System.err.println(ps);
        }
         catch (Exception ex)
        {
            ex.printStackTrace();
            System.err.println(ex.getMessage());
        }
    }

    /**
    * Removes an observed pair of values.
    *
    * @param value1 the value from column 1
    * @param value2 the value from column 2
    */
    public void subtract(double value1, double value2)
    {
        xArrayStatistic.subtract(value1);
        yArrayStatistic.subtract(value2);
        differencesArrayStatistic.subtract(value1 - value2);
        xySum -= (value1 * value2);
        count--;
    }

    /**
    * Returns statistics on the paired comparison.
    *
    * @return the t-test statistics as a string
    */
    @Override
	public String toString()
    {
        return "Analysis for " + count + " points:\n" + "                " +
        "         Column 1" + "         Column 2" + "       Difference\n" +
        "Minimums        " + xArrayStatistic.min + yArrayStatistic.min +
        differencesArrayStatistic.min + '\n' + "Maximums        " +
        xArrayStatistic.max + yArrayStatistic.max +
        differencesArrayStatistic.max + '\n' + "Sums            " +
        xArrayStatistic.sum + yArrayStatistic.sum +
        differencesArrayStatistic.sum + '\n' + "SumSquares      " +
        xArrayStatistic.sumSq + yArrayStatistic.sumSq +
        differencesArrayStatistic.sumSq + '\n' + "Means           " +
        xArrayStatistic.mean + yArrayStatistic.mean +
        differencesArrayStatistic.mean + '\n' + "SDs             " +
        xArrayStatistic.stdDev + yArrayStatistic.stdDev +
        differencesArrayStatistic.stdDev + '\n' + "Prob(differences) " +
        differencesProbability + " (sigflag " + differencesSignificance +
        ")\n" + "Correlation       " + correlation + "\n";
    }
}
///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
