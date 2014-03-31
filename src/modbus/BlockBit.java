
package modbus;



public class BlockBit extends BlockMot
{
	public BlockBit (int start, int longueur, int frequence, String aFormat, String aIdSlave, ConnexionModbusTCP aCm) throws Exception
	{
		super (start, longueur, frequence, aFormat, aIdSlave, aCm);
		this.initialize ();
	}

	@Override
	public String toConvert (int keyentree, String aCoef)
	{		
		int i = keyentree - this.getAdresseDebut ();
		//byte [] b0 = this.getRes ().getRegister (i).to;
		Long valeur = Long.parseLong (Integer.toBinaryString (this.getRes ().getRegisterValue (i)));
		String sValeur = String.format ("%016d", valeur);
		return sValeur;
	}

}