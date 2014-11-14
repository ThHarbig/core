/*
 * File LinDecRadKernelTest.java
 * Created on 08.11.2005
 * As part of package mayday.clustering.som.kernel.jutest
 * By Janko Dietzsch
 */

package mayday.clustering.som.kernel.jutest;

import junit.framework.TestCase;
import mayday.clustering.som.kernel.LinearDecreasingRadiusKernel;
import mayday.core.math.functions.GaussianKernel;
import mayday.core.math.functions.IRotationalKernelFunction;

public class LinDecrRadKernelTest extends TestCase {
	private IRotationalKernelFunction gaussKernel;
	
	protected void setUp() throws Exception {
		super.setUp();
		this.gaussKernel = new GaussianKernel(2.0);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test method for 'mayday.clustering.som.kernel.LinearDecreasingRadiusKernel.getValueOfDistance(int)'
	 */
	public void testGetValueOfDistance() {
		LinearDecreasingRadiusKernel ldrKernel = new LinearDecreasingRadiusKernel(2, 0.1, 40, 10, gaussKernel);
		int i = 0;
		System.out.println("Start radius: " + ldrKernel.getStartRadius() + " End radius: "+ ldrKernel.getEndRadius());
		do {
			ldrKernel.tick();
			System.out.println("New round: " + i);
			for (int j = 0; j < 11; j++) System.out.print(ldrKernel.getValueOfDistance(j) + " ");
			System.out.println();
			i++;
		} while (!ldrKernel.endReached());
	}

	/*
	 * Test method for 'mayday.clustering.som.kernel.LinearDecreasingRadiusKernel.resetTime()'
	 */
	public void testResetTime() {

	}

	
}
