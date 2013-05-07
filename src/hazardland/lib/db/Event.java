package hazardland.lib.db;

public class Event 
{
	public final static int SELECT = 1;
	public final static int INSERT = 2;
	public final static int UPDATE = 3;
	public final static int DELETE = 4;
	public Action select = new Action();
	public Action insert = new Action();
	public Action update = new Action();
	public Action delete = new Action();
	public int type;
	public Event ()
	{
		
	}
	public Event (int type)
	{
	    this.type = type;
	}
}
