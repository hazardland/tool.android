package hazardland.lib.db;

import hazardland.lib.tool.Text;

import java.util.ArrayList;

public class Order 
{
	ArrayList<Order> items;
	public Table table;
	public String separator = ",";
	public String prefix = "ORDER BY";
	public String postfix = "";
	public Field field;
	public Method method;
	public Order (Table table)
	{
	    this.table = table;
		method = new Method ();
	}
	public Order (Table table, Field field)
	{
	    this.table = table;
		this.field = field;
	}
	public Order (Table table, Field field, Method method)
	{
	    this.table = table;
		this.field = field;
		this.method = method;
	}
	public Order add (Order order)
	{
		if (items==null)
		{
			items = new ArrayList<Order>();
		}
		items.add (order);
		return this;
	}
	public String field ()
	{
		if (field!=null)
		{
			return "\""+table.name +"\".\""+field.name+"\"";
		}
		return "\""+table.name +"\".\"id\"";
	}
	public String method ()
	{
		return method.name;
	}
	
	public String toString ()
	{
		if (items!=null)
		{
			String result = prefix + " ";
			for (Order item : items) 
			{
				result += Text.after(prefix+" ",item.toString()) + separator;
			}
			return " " + Text.beforeLast(separator,result) + " " + postfix + " ";
		}
		return " " + prefix + " " + field() + " " + method() + " ";
	}
}
