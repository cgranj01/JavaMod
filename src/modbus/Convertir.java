package modbus;

import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;
import net.wimpi.modbus.msg.WriteMultipleRegistersRequest;
import net.wimpi.modbus.procimg.SimpleRegister;

public interface Convertir
{
	
	public String toConvert(int aCle, int aBit, Float aCoef, ReadMultipleRegistersResponse ares);
	
	public WriteMultipleRegistersRequest convertToRegister (int aCle, Float aValeur, Float aCoef); 

}
