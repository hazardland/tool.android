package hazardland.lib.db;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Image extends Blob implements Trash
{
    public Config.Image config;
    private byte[] data;
    public Bitmap value;
    private int size = 0;
    private boolean empty = true;
	public Image ()
	{
	    //options.inJustDecodeBounds = true;
	}
	public boolean empty ()
	{
	    return empty;
	}
	public boolean set ()
	{
	    if (value==null && data!=null)
	    {
	        try
	        {
	            value = BitmapFactory.decodeByteArray(data, 0, data.length, config.read); //decodeStream(new ByteArrayInputStream(data), null, config.read);
	            data = null;
	        }
	        catch (OutOfMemoryError error)
	        {
	            return true;
	        }
	        return true;
	    }
	    return false;
	}
	@Override
	public void set (byte[] value, Config config)
	{
	    this.config = config.image;
	    size = value.length;
	    if (value.length>0)
	    {
	        empty = false;
	    }
	    if (value!=null && value.length>0)
	    {
	        if (config.image.decode)
	        {
	            try
	            {
	                this.value = BitmapFactory.decodeByteArray(value, 0, value.length, config.image.read);//decodeStream(new ByteArrayInputStream(value), null, config.image.read);
	            }
	            catch (OutOfMemoryError error)
	            {
	                return;
	            }
                data = null;
	        }
	        else
	        {
                data = value;
	        }
	    }
	    else
	    {
	        //debug("image: loading bitmap empty");
	    }
	}
	@Override
	public byte[] get (Config config)
	{
	    if (data!=null && value==null)
	    {
	        return data;
	    }
        //debug("image: saving bitmap");    
	    ByteArrayOutputStream result = new ByteArrayOutputStream();
	    if (value!=null && value.compress(config.image.write.format, config.image.write.quality, result))
	    {
	        return result.toByteArray();
	    }
        return new byte[] {};
	}
    public int clean()
    {
        if (value!=null)
        {
            value.recycle();
            return size;
        }
        return 0;
    }

    public void crop (byte[] value, int width, int height)
    {
    	crop (value, -1, -1, width, height);
    }    
    
    private static int sample (float source)
    {
		int root  = (int) source;
		if (source>root)
		{
			return root+1;
		}
		return root;
    }

    private static boolean resize (float source)
    {
		int root  = (int) source;
		if (source==root)
		{
			return true;
		}
		return false;
    }

    public void crop (byte[] value, int x, int y, int width, int height)
    {
    	if (value!=null && value.length>0)
    	{
	        size = value.length;
	        //ByteArrayInputStream stream = new ByteArrayInputStream (value);
    		BitmapFactory.Options options = new BitmapFactory.Options();
    		options.inJustDecodeBounds = true;
    		try
    		{
    		    BitmapFactory.decodeByteArray (value, 0, value.length, options);
    		}
    		catch (OutOfMemoryError error)
    		{
    		    return;
    		}
    		//BitmapFactory.decodeStream (stream, null, options);
    		
    		//1. fit input size to result size
    		// if input size is bigger then result size:
    		//    then make small input before creation with samplesize using sample scale
    		//    than fit to exact size using bitmap recreate using scale and matrix
    		// if input size is smaller then result size make large input after creation
    		//    than make it bigger using scale and crop
    		
   		
    		Size input = new Size (options.outWidth, options.outHeight);
    		Size result = new Size (x+width, x+height);
    		
    		Bitmap output = null;
    		
    		options.inJustDecodeBounds = false;
    		
    		
    		if (input.width==result.width && input.height==result.height)
    		{
                //this.value = BitmapFactory.decodeStream (stream, null, options);
                this.value = BitmapFactory.decodeByteArray (value, 0, value.length, options);
                debug ("exact size");
                //input exactly fits output
    		}
    		else
    		{
    		    
    		    options.inSampleSize = 1;
                while (input.width/options.inSampleSize/2>=result.width && input.width/options.inSampleSize/2 >= input.height) 
                {
                    options.inSampleSize *= 2;
                }                

    		    if (options.inSampleSize>1)
    		    {
    		        String report = " reason "+input.width+">="+result.width+"x"+input.height+">="+result.height;
    		        try
    		        {
    		            output = BitmapFactory.decodeByteArray(value, 0, value.length, options);
    		        }
    		        catch (OutOfMemoryError error)
    		        {
    		            return;
    		        }
        		    input = new Size (options.outWidth, options.outHeight);
        		    debug ("affected resample "+options.inSampleSize+" reuslt "+input.width+"x"+input.height+report);
    		    }
    		    else
    		    {
    		        try
    		        {
    		            output = BitmapFactory.decodeByteArray(value, 0, value.length);
    		        }
    		        catch (OutOfMemoryError error)
    		        {
    		            return;
    		        }
    		    }

                if (output!=null)
                {
                    Size resize = null;
                    
                    resize = result.fit (input);
        		    
        		    debug ("input is "+input.width+"x"+input.height);
        		    debug ("result is "+result.width+"x"+result.height);
    		    	debug ("scale is "+resize.width/input.width +"x" + resize.height/input.height);
        		    debug ("resize is "+resize.width+"x"+resize.height);
        		    
        		    if (x==-1)
        		    {
        		        if (resize.width>input.width)
        		        {
        		            x = (int) ((resize.width-input.width)/2);
        		        }
        		        else
        		        {
        		            x = (int) ((input.width-resize.width)/2);            		            
        		        }
        		    	
        		    }
        		    else if (x>0)
        		    {
        		    	x = (int)input.x(result.width,x);
        		    }
        		    
        		    if (x>0 && x+resize.width>input.width)
        		    {
        		    	x = 0;
        		    }
        		    if (x<0)
        		    {
        		        x = 0;
        		    }
        		    
        		    if (y==-1)
        		    {
        		        if (resize.height>input.height)
        		        {
        		            y = (int) ((resize.height-input.height)/2);
        		        }
        		        else
        		        {
        		            y = (int) ((input.height-resize.height)/2);    
        		        }
        		    	
        		    }
        		    else if (y>0)
        		    {
        		    	y = (int)input.y(result.height,y);
        		    }

        		    if (y>0 && y+resize.height>input.height)
        		    {
        		    	y = 0;
        		    }
        		    if (y<0)
        		    {
        		        y = 0;
        		    }
        		    
        		    debug ("crop is "+x +"-"+(int)resize.width+"x"+ y+"-"+(int)resize.height);
        		    
        		    try
        		    {
            		    Bitmap crop = Bitmap.createBitmap (output, x, y, (int)resize.width, (int)resize.height);
            		    //output.recycle();
            		    this.value = Bitmap.createScaledBitmap (crop, width, height, false);
            		    //crop.recycle();
        		    }
        		    catch (OutOfMemoryError error)
        		    {
        		        return;
        		    }
                }
    		}
    	}
    	else
    	{
    	    debug ("skipping set");
    	}
    	
    }

    public void debug (String message)
    {
        //System.out.println  ("image:crop: "+message);            
    }
    
    private class Size
    {
        float width;
        float height;
        public Size (float width, float height)
        {
            this.width = width;
            this.height = height;
        }
        
        public Size large (Size target)
        {
            Size result = new Size (this.width, this.height);
            float scale;
            if (result.width<target.width)
            {
            	scale = target.width/result.width;
                result.width = result.width * scale;
                result.height = result.height * scale;
                debug ("larger by width "+result.width/scale+"->"+result.width+"x"+result.height/scale+"->"+result.height);                
            }
            else
            {
            	debug("larger width affect skipped");
            }
            
            if (result.height<target.height)
            {
            	scale = target.height/result.height;
                result.width = result.width * scale;
                result.height = result.height * scale;
                debug ("larger by height "+result.width/scale+"->"+result.width+"x"+result.height/scale+"->"+result.height);                
            }
            else
            {
            	debug("larger height affect skipped");
            }
            return result;
        }

        public Size fit (Size target)
        {
            Size result = new Size (this.width, this.height);
            float scale;
            if (result.width<target.width && result.height*(target.width/result.width)<target.height)
            {
                scale = target.width/result.width;
                result.width = result.width * scale;
                result.height = result.height * scale;
                debug ("larger by width "+result.width/scale+"->"+result.width+"x"+result.height/scale+"->"+result.height);                
            }
            else if (result.width>target.width)
            {
                scale = result.width/target.width;
                result.width = result.width / scale;
                result.height = result.height / scale;
                debug ("smaller by width "+result.width/scale+"->"+result.width+"x"+result.height/scale+"->"+result.height);
            }            
            else
            {
                debug("width affect skipped");
            }
            
            if (result.height<target.height && result.width*(target.height/result.height)<target.width)
            {
                scale = target.height/result.height;
                result.width = result.width * scale;
                result.height = result.height * scale;
                debug ("larger by height "+result.width/scale+"->"+result.width+"x"+result.height/scale+"->"+result.height);                
            }
            else if (result.height>target.height)
            {
                scale = result.height/target.height;
                result.width = result.width / scale;
                result.height = result.height / scale;
                debug ("smaller by height "+result.width/scale+"->"+result.width+"x"+result.height/scale+"->"+result.height);
            }            
            else
            {
                debug("height affect skipped");
            }
            return result;
        }
        
        public void debug (String message)
        {
            //System.out.println ("image:crop: "+message);            
        }

        public Size small (Size target)
        {
            Size result = new Size (this.width, this.height);
            float scale;
            if (result.width>target.width)
            {
            	scale = result.width/target.width;
                result.width = result.width / scale;
                result.height = result.height / scale;
                debug ("smaller by width "+result.width/scale+"->"+result.width+"x"+result.height/scale+"->"+result.height);
            }
            else
            {
            	debug("width affect skipped");
            }
            
            if (result.height>target.height)
            {
            	scale = result.height/target.height;
                result.width = result.width / scale;
                result.height = result.height / scale;
                debug ("smaller by height "+result.width/scale+"->"+result.width+"x"+result.height/scale+"->"+result.height);
            }
            else
            {
            	debug("smaller height affect skipped");
            }
            return result;
        }        
        
        public Sample sample (Size target)
        {
            return new Sample (large(target).width/width);
        }
        
        public float x (float width, float x)
        {
        	return (this.width/width)*x;
        }
        
        public float y (float height, float y)
        {
        	return (this.height/height)*y;
        }        
    }
    
    private class Sample
    {
        boolean resize;
        int value;
        public Sample (float source)
        {
            this.value = sample (source);
            this.resize = resize (source);
        }
    }
}
