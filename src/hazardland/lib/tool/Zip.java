package hazardland.lib.tool;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Zip
{
	ZipFile file;
	String name;
	//HashMap<String, String> list = new HashMap<String, String>();
	int check = 0;
	public Zip ()
	{

	}	
	public Zip (String name)
	{
		this.name = name;
	}
	public boolean open ()
	{
		return open ("");
	}
	public boolean open (File file)
	{
		if (file!=null)
		{
			return open (file.getAbsolutePath());
		}
		return false;
	}	
	public boolean open (String name)
	{
		check++;		
		if (name!=null && name!="")
		{
			debug ("on check "+check+" file is null");			
			this.name = name;
			close ();
		}
		if (file==null)
		{
			try
			{
				file = new ZipFile (this.name);
				debug ("opened file "+this.name);

			}
			catch (IOException e)
			{
				debug ("unable to open zip "+this.name);
			}
		}
		if (file!=null)
		{
			return true;
		}
		return false;
	}
	public ArrayList<String> list ()
	{
		return list (null);
	}
	public ArrayList<String> list (String folder)
	{
		if (open())
		{
			ArrayList<String> list = new ArrayList<String>();
			Enumeration<? extends ZipEntry> entries = file.entries();
			while (entries.hasMoreElements())
			{
				ZipEntry entry = entries.nextElement();
				if (folder==null)
				{
					list.add (entry.getName());
				}
				else if (entry.getName().startsWith(folder))
				{
					list.add (entry.getName());
				}
			}
			return list;
		}
		return null;
	}
	public boolean close ()
	{
		boolean result = false;
		if (file!=null)
		{
			try
			{
				file.close();
				//list.clear();
				result = true;
			}
			catch (IOException e)
			{
				
			}
			file = null;
		}
		else
		{
			result = true;
		}
		return result;
	}
	
	public String text (String name)
	{
		if (open())
		{
			ZipEntry entry = file.getEntry(name);
			if (entry==null)
			{
				return null;
			}
			try
			{
				InputStream stream = file.getInputStream(entry);
				BufferedReader r = new BufferedReader(new InputStreamReader(stream));
				StringBuilder result = new StringBuilder();
				String line;
				while ((line = r.readLine()) != null) 
				{
				    result.append (line);
				}
				return result.toString();
			}
			catch (IOException e)
			{
				return null;
			}
		}
		return null;
	}
	
	public byte[] bytes (String name)
	{
		if (open())
		{
			ZipEntry entry = file.getEntry(name);
			if (entry==null)
			{
				return null;
			}
			try
			{
				InputStream stream = file.getInputStream(entry);
	            byte[] buffer = new byte[1024];
	            int read = 0;
	            ByteArrayOutputStream result = new ByteArrayOutputStream();
                while ((read=stream.read(buffer))!=-1) 
                {
                    result.write(buffer, 0, read);
                }
                return result.toByteArray();

			}
			catch (IOException e)
			{
				return null;
			}
		}
		return null;
	}	
	
	public InputStream stream (String name)
	{
		if (open())
		{
			ZipEntry entry = file.getEntry(name);
			if (entry==null)
			{
				return null;
			}
			try
			{
				InputStream stream = file.getInputStream(entry);
				return stream;
			}
			catch (IOException e)
			{
				return null;
			}
		}
		return null;
	}	
	
	public void debug (String message)
	{
		System.out.println("zip: "+message);
	}
	
}
