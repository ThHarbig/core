/** This is the interface that has to be implemented by ALL MIO types.
 */ 

package mayday.core.meta;

import mayday.core.meta.gui.AbstractMIRenderer;


public interface MIType {
	
	public final static int SERIAL_TEXT=0;
	public final static int SERIAL_XML=1;
	
	/** Get a MIRenderer that can display the contents of the MIO as well as provide an Editor Component.
	 * @return the MIRenderer
	 */
	@SuppressWarnings("unchecked")
	public AbstractMIRenderer getGUIElement();  //replaces MIO.viewerComponent, allows editing
	
	/** Serialize the value of this object into a String. Does not add enclosing Tags for XML serialization,
	 * i.e. a double value mio with value=5.2 should be represented as "5.2", not as "<Double>5.2</Double>" in XML. 
	 * @param serializationType - SERIAL_TEXT or SERIAL_XML
	 * @return the serialized form or NULL if the MIO has no value
	 */
	public String serialize(int serializationType);
	
	/** Restore the value of this object from a String as created by serialize(). 
	 * @param serializationType - SERIAL_TEXT or SERIAL_XML
	 * @param serializedForm The serialized form
	 * @return true if the deSerialization was successful.
	 */
	public boolean deSerialize(int serializationType, String serializedForm);
	
	/** Clone this MIO object.
	 * @return the new Object
	 */
	public MIType clone();
	
	public String toString();
	
	/** Get the Pluma ID of the type of this MIO.
	 * @return the Pluma ID.
	 */
	public String getType();
	
}
