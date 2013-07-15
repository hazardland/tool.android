package hazardland.lib.db;

import android.database.sqlite.SQLiteDatabase;

public class Lock 
{
	public static final int SAVE=1;
	public static final int LOAD=2;
	
	public Database parent;
	public SQLiteDatabase database;
	public boolean active = false;
	public int type;
	
	private boolean disable = false;
	
	public Lock (Database database, int type)
	{
		this.type = type;
		this.parent = database;
		lock ();
	}
	
	protected void lock  ()
	{
		if (disable)
		{
			return;
		}
		if (type==SAVE)
		{
			if (parent.load.size()!=0 || parent.save.size()!=0)
			{
				while (parent.load.size()!=0 || parent.save.size()!=0)
				{
					try 
					{
						java.lang.Thread.sleep(25);
					} 
					catch (InterruptedException error) 
					{
						
					}
				}
			}
			active = true;
			//parent.save.add (id, this);
			parent.save.add (this);
			//debug ("locking save with pid "+id);
			return;
		}
		if (type==LOAD)
		{
//			if (parent.save.size()!=0)
//			{
//				while (parent.save.size()!=0)
//				{
            if (parent.load.size()!=0 || parent.save.size()!=0)
            {
                while (parent.load.size()!=0 || parent.save.size()!=0)
                {		    
					try 
					{
						java.lang.Thread.sleep(25);
					} 
					catch (InterruptedException error) 
					{
						
					}
				}
			}
			active = true;
			//parent.load.add (id, this);
			parent.load.add (this);
			//debug ("locking load with pid "+id);
			return;
		}		
	}
	
	protected void unlock ()
	{
		if (disable)
		{
			return;
		}		
		if (active==false)
		{
			return;
		}
		if (this.type==SAVE)
		{
			//parent.save.remove (id);
			parent.save.remove (this);
			//debug ("unlocking save with pid "+id);
		}
		else
		{
			//parent.load.remove (id);
			parent.load.remove (this);
			//debug ("unlocking load with pid "+id);
		}
		active = false;
	}
	
	public void debug (String message)
	{
		System.out.println("locker: " + message);
	}
	
}
