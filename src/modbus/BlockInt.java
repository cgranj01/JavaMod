
package modbus;

import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;
import net.wimpi.modbus.msg.WriteMultipleRegistersRequest;
import net.wimpi.modbus.procimg.Register;
import net.wimpi.modbus.procimg.SimpleRegister;
import net.wimpi.modbus.util.ModbusUtil;

public class BlockInt extends Blocks
{


	@Override
	public String toConvert (int aCle, int numbit, Float aCoef, ReadMultipleRegistersResponse res)
	{
		int i = aCle - this.getStartadress ();
		byte [] b0 = res.getRegister (i).toBytes ();
		String sValeur = Float.toString (ModbusUtil.registerToShort (b0) * aCoef);
		return sValeur;
	}

	@Override
	public WriteMultipleRegistersRequest convertToRegister (int aCle, Float aSaisie, Float aCoef)
	{
		WriteMultipleRegistersRequest write = new WriteMultipleRegistersRequest ();
		int val = Math.round (aSaisie/aCoef);
		byte [] b = ModbusUtil.shortToRegister ((short) (val));
		SimpleRegister sr = new SimpleRegister (b[0], b[1]);
		Register [] tr= {sr};
		write.setReference (aCle);
		write.setRegisters (tr);
		return write;
		

	}

}