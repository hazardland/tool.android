package hazardland.lib.tool;

public class Text
{
    public String content;
    public String result;
	public static String after (String from, String content)
	{
		int position = content.indexOf (from);
		if (position!=-1)
		{
			return new String(content.substring (position+from.length()));
		}
		return "";
	}
	public static String afterLast (String from, String content)
	{
		int position = content.lastIndexOf (from);
		if (position!=-1)
		{
			return new String(content.substring (position+from.length()));
		}
		return "";
	}
	public static String before (String from, String content)
	{
		int position = content.indexOf (from);
		if (position!=-1)
		{
			return new String(content.substring (0, position));
		}
		return "";
	}
	public static String beforeLast (String from, String content)
	{
		int position = content.lastIndexOf (from);
		if (position!=-1)
		{
			return new String(content.substring (0, position));
		}
		return "";
	}
	
    public static String between (String from, String to, String content)
    {
        return before (to, after (from, content));
    }
    
    public static boolean begins (String on, String content)
    {
        if (content.substring(0, on.length()).equals(on))
        {
            return true;
        }
        return false;
    }

    public static String betweenLast (String from, String to, String content)
    {
        return afterLast (from, beforeLast (to, content));
    }    
    
	public Text (String input, String from, String to)
	{
	    content = after (from, input);
	    if (empty(content))
	    {
	        content = beforeLast (to, content);
	        if (empty(content))
	        {
	            debug ("to not found");
	        }
	    }
	    else
	    {
	        debug ("from not found");
	    }
	}
	
	public Text (String input)
	{
	    content = input;
	}
	
	public static boolean empty (String string)
	{
		if (string==null || string.length()==0 || string.equals(""))
		{
			return true;
		}
		return false;
	}
	
	public boolean next (String from, String to)
	{
	    if (empty(content))
	    {
	        debug ("returning no content");
	        return false;
	    }
	    result = before (to, content);
	    if (empty(result))
	    {
	        debug ("returning to not found");
	        return false;
	    }
	    //debug ("found to ["+to+"]");
	    content = after (to, content);
	    result = after (from, result);
	    if (empty(result))
	    {
	        debug ("returning from not found");
	        return false;
	    }
	    //debug ("found from ["+from+"]");
	    return true;
	}
	
	public void debug (String message)
	{
	    //System.out.println("text: "+message);
	}
	
	public static String replaceTag (String tag)
	{
	    return "[<](/)?"+tag+"[^>]*[>]";
	}
	
	public static String replaceTagContent (String tag)
	{
	    return "<"+tag+"[^>]*?>.*?</"+tag+"[^>]*?>";
	}
	
	public static String replaceTagContentWith (String tag, String with)
	{
	    return "<"+tag+with+"[^>]*?>.*?</"+tag+"[^>]*?>";
	}
}
