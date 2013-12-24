package hazardland.lib.db;

public class Where
{
    public String string;
    public String toString ()
    {
        if (string!=null && string!="")
        {
            return " WHERE " + string + " ";
        }
        return "";
    }
}
