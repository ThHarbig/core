/*
 * Created on Jan 30, 2005
 *
 */
package mayday.core;

/**
 * @author gehlenbo
 *
 */
public final class ComplexNumber
{
  private Double real;
  private Double imaginary;
  
  
  public ComplexNumber( Double real, Double imaginary )
  {
    this.real = real;
    this.imaginary = imaginary;
  }
  
  
  public Double getReal()
  {
    return ( this.real );
  }
  
  
  public void setReal( Double real )
  {
    this.real = real;
  }
  
  
  public Double getImaginary()
  {
    return ( this.imaginary );
  }

  
  public void setImaginary( Double imaginary )
  {
    this.imaginary = imaginary;
  }
  
  
  public String toString()
  {
    String l_string = this.real.toString();
    
    if ( this.imaginary < 0 )
    {
      l_string += " - "; 
    }
    else
    {
      l_string += " + "; 
    }
    
    l_string +=  this.imaginary.toString() + "i";
    
    return ( l_string );
  }
}
