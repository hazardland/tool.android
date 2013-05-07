package hazardland.lib.db;

public class Query 
{
    public Select select;
    public From from;
    public Where where;
    public Set set;
    public Order order;
    public Limit limit;
    public boolean debug = false;
	public Query (Table table)
	{
	    this.select = new Select(table);
        this.from = new From(table);
	    this.order = new Order(table);
	    this.limit = new Limit();
	    this.where = new Where();
	    this.set = new Set();
	}
	private class Select
	{
	    Table table;
	    public Select (Table table)
	    {
	        this.table = table;
	    }
        public String toString ()
	    {
	        return "SELECT " + table.fields() + " ";
	    }
	}
    private class From
    {
        Table table;
        public From (Table table)
        {
            this.table = table;
        }
        public String toString ()
        {
            return " FROM " + table.tables() + " ";
        }
    }	
}
