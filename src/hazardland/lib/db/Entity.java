package hazardland.lib.db;

public class Entity 
{
	public Integer id;
	public Database database;
	public Entity ()
	{
		
	}
	public Integer id ()
	{
	    return id;
	}
	public void create ()
	{
		
	}
	public Entity database (Database database)
	{
		this.database = database;
		return this;
	}
	public Database database ()
	{
		return database;
	}
	public String toString ()
	{
	    if (id==null)
	    {
	        return "NULL";
	    }
	    return id.toString();
	}
}
