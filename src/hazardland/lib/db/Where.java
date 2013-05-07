package hazardland.lib.db;

public class Where
{
    public String query;
    public String toString ()
    {
        if (query!=null && query!="")
        {
            return " WHERE " + query + " ";
        }
        return "";
    }
}
