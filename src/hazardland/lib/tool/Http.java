package hazardland.lib.tool;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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
    
    public void debug (String message)
    {
        System.out.println ("http: "+message);
    }
}
