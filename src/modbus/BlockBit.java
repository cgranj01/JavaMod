
package modbus;

import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;

import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;
import net.wimpi.modbus.msg.WriteMultipleRegistersRequest;
import net.wimpi.modbus.procimg.Register;
import net.wimpi.modbus.procimg.SimpleRegister;
import net.wimpi.modbus.util.ModbusUtil;



public class BlockBit extends Blocks
{


	@Override
	public WriteMultipleRegistersRequest convertToRegister (int aCle, Float aSaisie, Float aCoef)
	{
		WriteMultipleRegistersRequest write = new WriteMultipleRegistersRequest ();
		int val = Math.round (aSaisie);
		byte [] b = ModbusUtil.unsignedShortToRegister ((short) (val));
		SimpleRegister sr = new SimpleRegister (b[0], b[1]);
		Register [] tr= {sr};
		write.setReference (aCle);
		write.setRegisters (tr);
		return write;
		

	}


	@Override
	public String toConvert (int aCle, int aBit, Float aCoef, ReadMultipleRegistersResponse ares)
	{
		int i = aCle - this.getStartadress ();
		Long valeur = Long.parseLong (Integer.toBinaryString (ares.getRegisterValue (i)));
		String sValeur = String.format ("%016d", valeur);
		char[] c = sValeur.toCharArray ();
		ArrayUtils.reverse (c);
		char ch = c[aBit];
		return String.valueOf (ch);
		
	}

}