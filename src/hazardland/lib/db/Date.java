package hazardland.lib.db;

import hazardland.lib.tool.Text;

public class Date extends Value
{
    String value = "";
    public String topic = "";
    public String source = "";
    
    public void set (String value)
    {
        if (!Text.empty(value) && !Text.empty(value.trim()))
        {
            String input = value.trim ();
            input = input.replace ("/", "");
            input = input.replace (".", "-");
            input = input.replace (",", "");
            input = input.replace ("  ", " ");
            debug ("input is "+input);
            if (!Text.empty(Text.before(" ",input)))
            {
                String first = Text.before(" ",input);
                String last = Text.after(" ",input);
                String date, time;
                if (first.contains(":"))
                {
                    time = time (first);
                    date = date (last);
                }
                else
                {
                    date = date (first);
                    time = time (last);             
                }
                this.value = date(date) + " " + time(time);
                debug ("value is "+this.value);
            }
            else
            {
                if (input.contains(":"))
                {
                    this.value = "0000-00-00 " + time (input);
                    debug ("value is "+this.value);
                }
                else
                {
                    this.value = date (input) + " 00:00:00";
                    debug ("value is "+this.value);
                }
            }
        }
        else
        {
            debug ("invalid input");
        }
    }
    public String get ()
    {
        debug ("returning "+value);
        return value;
    }
    private String time (String input)
    {
        input = input.trim();
        if (!Text.empty(input))
        {
            String hour = Text.before(":", input);
            String minute = Text.after(":", input);
            String second = "00";
            if (!Text.empty(Text.after(":", minute)))
            {
                second = Text.after(":", minute);
                minute = Text.before(":", minute);
            }
            return hour+":"+minute+":"+second;
        }
        return "00:00:00";
    }
    private String date (String input)
    {
        input = input.trim();
        if (!Text.empty(input))
        {
            String year = "0000";
            String month = "00";
            String day = "00";
            if (Text.before("-",input).length()==4)
            {
                year = Text.before("-",input);
                month = Text.after("-", input);
                if (!Text.empty(Text.after("-",month)))
                {
                    day = Text.after("-", month);
                    month = Text.before("-", month);
                }
            }
            else
            {
                day = Text.before("-",input);
                month = Text.after("-", input);
                if (!Text.empty(Text.after("-",month)))
                {
                    year = Text.after("-", month);
                    month = Text.before("-", month);
                }
            }
            return year+"-"+month+"-"+day;
//            if (Integer.parseInt(month)>12)
//            {
//                return year+"-"+day+"-"+month;
//            }
//            else
//            {
//                return year+"-"+month+"-"+day;
//            }
        }
        return "0000-00-00";
    }
    public boolean empty ()
    {
        if (Text.empty(value) || value.equalsIgnoreCase("0000-00-00 00:00:00") || value.equalsIgnoreCase("0000-00-00"))
        {
            debug ("declaring as emtpy "+value);
            return true;
        }
        return false;
    }
    public void debug (String message)
    {
        //System.out.println("date: " + message + " ("+source+": "+topic+")");
    }

}
