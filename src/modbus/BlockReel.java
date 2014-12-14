
package modbus;

import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;
import net.wimpi.modbus.msg.WriteMultipleRegistersRequest;
import net.wimpi.modbus.procimg.Register;
import net.wimpi.modbus.procimg.SimpleRegister;
import net.wimpi.modbus.util.ModbusUtil;

public class BlockReel extends Blocks
{

	@Override
	public String toConvert (int aCle, int numbit, Float aCoef, ReadMultipleRegistersResponse res)
	{
		int i = aCle - this.getStartadress ();
		byte [] b0 = res.getRegister (i).toBytes ();
		byte [] b1 = res.getRegister (i+1).toBytes ();
		byte [] tb = {b1 [0], b1 [1], b0 [0], b0 [1]};
		String sValeur = Float.toString (ModbusUtil.registersToFloat (tb) * aCoef);
		return sValeur;		
	}

	@Override
	public WriteMultipleRegistersRequest convertToRegister (int aCle, Float aSaisie, Float aCoef)
	{
		WriteMultipleRegistersRequest write = new WriteMultipleRegistersRequest ();
		Float val = aSaisie/aCoef;
		byte [] b = ModbusUtil.floatToRegisters (val);
		Register sr = new SimpleRegister (b[0], b[1]);
		Register sr2 = new SimpleRegister (b[2], b[3]);
		Register [] tsr = {sr2, sr};	
		write.setReference (aCle);
		write.setRegisters (tsr);
		return write;
		
	}
}