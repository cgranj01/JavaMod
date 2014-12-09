package modbus;

import java.net.InetAddress;
import java.net.UnknownHostException;

import net.wimpi.modbus.net.TCPMasterConnection;

public class ConnexionModbusTCP
{
	private TCPMasterConnection				TCPMasterCon;
	
	public ConnexionModbusTCP(String aIp_slave, int aPort, int aTimeOut)
	{	
		InetAddress iad;
		try
		{
			iad = InetAddress.getByName (aIp_slave);
			this.TCPMasterCon = new TCPMasterConnection (iad);
			this.TCPMasterCon.setPort (aPort);
			this.TCPMasterCon.setTimeout (aTimeOut);
		}
		catch (UnknownHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	public TCPMasterConnection getTCPMasterCon ()
	{
		return TCPMasterCon;
	}
}
