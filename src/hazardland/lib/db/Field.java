package hazardland.lib.db;

public class Field 
{
	public String name;
	public int type = 0;
	public boolean required = false;
	@SuppressWarnings("rawtypes")
	public Class foreign = null;
	public boolean primary = false; 
	@SuppressWarnings("rawtypes")
	public Class value = null;
	public boolean insert = true;
	public boolean select = true;
	public boolean update = true;
	public Event event = new Event ();
	public Config config = new Config();
	public Field (String name)
	{
		this.name = name;
	}
	public String type ()
	{
		if (type==Type.INTEGER || type==Type.BOOLEAN)
		{
			return "INTEGER";
		}
		if (type==Type.FLOAT)
		{
			return "FLOAT";
		}
        if (type==Type.BLOB)
        {
            return "BLOB";
        }
		return "TEXT";
	}
	public String required ()
	{
		if (required)
		{
			return "NOT NULL";
		}
		return "";
	}
	public String primary ()
	{
		if (primary)
		{
			return "PRIMARY KEY AUTOINCREMENT";			
		}
		return "";
	}
}
