package hazardland.lib.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper
{
	public String name;
//	public Map<Class<? extends Entity>, Table<? extends Entity>> tables = new HashMap <Class<? extends Entity>, Table<? extends Entity>> ();
	@SuppressWarnings("rawtypes")
	public Map<Class, Table> tables = new HashMap <Class, Table> ();	
	public Context context;
	public int version;
	protected ArrayList<Lock> save = new ArrayList<Lock>();
	protected ArrayList<Lock> load = new ArrayList<Lock>();
	protected ArrayList<Trash> trash = new ArrayList<Trash>();
	
	public Database (Context context, String name, int version)
	{
		super (context, name, null, version);
		this.name = name;
		this.context = context;
		this.version = version;
	}
	
//	public <Target extends Entity> void add (Class<Target> target, Table<Target> table) 
//	{
//	    tables.put(target, table);
//	}	

	
	@SuppressWarnings("rawtypes")
	public void add (Class target, Table table) 
	{
	    tables.put(target, table);
	}	
	
//	@SuppressWarnings("unchecked")
//	public <Target extends Entity> Table<Target> table (Class<Target> target) 
//	{
//	    return (Table<Target>) tables.get (target);
//	}

	@SuppressWarnings("rawtypes")
	public Table table (Class target) 
	{
	    return tables.get (target);
	}

	@Override
	public void onCreate (SQLiteDatabase database) 
	{
	    debug ("global install event with " + tables.size() + " tables");
        for (Table table : tables.values())
        {
            table.install (database);
        }		
	}

	@Override
	public void onUpgrade (SQLiteDatabase database, int oldVersion, int newVersion) 
	{
		debug ("global upgrade event with " + tables.size() + " tables");
		for (Table table : tables.values())
        {
            table.upgrade (database);
        }
	}
	
	public void debug (String message)
	{
		System.out.println ("database: "+message);
	}
	
	public int clean ()
	{
	    int result = 0;
	    for (Trash item : trash)
        {
            result += item.clean();
        }
	    this.trash.clear();
	    return result;
	}
	
	public void trash (Trash trash)
	{
	    this.trash.add (trash);
	}
}
