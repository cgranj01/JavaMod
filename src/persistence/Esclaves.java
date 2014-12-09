package persistence;

import modbus.ConnexionModbusTCP;

public class Esclaves
{
	private long idslave;
	private String name;
	private String ipAdress;
	private int port;
	private int timeout;
	
	public Esclaves ()
	{
		
	}
	
	
	
	public long getIdslave ()
	{
		return this.idslave;
	}
	public void setIdslave (long idslave)
	{
		this.idslave = idslave;
	}
	public String getName ()
	{
		return name;
	}
	public void setName (String name)
	{
		this.name = name;
	}
	public String getIpAdress ()
	{
		return ipAdress;
	}
	public void setIpAdress (String ipAdress)
	{
		this.ipAdress = ipAdress;
	}
	public int getPort ()
	{
		return port;
	}
	public void setPort (int port)
	{
		this.port = port;
	}
	public int getTimeout ()
	{
		return timeout;
	}
	public void setTimeout (int timeout)
	{
		this.timeout = timeout;
	}
	
	public ConnexionModbusTCP connexionModbusTCP ()
	{
		return new ConnexionModbusTCP (this.ipAdress, this.port, this.timeout);
	}

}
