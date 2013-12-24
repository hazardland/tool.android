package hazardland.lib.project;

import hazardland.lib.db.Database;
import hazardland.lib.db.Query;
import hazardland.lib.tool.Http;
import hazardland.lib.tool.Text;

import java.net.URLEncoder;
import java.util.ArrayList;

import android.os.Build;
import android.provider.Settings;

public class Server
{
    public static final int LOUNCH = 1;
    public String link; //to submit
    public String platform = "android"; // dont change
    public String company; //manufacturer or device name
    public String device; //model of device
    public String serial; //serial of device    
    public String kernel; //sdk version
    public String system; //operating system
    public String project; //which uses this class
    public Integer release; //project release
    public String version; //project version
    public Database database;
    public Server (Database database, String link, String project, Integer release, String version)
    {
        this.database = database;
        this.link = link;
        this.company = Build.MANUFACTURER;
        this.device = Build.DEVICE;
        this.serial = Build.SERIAL;
        if (this.serial.equalsIgnoreCase("unknown"))
        {
            this.serial = Settings.Secure.ANDROID_ID;
        }
        this.kernel = String.valueOf(Build.VERSION.SDK_INT);
        this.system = Build.DISPLAY;
        this.project = project;
        this.release = release;
        this.version = version;

        debug ("model:"+Build.MODEL);
        debug ("display:"+Build.DISPLAY);
        debug ("device:"+Build.DEVICE);
        debug ("hardware:"+Build.HARDWARE);
        debug ("manufacturer:"+Build.MANUFACTURER);
        debug ("host:"+Build.HOST);
        debug ("serial:"+Build.SERIAL);
        debug ("type:"+Build.TYPE);
        
        log (LOUNCH);
    }
    public void log (int action)
    {
        Action thread = new Action (this, action);
        thread.start();        
    }
    public void lounch ()
    {
        String link = this.link;
        if (!link.endsWith("?") && !link.endsWith("&"))
        {
            link += "?";
        }
        
        link += "platform=" + encode(platform);
        link += "&company=" + encode(company);
        link += "&device=" + encode(device);
        link += "&serial=" + encode(serial);
        link += "&kernel=" + encode(kernel);        
        link += "&system=" + encode(system);
        link += "&project=" + encode(project);
        link += "&release=" + encode(release.toString());
        link += "&version=" + encode(version);
        
        String commands = "";
        if (database.table(Command.class)!=null)
        {
            Query query = new Query (database.table(Command.class));
            query.where.string = "commands.view=1";            
            ArrayList<Command> result = database.table(Command.class).load(query);
            if (result!=null && result.size()>0)
            {
                for (Command command: result)
                {
                    commands += command.hash+",";
                }
            }        
        }  
        if (!Text.empty(commands))
        {
            commands = Text.beforeLast(",", commands);
        }
        
        link += "&command=" + encode(commands);
        
        Http http = new Http();
        debug (link);
        String result = http.text (link);
        debug ("result: "+result);
        
        if (!Text.empty(result) && database.table(Command.class)!=null)
        {
            Text text = new Text(result);
            while (text.next("<command>", "</command>"))
            {
                Command command = new Command();
                try
                {
                    command.hash = Integer.valueOf(Text.between("<id>","</id>",text.result));
                }
                catch (Exception e)
                {
                    
                }
                if (command.hash!=null && !database.table(Command.class).exists(command.hash))
                {
                    command.insert.set (Text.between("<insert>", "</insert>", text.result));
                    command.data = Text.between("<data>", "</data>", text.result);
                    command.text = Text.between("<text>", "</text>", text.result);
                    command.type = Text.between("<type>", "</type>", text.result);
                    database.table(Command.class).save(command);
                }
            }
            
        }
        
    }
    public String encode (String input)
    {
        return URLEncoder.encode(input);
    }
    public void debug (String message)
    {
        System.out.println("server: "+message);
    }
    public static class Action extends Thread  
    {
        Server server;
        int action;
        public Action (Server server, int action)
        {
            this.action = action;
            this.server = server;
        }
        @Override
        public void run() 
        {
            if (action==LOUNCH)
            {
                server.lounch ();
            }
        }
    }
    
    public Command command ()
    {
        Command command = null;
        if (database.table(Command.class)!=null)
        {
            Query query = new Query (database.table(Command.class));
            query.where.string = "commands.view=0";
            query.limit.count = 1;
            ArrayList<Command> commands = database.table(Command.class).load(query);
            if (commands!=null && commands.size()>0)
            {
                command = commands.get(0);
            }        
        }
        return command;
    }
    
}
