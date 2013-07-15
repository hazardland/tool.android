package hazardland.lib.db;

public class Limit 
{
	public Integer from;
	public Integer count;
	public Limit ()
	{
		
	}
	public Limit (Integer count)
	{
		this.count = count;
	}
	public Limit (Integer from, Integer count)
	{
		this.from = from;
		this.count = count;
	}
	public String toString ()
	{
		if (count==null)
		{
			return "";
		}
		if (from==null)
		{
			return " LIMIT " + count + " ";
		}
		return  " LIMIT "+from+","+count + " ";
	}
}
