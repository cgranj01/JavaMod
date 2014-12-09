package modbus;

import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;

public interface Convertir
{
	public String toConvert(int aCle, Float aCoef, ReadMultipleRegistersResponse ares);

}
