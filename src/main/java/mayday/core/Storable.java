package mayday.core;

import mayday.core.meta.types.AnnotationMIO;
/*
 * Created on Apr 8, 2003
 *
 */

/**
 * @author neil
 * @version 
 */
@SuppressWarnings("unchecked")
public interface Storable
extends Comparable
{
  public void setAnnotation( AnnotationMIO annotation );
  public AnnotationMIO getAnnotation();
  public String toString();
  public String getName();
}
