package hazardland.lib.tool;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.content.Context;

public class File
{
	public static boolean write (Context context, String name, String content)
	{
        FileOutputStream stream = null;
		try
		{
			stream = context.openFileOutput (name, Context.MODE_WORLD_READABLE);
		}
		catch (FileNotFoundException exception)
		{
			return false;
		}
        OutputStreamWriter file = new OutputStreamWriter (stream); 
		try
		{
			file.write (content);
		}
		catch (IOException exception)
		{
			return false;
		}
		try
		{
			file.flush();
		}
		catch (IOException exception)
		{
			return false;
		}
        try
		{
			file.close();
		}
		catch (IOException exception)
		{
			return false;
		}
	    return true;
	}
	
	public static String read (Context context, String name)
	{
        FileInputStream stream = null;
		try
		{
			stream = context.openFileInput (name);
		}
		catch (FileNotFoundException exception)
		{
			return "";
		}
        BufferedReader file = new BufferedReader(new InputStreamReader(stream));
        StringBuilder result = new StringBuilder();
        String line;
        try
		{
			while ((line = file.readLine()) != null) 
			{
			    result.append(line);
			}
		}
		catch (IOException exception)
		{
			return "";
		}

		return result.toString ();		
	}
	
	public static boolean delete (Context context, String name)
	{
		return context.deleteFile (name);
	}
	
	public static boolean rename (String from, String to)
	{
		java.io.File file = new java.io.File (from);
		return file.renameTo (new java.io.File (to));
	}
	
	public static String[] list (String path)
	{
		java.io.File file = new java.io.File (path);
		return file.list ();
	}
}
