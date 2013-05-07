package hazardland.lib.db;

public class Value 
{
	public Value ()
	{
		
	}
	public void set (String value)
	{
	    debug ("set please dont call me");
	}
	public String get ()
	{
	    debug ("get please dont call me");
		return "";
	}
	public void debug (String message)
	{
	    System.out.println ("value: "+message);
	}
}
