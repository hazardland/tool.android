package hazardland.lib.tool;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Http
{
    URLConnection client;
    public Error error;
    
    public Http ()
    {
		SSLContext context = null;
		try
		{
			context = SSLContext.getInstance("TLS");
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		try
		{
			context.init(null, new TrustManager[] { new X509TrustManager()
			{
				public void checkClientTrusted(X509Certificate[] chain, String authType)
				{
				}

				public void checkServerTrusted(X509Certificate[] chain, String authType)
				{
				}

				public X509Certificate[] getAcceptedIssuers()
				{
					return new X509Certificate[] {};
				}
			} }, null);
		}
		catch (KeyManagementException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());

		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier()
		{
			public boolean verify(String hostname, SSLSession session)
			{
				return true;
			}
		});
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
            client = url.openConnection();
            client.setRequestProperty("Accept-Charset", "UTF-8");
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
            debug ("connected to "+link);
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
                e1.printStackTrace();
                return null;
            }
            try
            {
                reader = new BufferedReader(new InputStreamReader(stream));
            }
            catch (NullPointerException e)
            {
            	debug ("buffered reader error");
            }
            StringBuilder result = new StringBuilder();
            String line;
            try
            {
                while ((line = reader.readLine())!=null) 
                {
                    result.append(line+"\n");
                }
            }
            catch (NullPointerException e)
            {
                debug ("no internet connection");
                finish();
                return null;
            }            
            catch (IOException e)
            {
                debug ("error while readering result");
                finish ();
                return null;
            }
            catch (OutOfMemoryError e)
            {
                debug ("out of memory !");
                finish ();
                return null;
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
                return null;
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
        //debug ("disconnected");
        //client.disconnect();
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
                return null;
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
                return null;
            }
            catch (IOException e)
            {
                debug ("error while readering result");
                return null;
            }
            catch (OutOfMemoryError e)
            {
                debug ("out of memory !");
                return null;
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
    
    public boolean save (String link, java.io.File file)
    {
    	return save (link, file, null); 
    }
    
    public boolean save (String link, java.io.File file, Progress progress)
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
                System.out.println("no internet connection while file download");                
                return false;
            }
            byte[] buffer = new byte[1024];
            int read = 0;
            int length = -1;
            int position = 0;
            int percent = 0;
            if (progress!=null)
            {
            	length = client.getContentLength();
            	if (length==-1 && client.getHeaderField("X-Size")!=null)
            	{
            		try
					{
            			length = Integer.parseInt(client.getHeaderField("X-Size"));						
					}
					catch (NumberFormatException e)
					{
						length = -1;
					}
            	}
//            	Map headerss = client.getHeaderFields();
//            	Set headers = headerss.entrySet(); 
//            	 for(Iterator i = headers.iterator(); i.hasNext();)
//            	 { 
//            		  Map.Entry map = (Map.Entry)i.next();
//            		  System.out.println(map.getKey() + " : " + map.getValue() + "");
//            	 }
            	debug("length is "+client.getContentLength()+" vs alternate "+client.getHeaderField("X-Size"));
            }
            if (!file.exists())
            {
            	try
				{
					file.createNewFile();
				}
				catch (IOException e)
				{
					debug ("can not create new file");
				}
            }
            else
            {
            	file.delete();
            	try
				{
					file.createNewFile();
				}
				catch (IOException e)
				{
					debug ("can not create new file");
					return false;
				}
            }
            FileOutputStream result = null;
			try
			{
				result = new FileOutputStream (file);
			}
			catch (FileNotFoundException e2)
			{
				debug ("file not found");
				return false;
			}
            try
            {
                while ((read=stream.read(buffer))!=-1) 
                {
                	if (length>0 && progress!=null)
                	{
                		position += read;
                		//debug ("progress "+(((double)position/length)*100)+" vs read "+read+" vs position "+position+" vs length "+length+" division "+(position/length));
                		if ((int)(((double)position/length)*100)>percent)
                		{
                			percent = (int)(((double)position/length)*100);
                			debug ("progress "+percent);
                			progress.progress(percent);
                		}
                	}
                	//debug ("writing "+read+" bytes");
                    result.write(buffer, 0, read);
                }
                if (progress!=null)
                {
                	progress.progress(100);
                }
            }
            catch (NullPointerException e)
            {
                debug ("no internet connection");
                return false;
            }
            catch (IOException e)
            {
                debug ("error while readering result");
                return false;
            }
            catch (OutOfMemoryError e)
            {
                debug ("out of memory !");
                return false;
            }
            finally
            {
                if (stream!=null)
                {
                    try
                    {
                        stream.close();
                        result.flush();
                        result.close();
                    } 
                    catch (IOException e1)
                    {
                        
                    }
                }
                finish();
            }
            debug ("returning");
            return true;
        }
        return false;
    }    
    public void debug (String message)
    {
        System.out.println ("http: "+message);
    }
    
    public interface Progress
    {
    	public void progress (int value);
    }
}
