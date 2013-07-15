package hazardland.lib.db;

public class Set
{
    public String query;
    public String toString ()
    {
        if (query!=null && query!="")
        {
            return " " + query + " ";
        }
        return "";
    }
}
