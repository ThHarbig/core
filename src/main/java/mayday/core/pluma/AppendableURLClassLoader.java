/*
 * Created on 08.06.2005
 */
package mayday.core.pluma;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * A ClassLoader where class paths can be appended.
 * 
 * 
 * @author Matthias Zschunke
 * @version 0.1
 * Created on 08.06.2005
 *
 */
public class AppendableURLClassLoader extends URLClassLoader
{
    public AppendableURLClassLoader(ClassLoader parent) {
        super(new URL[0], parent);
    }
    
    public AppendableURLClassLoader()  {
        this(Thread.currentThread().getContextClassLoader());
    }
    
    protected synchronized void addURL(URL url)
    {     
      if (!containsURL(url)) {
        super.addURL(url);
      }
    }
    
    public synchronized boolean containsURL(URL cmp)
    {
    	if (cmp!=null) {
            for(URL url : this.getURLs()) {
                if(url.equals(cmp)) return true;
            }    		
    	}
    	return false;
    }
    
    public Package[] getPackages() {
    	return super.getPackages();
    }
}
