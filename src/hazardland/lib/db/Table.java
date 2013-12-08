package hazardland.lib.db;

//□ value class (set/get) methods
//□ entity class
//□ integer, float, string
//
//if (public void [entity] ([entity] entity))
//then use
//else object.entity = entity
//
//entity object.entity() to get object
//
//if 
//date object.insert - set date on insert
//date object.update - set date on update
//
//use only datetime field types
//
//if name or caption than required

import hazardland.lib.tool.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class Table<Target extends Entity>// extends SQLiteOpenHelper 
{
	
	public Database database;
	public String name;
	public Class<?> target;
	public Field primary;
	public String[] select = new String[] {};	
	public Map <String, Field> fields = new HashMap <String, Field> ();
	public static boolean debug = false;
	public Query query;
	public Build build = new Build();
	
	public Table (Database database, Class<?> target)
	{
		//super (database.context, database.name, null, database.version);

		this.database = database;
		this.target = target;
		//this.name = Text.afterLast (".", target.getName()).toLowerCase()+"s";
		this.name = Text.afterLast (".", target.getName()).toLowerCase();
		debug ("table name is "+name);
		debug ("------------------------------------");
		java.lang.reflect.Field[] propertys = target.getFields();
		for (int position = 0; position < propertys.length; position++) 
		{
			if (!propertys[position].getType().isPrimitive())
			{
				Field field = new Field (propertys[position].getName());
				if (equals(propertys[position], Integer.class))
				{
					field.type = Type.INTEGER;
				}
				else if (equals(propertys[position],Float.class)) 
				{
					field.type = Type.FLOAT;
				}
				else if (equals(propertys[position],String.class))
				{
					field.type = Type.TEXT;
				}
				else if (equals(propertys[position],Boolean.class))
				{
					field.type = Type.BOOLEAN;
				}				
				else if (inherits(propertys[position],Value.class))
				{
					field.type = Type.TEXT;
					field.value = propertys[position].getType();
					debug ("setting value type for "+propertys[position].getType().getName());
					debug ("you can change type before use:");
				}
                else if (inherits(propertys[position],Blob.class))
                {
                    field.type = Type.BLOB;
                    field.value = propertys[position].getType();
                    debug ("setting blob type for "+propertys[position].getType().getName());
                    debug ("you can change type before use:");
                }				
				else if (inherits(propertys[position],Entity.class))
				{
					field.type = Type.INTEGER;
					field.foreign = propertys[position].getType();
					debug ("setting foreign "+propertys[position].getType().getName());
				}
				if (field.name.equalsIgnoreCase("id"))
				{
					debug ("the field is the primary");
					field.primary = true;
				}
				else if (field.name.equalsIgnoreCase("name") || field.name.equalsIgnoreCase("caption"))
				{
					if (field.type==Type.TEXT && field.value==null)
					{
						//field.required = true;
						//debug ("assumed as required");
					}
				}
				else if (field.name.equalsIgnoreCase("insert"))
				{
					field.event.insert.action = Action.DATE;
					debug ("setting auto date on insert");
				}
				else if (field.name.equalsIgnoreCase("update"))
				{
					field.event.update.action = Action.DATE;
					debug ("setting auto date on update");
				}
				if (field.type!=0)
				{
					fields.put(field.name, field);
					debug ("adding field "+field.name);
					if (field.type==Type.INTEGER)
					{
						debug ("as integer");
					}
					else if (field.type==Type.FLOAT)
					{
						debug ("as float");
					}
					else if (field.type==Type.TEXT)
					{
						debug ("as text");
					}
					else if (field.type==Type.BOOLEAN)
					{
						debug ("as boolean");
					}					
					debug ("------------------------------------");
				}
				else
				{
					debug ("rejecting "+field.name+" ("+propertys[position].getType().getName()+")");
					debug ("------------------------------------");
				}
			}
			else
			{
				debug ("rejecting field");
				debug ("------------------------------------");
			}
		}
		primary = field("id");
		query = new Query(this);
		select = fields.keySet().toArray(select);
		debug ("field count "+select.length);
		debug ("primary is "+primary.name);
		debug ("------------------------------------");

		
//		if (database.upgrade)
//		{
//			upgrade ();
//		}
//		
//		if (database.install)
//		{
//			install ();
//		}
		
		//reset ();
		
	}
	
//	public void reset ()
//	{
//		SQLiteDatabase reset = database.getWritableDatabase();
//		upgrade (reset);
//		install (reset);
//		reset.close();		
//	}
	
	public void before (Target target, Event event)
	{
	    
	}
	
	public void after (Target target, Event event)
	{
	    
	}

	public void install (SQLiteDatabase database)
	{
		debug ("local install event");
		String create =  "CREATE TABLE IF NOT EXISTS \"" + name + "\" (";
		create += "\""+ primary.name + "\" "+ primary.type()+" "+primary.primary();
		
		for (Field field : fields.values()) 
		{
			if (!field.primary)
			{
				create += ",\"" + field.name + "\" "+ field.type()+" "+field.required();
			}
		}
		create += ");";
		debug (create);
        
		//SQLiteDatabase database = this.database.getWritableDatabase();
		database.execSQL (create);
		//database.close();
		
	}

	public void upgrade (SQLiteDatabase database)
	{
		debug ("local upgrade event");
		String upgrade = "DROP TABLE IF EXISTS \"" + name + "\"";
		debug (upgrade);
        //SQLiteDatabase database = this.database.getWritableDatabase();		
		database.execSQL (upgrade);
		install (database);
		//database.close();
	}
	
	public Field field (String name)
	{
		return fields.get (name);
	}
	
//	private Target apply (Cursor cursor)
//	{
//		return apply (cursor, 0);
//	}
	
	
    static class Property
    {
        static void set (java.lang.reflect.Field property, Object object, Object value)
        {
            try
            {
                property.set (object, value);
            } 
            catch (IllegalArgumentException e)
            {
                e.printStackTrace();
            } 
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
        static java.lang.reflect.Field get (Class<?> target, String field)
        {
            try
            {
                return target.getField(field);
            } 
            catch (SecurityException error) 
            {
                System.out.println ("[error] security exception for "+field);
            } 
            catch (NoSuchFieldException error) 
            {
                System.out.println ("[error] no such field "+field);
            }
            return null;
        }
        static void instance (java.lang.reflect.Field property, Object object, Class value)
        {
            try
            {
                try
                {
                    property.set (object, value.newInstance());
                } 
                catch (InstantiationException e)
                {
                    e.printStackTrace();
                }
            } 
            catch (IllegalArgumentException e)
            {
                e.printStackTrace();
            } 
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
    } 

	@SuppressWarnings("unchecked")
	private Target apply (Cursor cursor, Integer from)
	{
	    //Target result = (Target) new Entity ();
	    Target result = null;
		try 
		{
			result = (Target) target.newInstance();
		}
		catch (IllegalAccessException error) 
		{
			error.printStackTrace();
		} 
		catch (InstantiationException error) 
		{
			error.printStackTrace();
		}
		
		result.database (database);
		
//		debug ("biohazard " + target.getName());
//		debug (database.table(target).name);
		

		Field field = null;
		java.lang.reflect.Field property = null;
		if (from==0)
		{
		    debug ("loading");
		}
		debug ("----------------");
		for (int position = 0; position < select.length; position++) 
		{
			field = field (select[position]);
			property = Property.get(target, select[position]);
			
			if (field.type==Type.INTEGER && field.foreign!=null)
			{
			    if (cursor.getInt(from+position)>0)
			    {
    				if (from==0)
    				{
    				    //debug ("entering "+field.foreign.getName()+" for "+field.name+" to position "+position(field)+" from position "+ (from+position));
    					//Property.set (property, result, database.table(field.foreign).load(cursor.getInt(from+position)));
    					Property.set (property, result, database.table(field.foreign).apply(cursor, position(field)));
    
    				}
    				else
    				{
    				    debug (field.name+"("+(from+position)+") -> "+database.table(field.foreign).name+" ("+cursor.getInt(from+position)+") * empty");
    				    Property.set (property, result, database.table(field.foreign).instance(cursor.getInt(from+position)));
    				}
			    }
			    else
			    {
			        debug (field.name+"("+(from+position)+") -> "+database.table(field.foreign).name+" ("+cursor.getInt(from+position)+") * skip");
			        Property.set (property, result, database.table(field.foreign).instance());
			    }
			}
			else if (field.value!=null)
			{
			    Property.instance (property, result, field.value);
			    if (field.type==Type.BLOB)
			    {
			        //Blob.class.cast (property.get(result)).set(cursor.getBlob(from+position));
		            try
                    {
		               if (cursor.getBlob(from+position)!=null)
		               {
		                   Blob.class.cast (property.get(result)).set(cursor.getBlob(from+position), field.config);
		                   debug (field.name+"("+(from+position)+") -> blob ("+cursor.getBlob(from+position).length+")");
		                   if (includes(field.value, Trash.class))
		                   {
		                       database.trash ((Trash)property.get(result));
		                   }
		               }
		               else
		               {
		                   debug (field.name+"("+(from+position)+") -> blob (null)");		                   
		               }
                       
                    }
                    catch (SQLiteException e)
                    {
                        debug (field.name+"("+(from+position)+") -> blob (error)");
                       
                    }
		            catch (IllegalArgumentException e)
                    {
                        e.printStackTrace();
                    }
		            catch (IllegalAccessException e)
                    {
                        e.printStackTrace();
                    }

			    }
			    else
			    {
			        try
                    {
                        Value.class.cast(property.get(result)).set(cursor.getString(from+position));
                        debug (field.name+"("+(from+position)+") -> value (x)");
                    }
			        catch (IllegalArgumentException e)
                    {
                        e.printStackTrace();
                    }
			        catch (IllegalAccessException e)
                    {
                        e.printStackTrace();
                    }
			    }

			}			
			else if (field.type==Type.INTEGER)
			{
			    debug (field.name+"("+(from+position)+") -> "+cursor.getInt(from+position));
			    Property.set (property, result, cursor.getInt(from+position));
			}
			else if (field.type==Type.BOOLEAN)
			{
			    debug (field.name+"("+(from+position)+") -> "+(cursor.getInt(from+position)!=0));
			    Property.set (property, result, (cursor.getInt(from+position)!=0));
			}			
			else if (field.type==Type.FLOAT) 
			{
			    debug (field.name+"("+(from+position)+") -> "+cursor.getFloat(from+position));
			    Property.set (property, result, cursor.getFloat(from+position));
			}
			else if (field.type==Type.TEXT) 
			{
			    debug (field.name+"("+(from+position)+") -> "+cursor.getString(from+position));
			    Property.set (property, result, cursor.getString(from+position));
			}
		}
		debug ("----------------");		
	    return result;		
	}
	
	public Target load (int id)
	{
		Lock lock = new Lock (database, Lock.LOAD);
		SQLiteDatabase database = this.database.getReadableDatabase();
		debug ("SELECT "+fields()+" FROM " + tables() + " WHERE \"" + name + "\".\"" + primary.name + "\"=" +id);
	    Cursor cursor = database.rawQuery ("SELECT "+fields()+" FROM " + tables() + " WHERE \"" + name + "\".\"" + primary.name + "\"=" +id, null);
	    if (cursor==null) 
	    {
	    	debug ("individual load failed for id "+id);
	        database.close();
	        lock.unlock();
	    	return null;
	    }
	    
	    debug ("loaded "+id);
	    if (!cursor.moveToFirst())
    	{
	    	debug ("no load result");
	        database.close();
	        lock.unlock();
	    	return null;
    	}
	    
	    database.close();
	    lock.unlock();

		return apply (cursor, 0);
	    
	}
	
	public ArrayList<Target> load ()
	{
	    return load (query);
	}
	
	public ArrayList<Target> load (Query query)
	{
		  Lock lock = new Lock (database, Lock.LOAD);
		  SQLiteDatabase database = this.database.getReadableDatabase();
		  debug ("" + query.select + query.from + query.where + query.order + query.limit, query.debug);
		  Cursor cursor = database.rawQuery ("" + query.select + query.from + query.where + query.order + query.limit, null);
		  ArrayList<Target> result = new ArrayList<Target>();
		  if (cursor.moveToFirst())
		  {
			  do
			  {
				  result.add (apply(cursor, 0));
			  }
			  while (cursor.moveToNext());
		  }
		  database.close();
		  lock.unlock();
		  debug ("load row count is "+cursor.getCount());
		  return result;
	}
	
	public void save (ArrayList<Target> targets)
	{
	    for (Target target : targets)
        {
            save (target);
        }
	}
	
	public ContentValues read (Target target)
	{
		ContentValues values = new ContentValues ();
		
		java.lang.reflect.Field property = null;
		Object object = null;
		Entity entity = null;
		Value value = null;
		Blob blob = null;
		debug ("saving");
		debug ("----------------");
		for (Field field : fields.values()) 
		{
//			if (!field.primary)
//			{
				try 
				{
					property = target.getClass().getField(field.name);
				} 
				catch (SecurityException error) 
				{
					debug ("[error] field "+field.name+" failed while extract");
				} 
				catch (NoSuchFieldException error) 
				{
					debug ("[error] field "+field.name+" not found while extract");
				}
				
				try 
				{
					object = property.get (target);
				} 
				catch (IllegalArgumentException error) 
				{
					debug ("[error] cant get field "+field.name+" value");
				}
				catch (IllegalAccessException error) 
				{
					debug ("[error] cant access field "+field.name+" value");
				} 
				
				if (field.type==Type.INTEGER && field.foreign!=null)
				{
					entity = null;
					entity = Entity.class.cast(object);
					if (entity!=null)
					{
					    if (entity.id==null)
					    {
					        database.table(field.foreign).save (entity);
					    }
					    debug (field.name+" -> "+entity.id+" bitch");					    
						values.put ("\""+field.name+"\"", entity.id);
					}
					else
					{
					    debug (field.name+" -> null *");
						values.putNull ("\""+field.name+"\"");
					}
				}
				else if (field.value!=null)
				{
				    if (field.type==Type.BLOB)
				    {
				        blob = null;
                        blob = Blob.class.cast(object);
                        if (blob!=null)
                        {
                            debug (field.name + " ->  blob("+blob.get(field.config).length+")");
                            values.put ("\""+field.name+"\"", blob.get(field.config));
                        }
                        else
                        {
                            debug (field.name + " ->  blob(null)");
                            values.putNull ("\""+field.name+"\"");
                        }
				    }
				    else
				    {
    					value = null;
    					value = Value.class.cast(object);
    					if (value!=null)
    					{
    					    debug (field.name + " ->  value(x)");
    						values.put ("\""+field.name+"\"", value.get());
    					}
    					else
    					{
    					    debug (field.name + " ->  value(null)");
    						values.putNull ("\""+field.name+"\"");
    					}
				    }
				}
				else if (field.type==Type.INTEGER)
				{
				    debug (field.name + " ->  "+(Integer)object);
					values.put ("\""+field.name+"\"", (Integer)object);
				}
				else if (field.type==Type.BOOLEAN)
				{
				    if (object==null)
				    {
				        debug (field.name + " ->  null");
				        values.putNull ("\""+field.name+"\"");
				    }
				    else if ((Boolean)object==true)
					{
				        debug (field.name + " ->  true");
						values.put ("\""+field.name+"\"", 1);
					}
					else
					{
					    debug (field.name + " ->  false");
						values.put ("\""+field.name+"\"", 0);
					}
				}				
				else if (field.type==Type.FLOAT)
				{
				    debug (field.name + " ->  "+(Float)object);
					values.put ("\""+field.name+"\"", (Float)object);
				}				
				else
				{
				    debug (field.name + " ->  "+(String)object);
					values.put ("\""+field.name+"\"", (String)object);
				}
//			}
		}
		debug ("----------------");		
		return values;
	}
	
	public boolean add (Target target)
	{
		int result = 0;
		ContentValues values = read (target);
		Lock lock = new Lock (database, Lock.SAVE);
        SQLiteDatabase database = this.database.getWritableDatabase ();		
		debug ("inserting");
		result = (int) database.insert (name, null, values);
        database.close();
        lock.unlock ();
		if (result>0)
		{
			debug ("inserted "+result);
			target.id = result;
			//target = null;
			//target = load (result);
			debug ("inserted with id "+target.id);
			//target.id = result;
			if (target==null)
			{
				debug ("failed to load after insert");
			}
			return true;
		}
		return false;
	}

	public boolean save (Target target)
	{
		
		int result = 0;

		ContentValues values = read (target);
		values.remove ("id");

		Lock lock = new Lock (database, Lock.SAVE);
        SQLiteDatabase database = this.database.getWritableDatabase ();		
		if (target.id==null)
		{
			debug ("inserting");
			result = (int) database.insert (name, null, values);
            database.close();
            lock.unlock ();
			if (result>0)
			{
				debug ("inserted "+result);
				target.id = result;
				//target = null;
				//target = load (result);
				debug ("inserted with id "+target.id);
				//target.id = result;
				if (target==null)
				{
					debug ("failed to load after insert");
				}
				return true;
			}
		}
		else
		{
			debug ("saving");
			result = database.update (name, values, primary.name + " = ?", new String[] {String.valueOf(target.id)});
            database.close();
            lock.unlock ();
			if (result>0)
			{
				debug ("saved "+result);
				target = load (target.id);
				if (target!=null)
				{
					debug ("loaded after update "+target.id);
				}
				else
				{
					debug ("failed to load after update");
				}
				
				return true;				
			}
		}
		return false;
	}

    public boolean delete (Target target) 
    {
    	Lock lock = new Lock(database, Lock.SAVE);
        SQLiteDatabase database = this.database.getWritableDatabase();
        if (database.delete (name, primary.name + " = ?", new String[] {String.valueOf(target.id)})>0)
        {
        	database.close();
        	lock.unlock();
        	return true;
        }
        database.close();
        lock.unlock();
        return false;
    }
    
    public void clear ()
    {
    	delete (query);
    }

    public void delete (Query query)
    {
          Lock lock = new Lock (database, Lock.SAVE);
          SQLiteDatabase database = this.database.getWritableDatabase();
          debug ("DELETE FROM " + "\""+name+"\"" + " WHERE "+"\""+primary.name+"\""+" IN (SELECT "+"\""+name+"\".\""+primary.name+"\""+query.from + query.where + query.order + query.limit+")", true);
          //database.execSQL ("DELETE FROM " + name + query.where + query.order + query.limit);
          database.execSQL ("DELETE FROM " + "\""+name+"\"" + " WHERE "+"\""+primary.name+"\""+" IN (SELECT "+"\""+name+"\".\""+primary.name+"\""+query.from + query.where + query.order + query.limit+")");
          database.close();
          lock.unlock();
    }

    public void update (Query query)
    {
          Lock lock = new Lock (database, Lock.SAVE);
          SQLiteDatabase database = this.database.getWritableDatabase();
          debug ("UPDATE " + "\""+name+"\"" + "SET "+query.set+" WHERE "+"\""+primary.name+"\""+" IN (SELECT "+"\""+name+"\".\""+primary.name+"\""+query.from + query.where + query.order + query.limit+")", true);
          //database.execSQL ("DELETE FROM " + name + query.where + query.order + query.limit);
          database.execSQL ("UPDATE " + "\""+name+"\" " + "SET "+query.set+" WHERE "+"\""+primary.name+"\""+" IN (SELECT "+"\""+name+"\".\""+primary.name+"\""+query.from + query.where + query.order + query.limit+")");
          database.close();
          lock.unlock();
    }    
    
    public int count ()
    {
    	int result = 0;
    	Lock lock = new Lock(database, Lock.LOAD);
    	SQLiteDatabase database = this.database.getReadableDatabase();
    	Cursor cursor = database.rawQuery ("SELECT COUNT(*) FROM " + name, null);
    	if (cursor.moveToFirst())
    	{
    		result = cursor.getInt(0);
        	debug ("count is " + cursor.getInt(0));
    	}
		database.close();
		lock.unlock();
		return result;
    }
    
    public ArrayList<Target> of (Entity object)
    {
        return of (object, null);
    }
    
    public ArrayList<Target> of (Entity object, Query query)
    {
        debug ("******* preparing "+ name+ "s OF for " + object.getClass().getName());
        java.lang.reflect.Field property;
        for (Field field: fields.values())
        {
            if (field.foreign==object.getClass())
            {
                property = null;
                
                try
                {
                    property = object.getClass().getField(name);
                } 
                catch (SecurityException e)
                {
                } 
                catch (NoSuchFieldException e)
                {
                }
                if (query==null)
                {
                    query = new Query(this);
                }
                query.where.query = "\""+name+"\".\""+field.name+"\"="+object;
                if (property!=null)
                {
                    debug ("object id is "+object);
                    try
                    {
                        property.set(object, load(query));
                    } 
                    catch (IllegalArgumentException e)
                    {
                        e.printStackTrace();
                    } 
                    catch (IllegalAccessException e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    debug ("for "+object.getClass().getName()+" we did not find "+name+" field");
                    return load(query);
                }
            }
        }
        return null;
    }
    

    public boolean exists (Integer id)
    {
		  boolean result = false;
		  Lock lock = new Lock (database, Lock.SAVE);
		  SQLiteDatabase database = this.database.getReadableDatabase();
   		  debug ("SELECT count(*) FROM " + tables() + " WHERE \"" + name + "\".\"" + primary.name + "\"=" +id);
		  Cursor cursor = database.rawQuery ("SELECT count(*) FROM " + tables() + " WHERE \"" + name + "\".\"" + primary.name + "\"=" +id, null);
		  if (cursor.moveToFirst())
		  {
			  try
			  {
				  if (cursor.getInt(0)>0)
				  {
					  result = true;
				  }
			  }
			  catch (Exception e) 
			  {
				  
			  }
		  }
          debug ("load row count is "+cursor.getCount());		  
		  database.close();
		  lock.unlock();
		  return result;
    }    
    
    public boolean exists (Query query)
    {
		  boolean result = false;
		  Lock lock = new Lock (database, Lock.SAVE);
		  SQLiteDatabase database = this.database.getReadableDatabase();
		  debug ("select count(*)" + query.from + query.where);
		  Cursor cursor = database.rawQuery ("select count(*)" + query.from + query.where, null);
		  try
		  {
    		  if (cursor.moveToFirst())
    		  {
    			  try
    			  {
    				  if (cursor.getInt(0)>0)
    				  {
    					  result = true;
    				  }
    			  }
    			  catch (Exception e) 
    			  {
    				  System.out.println("error on exist while get value");
    			  }
    		  }
		  }
		  catch (Exception e)
		  {
		      System.out.println("error on exist while cursor move");
		  }
          //debug ("load row count is "+cursor.getCount())
		  database.close();
		  lock.unlock();
		  return result;
    }
    
    public void debug (String message, boolean force)
    {
        if (force || debug)
        {
            System.out.println ("db."+database.name+"."+name+": "+message);
        }
    }
    
    public void debug (String message)
    {
    	if (debug)
    	{
    		System.out.println ("db."+database.name+"."+name+": "+message);
    	}
    }
    
    public String tables ()
    {
    	if (build.tables==null)
    	{
	    	String result = "\""+name+"\"";
	    	Table foreign;
	    	for (Field field : fields.values()) 
	    	{
	    		if (field.foreign!=null && database.table(field.foreign)!=null)
	    		{
	    			foreign = database.table(field.foreign);
	    			result += " left join \"" + foreign.name + "\" on \"" + foreign.name + "\".\"" + foreign.primary.name + "\" = \"" + name + "\".\"" +field.name+"\"";
	    		}
			}
	    	build.tables = result;
    	}
    	return build.tables;
    }
    
    public String fields ()
    {
    	if (build.fields==null)
    	{
	    	String result = "";
	    	Table foreign;
	    	for (Field field : fields.values()) 
	    	{
	    		result += "\""+name + "\".\"" +field.name + "\",";
			}
	    	for (Field field : fields.values()) 
	    	{
	    		if (field.foreign!=null && database.table(field.foreign)!=null)
	    		{
	    			foreign = database.table(field.foreign);
	    			Map <String, Field> subjects = foreign.fields;
	    			for (Field subject : subjects.values()) 
	    	    	{
	    	    		result += "\""+foreign.name + "\".\"" +subject.name + "\",";
	    			}
	    		}
			}
	    	build.fields = Text.beforeLast (",", result);
    	}
    	return build.fields;    	
    }
    
    private int position (Field field)
    {
    	if (build.positions==null)
    	{
    		build.positions = new HashMap<String, Integer>();
    		int position = fields.size();
//            for (Field field : fields.values()) 
//            {
//                result += "\""+name + "\".\"" +field.name + "\",";
//            }
    		
        	for (Field subject : fields.values()) 
        	{
        		if (subject.foreign!=null)
        		{
        			if (database.table(subject.foreign)!=null)
        			{
	        			build.positions.put(subject.name, position);
	        			position += database.table(subject.foreign).fields.size();
        			}
        			else
        			{
        			    debug (subject.foreign.getClass().getName() + " class not found in database");
        			}
        		}
        	}
    	}
    	//debug ("getting position for field " + field.name);
    	return build.positions.get(field.name);
    }
    
    private boolean equals (java.lang.reflect.Field field, Class<?> target)
    {
        if (field.getType().isAssignableFrom(target))
        {
            debug ("* "+field.getType().getName()+"=="+target.getName());
            return true;
        }
        return false;
    }
    
    private boolean inherits (java.lang.reflect.Field field, Class<?> parent)
    {
        try
        {
            if (!field.getType().getSuperclass().getName().equalsIgnoreCase ("java.lang.Object") && field.getType().getSuperclass().isAssignableFrom(parent))
            {
                debug ("* "+field.getType().getSuperclass().getName()+"=="+parent.getName());
                return true;
            }
        }
        catch (NullPointerException e)
        {
            
        }
        return false;
    }
    
    private boolean includes (Class<?> value, Class<?> parent)
    {
        for (Class<?> component: value.getInterfaces()) 
        {
            if (component.equals(parent)) 
            {
                return true;
            }
        }
        return false;
    }    
    
    public Target instance ()
    {
        Target result = null;
        try 
        {
            result = (Target) target.newInstance();
        }
        catch (IllegalAccessException error) 
        {
            error.printStackTrace();
        } 
        catch (InstantiationException error) 
        {
            error.printStackTrace();
        }
        result.database (database);
        return result;
    }
    
    public Target instance (Integer id)
    {
        Target result = null;
        try 
        {
            result = (Target) target.newInstance();
        }
        catch (IllegalAccessException error) 
        {
            error.printStackTrace();
        } 
        catch (InstantiationException error) 
        {
            error.printStackTrace();
        }
        result.database (database);
        result.id = id;
        return result;
    }    
    
   

}
