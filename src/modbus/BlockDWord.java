
package modbus;

import net.wimpi.modbus.util.ModbusUtil;

public class BlockDWord extends BlockMot
{
	public BlockDWord (int start, int longueur, int frequence, String aFormat, String aIdSlave, ConnexionModbusTCP aCm) throws Exception
	{
		super (start, longueur, frequence, aFormat, aIdSlave, aCm);
		this.initialize ();
	}


	@Override
	public String toConvert (int keyentree, String aCoef)
	{
		int i = keyentree - this.getAdresseDebut ();
		byte [] b0 = this.getRes ().getRegister (i).toBytes ();
		byte [] b1 = this.getRes ().getRegister (i + 1).toBytes ();
		byte [] tb = {b1 [0], b1 [1], b0 [0], b0 [1]};
		if (aCoef.equals ("") || aCoef.equals ("1.0"))
		{
			String sValeur = Long.toString ((ModbusUtil.registersToInt (tb) & 0xFFFFFFFFL));
			return sValeur;
		}
		else
		{
			String sValeur = Float.toString ((ModbusUtil.registersToInt (tb) & 0xFFFFFFFFL) * Float.parseFloat (aCoef));
			return sValeur;
		}
	}

}