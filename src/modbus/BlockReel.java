
package modbus;

import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;
import net.wimpi.modbus.util.ModbusUtil;

public class BlockReel extends Blocks
{

	@Override
	public String toConvert (int aCle, Float aCoef, ReadMultipleRegistersResponse res)
	{
		int i = aCle - this.getStartadress ();
		byte [] b0 = res.getRegister (i).toBytes ();
		byte [] b1 = res.getRegister (i+1).toBytes ();
		byte [] tb = {b1 [0], b1 [1], b0 [0], b0 [1]};
		String sValeur = Float.toString (ModbusUtil.registersToFloat (tb) * aCoef);
		return sValeur;		
	}

}