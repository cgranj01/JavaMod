
package modbus;

import net.wimpi.modbus.util.ModbusUtil;

public class BlockReel extends BlockMot
{
	public BlockReel (int start, int longueur, int frequence, String aFormat, String aIdSlave, ConnexionModbusTCP aCm) throws Exception
	{
		super (start, longueur, frequence, aFormat, aIdSlave, aCm);
		this.initialize ();
	}

	@Override
	public String toConvert (int aCle, String aCoef)
	{
		int i = aCle - this.getAdresseDebut ();
		byte [] b0 = this.getRes ().getRegister (i).toBytes ();
		byte [] b1 = this.getRes ().getRegister (i+1).toBytes ();
		byte [] tb = {b1 [0], b1 [1], b0 [0], b0 [1]};
		if (aCoef.equals (""))
		{
			aCoef = "1";
		}
		String sValeur = Float.toString (ModbusUtil.registersToFloat (tb) * Float.parseFloat (aCoef));
		return sValeur;		
	}

}