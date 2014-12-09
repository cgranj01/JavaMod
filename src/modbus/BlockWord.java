
package modbus;

import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;
import net.wimpi.modbus.util.ModbusUtil;

public class BlockWord extends Blocks
{
	
	
	@Override
	public String toConvert (int aCle, Float aCoef, ReadMultipleRegistersResponse res)
	{
		int i = aCle - this.getStartadress ();
		byte [] b0 = res.getRegister (i).toBytes ();
		String sValeur = Float.toString ((ModbusUtil.registerToShort (b0) & 0xffff) * aCoef);
		return sValeur;

	}

}