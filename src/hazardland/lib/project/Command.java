package hazardland.lib.project;

import hazardland.lib.db.Date;
import hazardland.lib.db.Entity;

public class Command extends Entity
{
    public Integer hash;
    public Date insert = new Date();
    public String type;
    public String data;
    public String text;
    public Boolean view = false;
    public Command ()
    {
        
    }
    

    
}
