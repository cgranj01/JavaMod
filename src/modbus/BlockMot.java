
package modbus;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.JOptionPane;

import net.wimpi.modbus.ModbusException;
import net.wimpi.modbus.ModbusIOException;
import net.wimpi.modbus.ModbusSlaveException;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.ReadMultipleRegistersRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;
import connexionBase.Connexion;
import connexionBase.ConnexionDBrec;

public abstract class BlockMot extends Thread implements Convertir
{
	private Connexion						co				= Connexion.getInstance ();
	private ConnexionDBrec					corec			= ConnexionDBrec.getInstance ();
	private ModbusTCPTransaction			trans			= null;
	private ReadMultipleRegistersRequest	req				= null;
	private InetAddress						addr			= null;
	private ReadMultipleRegistersResponse	res				= null;
	private String							messageInfo		= null;
	private String							idSlave			= null;
	private String							format			= null;
	private boolean							running			= true;
	private boolean							recsurfichier	= false;
	private TreeMap<String, String []>		hmchg;
	private TreeMap<String, String []>		hmdelai;
	private TreeMap<String, String []>		hmaffiche 		= new TreeMap<> ();
	private SimpleDateFormat				sdf				= new SimpleDateFormat ("dd/MM/yyyy HH:mm:ss.SSS");
	private Long							freq;
	private int								adresseDebut;
	private int								adresseFin;
	private ConnexionModbusTCP				cmod;
	private int								row				= 0;
	private PreparedStatement				pStatement;
	private BufferedWriter					bw;
//	private int longueur;
//	private int start;

	public BlockMot (int start, int longueur, int frequence, String aFormat, String aIdSlave, ConnexionModbusTCP aCm) throws Exception
	{
		super ();
		this.cmod = aCm;
		this.freq = (long) frequence;
		this.adresseDebut = start;
		this.adresseFin = start + longueur - 2;
		this.idSlave = aIdSlave;
		this.format = aFormat;
//		this.longueur = longueur;
//		this.start = start;
		this.preparationFichierBuffer ();
		this.initialize ();
		this.preparationRequete (longueur, start);
	}
	
	public void preparationRequete (int aLongueur, int aStart) throws Exception
	{
		this.req = new ReadMultipleRegistersRequest ();
		//this.cmod.getTCPMasterCon ().connect ();
		this.trans = new ModbusTCPTransaction ();	
		this.req.setReference (aStart);
		this.req.setWordCount (aLongueur);
		this.trans.setRequest (this.req);
		this.trans.setConnection (this.cmod.getTCPMasterCon ());
	}

	public void initialize ()
	{
		try
		{
			String query = "select count (*) from variables as var WHERE " + "var.id_slave = '"
					+ this.idSlave + "' and adresse between " + this.adresseDebut + " and " + this.adresseFin
					+ " and type = '" + this.format + "'";
			ResultSet res = this.co.getOrdreSql ().executeQuery (query);
			
			while (res.next ())
			{
				this.row = res.getInt (1);
			}
			if (this.row != 0)
			{
				
				if (this.corec.isConnecte ())
				{
					String insert = "insert into enr_variables "
							+ "(id_var, tagname, text1, text2, text3, adresse, valeur, horodatage) values (?,?,?,?,?,?,?,?)";
					this.pStatement = this.corec.getConnexionSqlServer ().prepareStatement (insert);
				}
				this.hmchg = new TreeMap<> ();
				this.hmdelai = new TreeMap<> ();
				this.hmaffiche = new TreeMap<> ();
				this.creationHmEnregitrement ();	
				this.creationHmAffiche ();
			}
		}
		catch (SQLException e)
		{
			JOptionPane
					.showMessageDialog (null, e.getMessage (), "Erreur Lecture variables", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void creationHmEnregitrement() throws SQLException
	{
		String query = "select id_var, type, adresse, delai_enregistrement, id_slave, tagname, text1, text2, text3, coef from variables as var WHERE "
				+ "var.id_slave = '"
				+ this.idSlave
				+ "' and adresse between "
				+ this.adresseDebut
				+ " and "
				+ this.adresseFin + " and type = " + "'" + this.format + "' order by adresse";

		ResultSet res = this.co.getOrdreSql ().executeQuery (query);
		while (res.next ())
		{
			String str = res.getString ("delai_enregistrement");
			if (str.toUpperCase ().equals ("C"))
			{
				String [] tabdescription = new String [9];
				tabdescription [0] = res.getString ("tagname");
				tabdescription [1] = res.getString ("text1");
				tabdescription [2] = res.getString ("text2");
				tabdescription [3] = res.getString ("text3");
				tabdescription [4] = res.getString ("id_var");
				tabdescription [5] = "";
				tabdescription [6] = res.getString ("delai_enregistrement");
				tabdescription [7] = "0";
				tabdescription [8] = res.getString ("coef").replaceAll (",",".");
				this.hmchg.put (res.getString ("adresse"), tabdescription);
			}
			else
			{			
				if(!(str.equals ("")))
				{
					String [] tabdescription = new String [9];
					tabdescription [0] = res.getString ("tagname");
					tabdescription [1] = res.getString ("text1");
					tabdescription [2] = res.getString ("text2");
					tabdescription [3] = res.getString ("text3");
					tabdescription [4] = res.getString ("id_var");
					tabdescription [5] = "";
					tabdescription [6] = res.getString ("delai_enregistrement");
					tabdescription [7] = "0";
					tabdescription [8] = res.getString ("coef").replaceAll (",",".");;
					this.hmdelai.put (res.getString ("adresse"), tabdescription);
				}			
			}
		}
	}
	
	public void creationHmAffiche() throws SQLException
	{
		String query = "select id_var, type, adresse, delai_enregistrement, id_slave, tagname, text1, text2, text3, coef from variables as var WHERE "
				+ "var.id_slave = '"
				+ this.idSlave
				+ "' and adresse between "
				+ this.adresseDebut
				+ " and "
				+ this.adresseFin + " and type = " + "'" + this.format + "'"
				+ " and affichage = 'Y' order by adresse";

			ResultSet res = this.co.getOrdreSql ().executeQuery (query);
			while (res.next ())
			{
				String [] taffiche = new String [6];
				taffiche [0] = res.getString ("tagname");
				taffiche [1] = res.getString ("text1");
				taffiche [2] = res.getString ("text2");
				taffiche [3] = res.getString ("id_var");
				taffiche [4] = res.getString ("coef").replaceAll (",",".");
				taffiche [5] = "";
				this.hmaffiche.put (res.getString ("adresse"), taffiche);
			}
	}

	public void run ()
	{	
		while (this.running && (this.getRow () != 0))
		{
			try
			{					
				this.trans.execute ();
				this.res = (ReadMultipleRegistersResponse) this.trans.getResponse ();
				Long heureCourante = System.currentTimeMillis ();
				Long tempsCourant = System.nanoTime ();
				String dateString = this.sdf.format (heureCourante);
				System.out.println (toString () + " " + dateString);
				for (Entry<String, String []> entree : this.hmchg.entrySet ())
				{
					String sValeur = this.toConvert (Integer.parseInt (entree.getKey ()), entree.getValue () [8]);
					if ( ! entree.getValue () [5].equals (sValeur))
					{
						this.insertion (entree.getKey (), sValeur, entree.getValue () [0], entree.getValue () [1],
								entree.getValue () [2], entree.getValue () [3], entree.getValue () [4], dateString);
						entree.getValue () [5] = sValeur;
					}
				}
				
				for (Entry<String, String []> entreedelai : this.hmdelai.entrySet ())
				{
					String sValeur = this.toConvert (Integer.parseInt (entreedelai.getKey ()),
							entreedelai.getValue () [8]);
					if (tempsCourant - Long.parseLong (entreedelai.getValue () [7]) >= Long.parseLong (entreedelai
							.getValue () [6]) * 1E9)
					{
						this.insertion  (entreedelai.getKey (), sValeur, entreedelai.getValue () [0],
								entreedelai.getValue () [1], entreedelai.getValue () [2], entreedelai.getValue () [3],
								entreedelai.getValue () [4], dateString);
						entreedelai.getValue () [7] = String.valueOf (tempsCourant);
					}
				}
				for (Entry<String, String []> entreeaffiche : this.hmaffiche.entrySet ())
				{
					String sValeur = this.toConvert (Integer.parseInt (entreeaffiche.getKey ()), entreeaffiche.getValue ()[4]);
					entreeaffiche.getValue () [5]= sValeur;
				}
				this.delai (100L, this.freq);			
			}
			
			catch (ModbusIOException e)
			{
				this.running = false;
				JOptionPane.showMessageDialog (null, e.getMessage (), "IO Erreur", JOptionPane.ERROR_MESSAGE);
			}
			catch (ModbusSlaveException e)
			{
				this.running = false;
				JOptionPane.showMessageDialog (null, "Requetes vers esclave " + this.idSlave + " impossible \n" + e.getMessage (),
					"Esclave Erreur", JOptionPane.ERROR_MESSAGE);
			}
			catch (ModbusException e)
			{
				this.running = false;
				JOptionPane.showMessageDialog (null, e.getMessage (), "Modbus Erreur", JOptionPane.ERROR_MESSAGE);
			}
		}
			
	}
	
	public String toString()
	{
		return "Block " + String.valueOf (this.adresseDebut);
	}
	
	public void insertion (String aAdresse, String aValeur, String aTagname, String aText1, String aText2,
			String aText3, String aId_var, String aHorodatage)
	{		
		if(this.corec.isConnecte ())
		{
			try
			{		
				this.pStatement.setString (1, aId_var);
				this.pStatement.setString (2, aTagname);
				this.pStatement.setString (3, aText1);
				this.pStatement.setString (4, aText2);
				this.pStatement.setString (5, aText3);
				this.pStatement.setString (6, aAdresse);
				this.pStatement.setString (7, aValeur);
				this.pStatement.setString (8, aHorodatage);
				this.pStatement.addBatch ();
				this.pStatement.executeBatch ();
			}
			catch (SQLException e)
			{
				this.recupereValeur (aAdresse, aValeur, aTagname, aText1, aText2, aText3, aId_var, aHorodatage);
			}			
		}
		else
		{
			this.recupereValeur (aAdresse, aValeur, aTagname, aText1, aText2, aText3, aId_var, aHorodatage);
		}	
		
	}
	
	public void recupereValeur(String aAdresse, String aValeur, String aTagname, String aText1, String aText2,
			String aText3, String aId_var, String aHorodatage)
	{
		String line = aId_var + ";" + aTagname + ";" + aText1 + ";" + aText2 + ";" + aText3 + ";" + aAdresse + ";"
				+ aValeur + ";" + aHorodatage;
		try
		{
			this.bw.append (line);
			this.bw.newLine ();
			this.bw.flush ();		
        	this.recsurfichier = true;
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void preparationFichierBuffer()
	{
		File file = new File("bufferErrors.sql");	
		FileWriter writer = null;
		try
		{
			if (!file.exists())
			{
				file.createNewFile();
			}
			writer = new FileWriter(file,true);
			this.bw = new BufferedWriter (writer);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	void delai (Long aDelaifixe, Long aDelai)
	{
		try
		{						
			synchronized (this)
			{
				Thread.sleep (aDelai);			
			}
			synchronized (BlockMot.class)
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

	public ReadMultipleRegistersRequest getReq ()
	{
		return req;
	}

	public void setReq (ReadMultipleRegistersRequest req)
	{
		this.req = req;
	}

	public InetAddress getAddr ()
	{
		return addr;
	}

	public void setAddr (InetAddress addr)
	{
		this.addr = addr;
	}

	public boolean isRunning ()
	{
		return running;
	}

	public void setRunning (boolean running)
	{
		this.running = running;
	}

	public ReadMultipleRegistersResponse getRes ()
	{
		return res;
	}

	public void setRes (ReadMultipleRegistersResponse res)
	{
		this.res = res;
	}

	public Long getFreq ()
	{
		return freq;
	}

	public void setFreq (Long freq)
	{
		this.freq = freq;
	}

	public TreeMap<String, String []> getHm ()
	{
		return hmchg;
	}

	public void setHm (TreeMap<String, String []> hm)
	{
		this.hmchg = hm;
	}

	public TreeMap<String, String []> getHmdelai ()
	{
		return hmdelai;
	}

	public void setHmdelai (TreeMap<String, String []> hmdelai)
	{
		this.hmdelai = hmdelai;
	}

	public int getRow ()
	{
		return row;
	}

	public void setRow (int row)
	{
		this.row = row;
	}

	public int getAdresseDebut ()
	{
		return adresseDebut;
	}

	public void setAdresseDebut (int adresseDebut)
	{
		this.adresseDebut = adresseDebut;
	}

	public boolean isRecsurfichier ()
	{
		return recsurfichier;
	}

	public void setRecsurfichier (boolean recsurfichier)
	{
		this.recsurfichier = recsurfichier;
	}

	public String getIdSlave ()
	{
		return idSlave;
	}

	public void setIdSlave (String idSlave)
	{
		this.idSlave = idSlave;
	}

	public TreeMap<String, String []> getHmaffiche ()
	{
		return hmaffiche;
	}

	public void setHmaffiche (TreeMap<String, String []> hmaffiche)
	{
		this.hmaffiche = hmaffiche;
	}

}