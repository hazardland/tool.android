package hazardland.lib.db;

public class Blob
{
    //public Config config;
	public Blob ()
	{
		
	}
	public void set (byte[] value, Config config)
	{
		System.out.println("blob: set parent call");
	}
	public byte[] get (Config config)
	{
	    System.out.println("blob: get parent call");
		return new byte[] {};
	}

}
