package hazardland.lib.db;

public class Method 
{
	public static final String ASC = "ASC";
	public static final String DESC = "DESC";
	public String name = ASC;
	public Method ()
	{
		
	}
	public Method (String name)
	{
		this.name = name; 
	}
	public void swap ()
	{
		if (name==ASC)
		{
			name = DESC;
		}
		else
		{
			name = ASC;
		}
	}
}
