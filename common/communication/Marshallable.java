package common.communication;

/*
	Provides a standard interface that is applicable to all objects that will be marshaled.
*/
public interface Marshallable {
	// Returns a list of all attributes of the class
	public String[] getAllAttributes();

	// Returns a specified attribute
	public Object getAttribute(String attr);

	// Set a specified attribute
	public void setAttribute(String attr, Object val);
}
