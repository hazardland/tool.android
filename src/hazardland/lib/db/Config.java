package hazardland.lib.db;

import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;

public class Config
{
    public Image image = new Image();
    public Config ()
    {
        
    }
    public class Image
    {
        public boolean decode = true;
        public BitmapFactory.Options read = new BitmapFactory.Options();
        public Write write = new Write();
        public Image ()
        {
            
        }
        public class Write
        {
            public CompressFormat format = CompressFormat.PNG;
            public int quality = 100;
            public Write ()
            {
                
            }
        }
    }    
}
