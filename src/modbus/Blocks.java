
package modbus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import net.wimpi.modbus.ModbusException;
import net.wimpi.modbus.ModbusIOException;
import net.wimpi.modbus.ModbusSlaveException;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.ReadMultipleRegistersRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;

import org.hibernate.Session;

import persistence.Esclaves;
import persistence.Variables;
import connexionBase.SingletonSessionHSQL;

public abstract class Blocks extends Thread implements Convertir
{
	private long							idblock;
	private String							format;
	private String							fonction;
	private int								startadress;
	private int								longueur;
	private int								frequence;
	private List<Variables>					listvar = new ArrayList<Variables> ();;
	private Esclaves						slave;
	private ConnexionModbusTCP				connModbus;

	//private
	private ModbusTCPTransaction			trans;
	//private ReadMultipleRegistersRequest	req;
	private String							messageInfo	= null;
	private boolean							running		= true;

	private DateTimeFormatter				dtf			= DateTimeFormatter.ofPattern ("HH:mm:ss:SSS");

	private Long							freq;
	private int								row			= 0;
	
	

	public void preparationRequete ()
	{
		ReadMultipleRegistersRequest	req;
		req = new ReadMultipleRegistersRequest ();
		// this.cmod.getTCPMasterCon ().connect ();
		this.trans = new ModbusTCPTransaction ();
		req.setReference (this.startadress);
		req.setWordCount (this.longueur);
		this.trans.setRequest (req);
		this.trans.setConnection (this.connModbus.getTCPMasterCon ());
	}

	public void initialize ()
	{
		Session sessionHSQL	= SingletonSessionHSQL.getInstance ().getSession ();
		String query = "select count (*) from Variables v inner join v.Slave s on s.idslave = :idslave and "
				+ "adresse between :adressdebut and :adressfin and format = :format";
		this.row = ((Long) sessionHSQL.createQuery (query).setLong ("idslave", this.slave.getIdslave ())
				.setInteger ("adressdebut", this.startadress).setInteger ("adressfin", (this.startadress + this.longueur - 2))
				.setString ("format", this.format).uniqueResult ()).intValue ();
		if (this.row != 0)
		{
			// this.treemap = new TreeMap<> ();
			this.remplirTm ();
		}
	}

	public void remplirTm ()
	{
		Session sessionHSQL	= SingletonSessionHSQL.getInstance ().getSession ();
		String query = "select v.tagname, v.format, v.type, v.adresse, v.recdelay, "
				+ "v.userfield1, v.userfield2, v.zone, v.coefficient, v.affichage, v.periode, v.idtag, v.valeur from Variables as v join v.Slave s on s.idslave = :idslave and "
				+ "adresse between :adressdebut and :adressfin and format = :format";

		List<Object []> groupList = sessionHSQL.createQuery (query).setLong ("idslave", this.slave.getIdslave ())
				.setInteger ("adressdebut", this.startadress).setInteger ("adressfin", (this.startadress + this.longueur -2))
				.setString ("format", this.format).list ();
		for (Object [] t : groupList)
		{
			Variables var = new Variables ();
			var.setTagname (t [0].toString ());
			var.setFormat (t [1].toString ());
			var.setType (t [2].toString ());
			var.setAdresse ((int) t [3]);
			var.setRecdelay ((int) t [4]);
			var.setUserfield1 (t [5].toString ());
			var.setUserfield2 (t [6].toString ());
			var.setZone (t [7].toString ());
			var.setCoefficient ((float) t [8]);
			var.setAffichage (t [9].toString ());
			var.setPeriode (t [10].toString ());
			var.setIdtag ((Long) t[11]);
			var.setValeur (0f);

			this.listvar.add (var);


		}
		
		this.preparationRequete ();

	}

	public void run ()
	{
		while (this.running && (this.row != 0))
		{
			try
			{
				this.trans.execute ();
				ReadMultipleRegistersResponse res = (ReadMultipleRegistersResponse) this.trans.getResponse ();
				LocalDateTime localedatetime = LocalDateTime.now ();
				String dateString = localedatetime.format (this.dtf);
				System.out.println (toString () + " " + dateString);

				for (Variables listv : this.listvar)
				{
					String sValeur = this.toConvert (listv.getAdresse (), listv.getCoefficient (), res);
					listv.setValeur (Float.parseFloat (sValeur));
				}
				// for (Entry<Integer, Variables> entree : this.treemap.entrySet
				// ())
				// {
				// String sValeur = this.toConvert (entree.getKey (),
				// entree.getValue ().getCoefficient ());
				// entree.getValue ().setValeur (Float.parseFloat (sValeur));
				// }

				this.delai (50L, this.freq-50L);
			}

			catch (ModbusIOException e)
			{
				this.running = false;
				JOptionPane.showMessageDialog (null, e.getMessage (), "IO Erreur", JOptionPane.ERROR_MESSAGE);
			}
			catch (ModbusSlaveException e)
			{
				this.running = false;
				JOptionPane.showMessageDialog (null, "Requetes vers esclave " + this.slave.getIdslave ()
						+ " impossible \n" + e.getMessage (), "Esclave Erreur", JOptionPane.ERROR_MESSAGE);
			}
			catch (ModbusException e)
			{
				this.running = false;
				JOptionPane.showMessageDialog (null, e.getMessage (), "Modbus Erreur", JOptionPane.ERROR_MESSAGE);
			}
		}

	}

	public String toString ()
	{
		return "Block " + String.valueOf (this.startadress) + " " + this.format + " nb ligne : " + this.row;
	}

	void delai (Long aDelaifixe, Long aDelai)
	{
		try
		{
			synchronized (this)
			{
				Thread.sleep (aDelai);
			}
			synchronized (Blocks.class)
			{
				Thread.sleep (aDelaifixe);
			}

		}
		catch (InterruptedException e)
		{
			e.printStackTrace ();
		}
	}

	public String getMessageInfo ()
	{
		return this.messageInfo;
	}

	public void setMessageInfo (String message)
	{
		this.messageInfo = message;
	}

	public ModbusTCPTransaction getTrans ()
	{
		return this.trans;
	}

	public void setTrans (ModbusTCPTransaction trans)
	{
		this.trans = trans;
	}

	public boolean isRunning ()
	{
		return running;
	}

	public void setRunning (boolean running)
	{
		this.running = running;
	}

	public Long getFreq ()
	{
		return freq;
	}

	public void setFreq (Long freq)
	{
		this.freq = freq;
	}

	public int getRow ()
	{
		return row;
	}

	public void setRow (int row)
	{
		this.row = row;
	}

	public List<Variables> getListvar ()
	{
		return listvar;
	}

	public void setListvar (List<Variables> listvar)
	{
		this.listvar = listvar;
	}

	public long getIdblock ()
	{
		return idblock;
	}

	public void setIdblock (long idblock)
	{
		this.idblock = idblock;
	}

	public String getFormat ()
	{
		return format;
	}

	public void setFormat (String format)
	{
		this.format = format;
	}

	public int getStartadress ()
	{
		return startadress;
	}

	public void setStartadress (int startadress)
	{
		this.startadress = startadress;
	}

	public int getLongueur ()
	{
		return longueur;
	}

	public void setLongueur (int longueur)
	{
		this.longueur = longueur;
	}

	public int getFrequence ()
	{
		return frequence;
	}

	public void setFrequence (int frequence)
	{
		this.frequence = frequence;
	}

	public String getFonction ()
	{
		return fonction;
	}

	public void setFonction (String fonction)
	{
		this.fonction = fonction;
	}

	//
	// public Esclaves getSlave ()
	// {
	// return slave;
	// }
	//
	// public void setSlave (Esclaves slave)
	// {
	// this.slave = slave;
	// }

	public ConnexionModbusTCP getConnModbus ()
	{
		return connModbus;
	}

	public void setConnModbus (ConnexionModbusTCP connModbus)
	{
		this.connModbus = connModbus;
	}

	public Esclaves getSlave ()
	{
		return slave;
	}

	public void setSlave (Esclaves slave)
	{
		this.slave = slave;
	}

}
