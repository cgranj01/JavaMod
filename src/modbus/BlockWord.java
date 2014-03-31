
package modbus;

import net.wimpi.modbus.util.ModbusUtil;

public class BlockWord extends BlockMot
{
	public BlockWord (int start, int longueur, int frequence, String aFormat, String aIdSlave, ConnexionModbusTCP aCm) throws Exception
	{
		super (start, longueur, frequence, aFormat, aIdSlave, aCm);
		this.initialize ();
	}

	@Override
	public String toConvert (int aCle, String aCoef)
	{
		int i = aCle - this.getAdresseDebut ();
		byte [] b0 = this.getRes ().getRegister (i).toBytes ();
		if (aCoef.equals ("") || aCoef.equals ("1.0"))
		{
			String sValeur = Integer.toString ((ModbusUtil.registerToShort (b0) & 0xffff));
			return sValeur;
		}
		else
		{
			String sValeur = Float.toString ((ModbusUtil.registerToShort (b0) & 0xffff) * Float.parseFloat (aCoef));
			return sValeur;
		}
	}

}