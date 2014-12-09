
package modbus;

import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;



public class BlockBit extends Blocks
{


	@Override
	public String toConvert (int keyentree, Float aCoef, ReadMultipleRegistersResponse res)
	{		
		int i = keyentree - this.getStartadress ();
		//byte [] b0 = this.getRes ().getRegister (i).to;
		Long valeur = Long.parseLong (Integer.toBinaryString (res.getRegisterValue (i)));
		//String sValeur = String.format ("%016d", valeur);
		String sValeur = String.format ("%1d", valeur);
		return sValeur;
	}

}