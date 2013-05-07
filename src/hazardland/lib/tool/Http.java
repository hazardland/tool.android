package hazardland.lib.tool;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Http
{
    HttpURLConnection client;
    public Error error;
    
    public Http ()
    {

    }
    
    public boolean connect (String link)
    {
        boolean result = true;
        URL url = null;
        try
        {
            url = new URL(link);
        } 
        catch (MalformedURLException e)
        {
            debug ("bad url");
            result = false;
        }
        try
        {
            client = (HttpURLConnection) url.openConnection();
        } 
        catch (IOException e)
        {
            debug ("no connection");
            result = false;
        }
        if (result)
        {
            client.setUseCaches (false);
        }
        return result;
    }

    public String text (String link)
    {
        if (connect(link))
        {
            debug ("connected");
            BufferedReader reader = null;
            InputStream stream = null;
            try
            {
                stream = client.getInputStream();
            } 
            catch (IOException e1)
            {
                finish ();
                debug ("bad stream");            
            }
            try
            {
                reader = new BufferedReader(new InputStreamReader(stream));
            }
            catch (NullPointerException e)
            {
                
            }
            StringBuilder result = new StringBuilder();
            String line;
            try
            {
                while ((line = reader.readLine())!=null) 
                {
                    result.append(line);
                }
            }
            catch (NullPointerException e)
            {
                debug ("no internet connection");
            }            
            catch (IOException e)
            {
                debug ("error while readering result");
                finish ();
            }
            catch (OutOfMemoryError e)
            {
                debug ("out of memory !");
                finish ();
            }
            finally
            {
                if (stream!=null)
                {
                    try
                    {
                        stream.close();
                    } 
                    catch (IOException e1)
                    {
                        
                    }
                }                
                finish();
            }
            debug ("returning");
            try
            {
                return result.toString();
            }
            catch (OutOfMemoryError e)
            {
                debug ("out of memory !");
            }
            finally
            {
                if (stream!=null)
                {
                    try
                    {
                        stream.close();
                    } 
                    catch (IOException e1)
                    {
                        
                    }
                }
                finish ();
            }
        }
        
        return null;
    }
    
    public void finish ()
    {
        debug ("disconnected");
        client.disconnect();
    }
    
    public byte[] data (String link)
    {
        if (connect(link))
        {
            debug ("connected");
            InputStream stream = null;
            try
            {
                stream = client.getInputStream();
            } 
            catch (IOException e1)
            {
                finish ();
                debug ("bad stream");
            }
            byte[] buffer = new byte[1024];
            int read = 0;
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            try
            {
                while ((read=stream.read(buffer))!=-1) 
                {
                    result.write(buffer, 0, read);
                }
            }
            catch (NullPointerException e)
            {
                debug ("no internet connection");
            }
            catch (IOException e)
            {
                debug ("error while readering result");
            }
            catch (OutOfMemoryError e)
            {
                debug ("out of memory !");
            }
            finally
            {
                if (stream!=null)
                {
                    try
                    {
                        stream.close();
                    } 
                    catch (IOException e1)
                    {
                        
                    }
                }
                finish();
            }
            debug ("returning");
            return result.toByteArray();
        }
        return null;
    }
    
    public void debug (String message)
    {
        //System.out.println ("http: "+message);
    }
}
