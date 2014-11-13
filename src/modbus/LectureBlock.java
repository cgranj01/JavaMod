
package modbus;

import gui.FenetrePrincipale;
import gui.MapTableModel;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.JOptionPane;
import javax.swing.JTable;

import connexionBase.Connexion;

public class LectureBlock extends Thread
{
	private Connexion					co				= Connexion.getInstance ();
	private FenetrePrincipale			fp;
	private static LectureBlock			lb;
	private ArrayList<BlockMot>			vecteur			= new ArrayList <BlockMot>();
	private boolean						running			= true;
	private HashMap<String, String []>	hmaffiche		= new HashMap<> ();
	private Map<String, String>			hmafficheModif	= new TreeMap<> ();
	private MapTableModel				tm;
	public JTable						jtable			= new JTable ();

	//public int 							nbBlock = 0;

	private LectureBlock (FenetrePrincipale aFp)
	{
		this.fp = aFp;
		try
		{		
			this.co.getOrdreSql ().executeUpdate ("delete from variables ");
			this.lirefichierVariables ("variables.csv");
			this.creationBlock ();
			this.co.getConnexionSqlite ().commit ();
			this.tm = new MapTableModel (this.hmafficheModif, "Variable", "Valeur");
			this.jtable.setModel (this.tm);
			this.jtable.setFont (new Font ("Tahoma", Font.PLAIN, 14));
			this.jtable.setRowHeight (30);
			this.jtable.setRowSelectionAllowed (true);
			this.lectureComplete ();
		}

		catch (SQLException aSQLe)
		{
			JOptionPane.showMessageDialog (null, aSQLe.getMessage (), "Avertissement", JOptionPane.ERROR_MESSAGE);
			this.fp.setInfoCourante (" Erreur");
		}
	}

	public void lirefichierVariables (String aFichier) throws SQLException
	{
		BufferedReader br;
		String insertSQL = "insert into variables (id_var,type,"
				+ "adresse,delai_enregistrement,id_slave,tagname,text1,text2,text3,coef,affichage) values (?,?,?,?,?,?,?,?,?,?,?)";
		String line;
		String csvSplitBy = ";";
		PreparedStatement ps = this.co.getpStatement (insertSQL);
		try
		{
			br = new BufferedReader (new FileReader (new File (aFichier)));
			while ( (line = br.readLine ()) != null)
			{
				if ( ! (line.startsWith ("id_var")))
				{
					String [] variable = line.split (csvSplitBy);
		
					ps.setString (1, variable [0]);
					ps.setString (2, variable [1]);
					ps.setString (3, variable [2]);
					ps.setString (4, variable [3]);
					ps.setString (5, variable [4]);
					ps.setString (6, variable [5]);
					ps.setString (7, variable [6]);
					ps.setString (8, variable [7]);
					ps.setString (9, variable [8]);
					ps.setString (10, variable [9]);
					ps.setString (11, variable [10]);
					ps.addBatch ();						
				}
				
			}
			ps.executeBatch ();		
			this.co.getConnexionSqlite ().commit ();
		}
		catch (FileNotFoundException e)
		{
			JOptionPane.showMessageDialog (null, e.getMessage (), " Erreur", JOptionPane.ERROR_MESSAGE);
			System.exit ( - 1);
		}
		catch (IOException e)
		{
			e.printStackTrace ();
		}
	}
	
	public void creationBlock() throws SQLException
	{
		int amax, amin;
		String atype,aidslave;
		this.co.getOrdreSql ().executeUpdate ("DELETE from Block");
		this.co.getConnexionSqlite ().commit ();
		ResultSet res = this.co.getOrdreSql ().executeQuery ("SELECT type, min(adresse) as mini, max(adresse) as maxi, var.id_slave as idslave " +
								"from variables as var, esclaves as esc " +
								"WHERE var.id_slave = esc.id_slave " +
								"and type not like '%RW%' " +
								"group by  type, var.id_slave");
		while (res.next ())
		{
			amax = res.getInt ("maxi");
			amin = res.getInt ("mini");
			atype = res.getString ("type");
			aidslave = res.getString ("idslave");
			while (amin < amax+1)
			{
				this.co.getOrdreSql ().executeUpdate ("insert into block (adresse_start, taille, frequence, actif, id_slave, format) " +
														"values ("+ amin + ", 123, 1000, 1, '" + aidslave+ "', '" + atype + "')");
				amin = amin + 122;
			}
		}
		this.co.getConnexionSqlite ().commit ();	
	}

	public void lectureComplete ()
	{
		String query = "select id_slave, adresseip, timeout, port from esclaves order by id_slave";
		ResultSet res;
		ConnexionModbusTCP cm = null;
		try
		{
			res = this.co.getOrdreSql ().executeQuery (query);
			String idslave = "";
			
			while (res.next ())
			{
				String idslavecurrent = res.getString ("id_slave");
				cm = new ConnexionModbusTCP (res.getString ("adresseip"), res.getInt ("port"),
						res.getInt ("timeout"));
				try
				{		
					cm.getTCPMasterCon ().connect ();
					this.newBlock (cm, res.getString ("id_slave"));
					idslave = idslave + " - " + idslavecurrent;
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog (null, "Esclave " + idslavecurrent + " injoignable", "Avertissement",
							JOptionPane.WARNING_MESSAGE);
				}

				if (idslave.equals (""))
				{
					this.fp.setInfoCourante (" Erreur : aucun esclave en communication ");
				}
				else
				{
					this.fp.setInfoCourante (" Lecture en cours sur esclave(s) " + idslave + " ");
				}
			}
			//this.updateBlockSleepTime ();
			this.demarrageLecture (); 
		}
		catch (SQLException aSQLe)
		{
			System.out.println (aSQLe.getMessage ());
		}
	}

	public void newBlock (ConnexionModbusTCP aCMTCP, String aIdSlave)
	{
		String query = "select adresse_start, taille, id_slave, format, frequence from block where id_slave = '"
				+ aIdSlave + "'";
		try
		{
			ResultSet res = this.co.getOrdreSql ().executeQuery (query);
			BlockMot bm;
			while (res.next ())
			{
				try
				{
				if (res.getString ("format").equals ("REAL"))
				{
					bm = new BlockReel (res.getInt ("adresse_start"), res.getInt ("taille"), res.getInt ("frequence"),
							res.getString ("format"), res.getString ("id_slave"), aCMTCP);
					if (bm.getRow ()!= 0)
					{
						this.vecteur.add (bm);	
						//this.nbBlock++;
					}				
					
				}

				if (res.getString ("format").equals ("INT"))
				{
					bm = new BlockInt (res.getInt ("adresse_start"), res.getInt ("taille"), res.getInt ("frequence"),
					res.getString ("format"), res.getString ("id_slave"), aCMTCP);
					if (bm.getRow ()!= 0)
					{
						this.vecteur.add (bm);	
						//this.nbBlock++;
					}
				}
					
				if (res.getString ("format").equals ("DINT"))
				{
					bm = new BlockDInt (res.getInt ("adresse_start"), res.getInt ("taille"), res.getInt ("frequence"),
						res.getString ("format"), res.getString ("id_slave"), aCMTCP);
					if (bm.getRow ()!= 0)
					{
						this.vecteur.add (bm);	
						//this.nbBlock++;
					}

				}

				if (res.getString ("format").equals ("WORD"))
				{
					bm = new BlockWord (res.getInt ("adresse_start"), res.getInt ("taille"), res.getInt ("frequence"),
						res.getString ("format"), res.getString ("id_slave"), aCMTCP);
					if (bm.getRow ()!= 0)
					{
						this.vecteur.add (bm);	
						//this.nbBlock++;
					}
				}

				if (res.getString ("format").equals ("DWORD"))
				{
					bm = new BlockDWord (res.getInt ("adresse_start"), res.getInt ("taille"), res.getInt ("frequence"),
							res.getString ("format"), res.getString ("id_slave"), aCMTCP);
					if (bm.getRow ()!= 0)
					{
						this.vecteur.add (bm);	
						//this.nbBlock++;
					}
				}
				if (res.getString ("format").equals ("BIT"))
				{
					bm = new BlockBit (res.getInt ("adresse_start"), res.getInt ("taille"), res.getInt ("frequence"),
						res.getString ("format"), res.getString ("id_slave"), aCMTCP);
					if (bm.getRow ()!= 0)
					{
						this.vecteur.add (bm);	
						//this.nbBlock++;
					}
				}

			}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog (null, e.getMessage (), "Avertissement", JOptionPane.WARNING_MESSAGE);
					break;
				}
			}
		}

		catch (SQLException aSQLe)
		{
			JOptionPane.showMessageDialog (null, aSQLe.getMessage (), "Erreur", JOptionPane.ERROR_MESSAGE);
			this.fp.setInfoCourante (" Erreur");
		}
		
	}
	
//	public void updateBlockSleepTime() throws SQLException
//	{
//		
//		int i = 1000/this.nbBlock;
//		this.co.getOrdreSql ().executeUpdate ("update block set frequence = " + String.valueOf (i));
//		this.co.getConnexionSqlite ().commit ();
//		
//		for (BlockMot bm : this.vecteur)
//		{
//			bm.setFreq ((long) i);
//		}
//	}

	public static LectureBlock getInstance (FenetrePrincipale aFp)
	{	
		if (LectureBlock.lb == null)
		{
			LectureBlock.lb = new LectureBlock (aFp);
		}		
		return LectureBlock.lb;
	}

	public static LectureBlock getInstance ()
	{
		return LectureBlock.lb;
	}

	public static void setInstanceNull ()
	{
		LectureBlock.lb = null;
	}

	public void actualiseHM ()
	{
		for (BlockMot bm : this.vecteur)
		{
			this.hmaffiche.putAll (bm.getHmaffiche ());
		}
		
		for (Entry<String, String []> entreeaffiche : this.hmaffiche.entrySet ())
		{
			this.hmafficheModif.put (entreeaffiche.getValue () [0], entreeaffiche.getValue () [5]);
		}

	}

	public void demarrageLecture ()
	{		
		synchronized (BlockMot.class)
		{
			for (BlockMot bm : this.vecteur)
			{
				bm.start ();
				try
				{
					Thread.sleep (100L);
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}

	public void arretLecture ()
	{
		for (BlockMot bm : this.vecteur)
		{
			this.running = false;
			bm.setRunning (false);
			this.fp.setInfoCourante (" Arret manuel");
		}
	}

	public void run ()
	{
		while (this.running)
		{
			for (BlockMot bm : this.vecteur)
			{
				if (bm.isRunning () == false)
				{
					bm.getIdSlave ();
					this.fp.setInfoCourante (" Erreur de requetes sur esclave " + bm.getIdSlave ());
					//this.arretLecture ();
					
				}

				if (bm.isRecsurfichier () == true)
				{
					this.fp.setInfoCourante (" Aucune base connectée");
					this.fp.getBarreDeMenu ().getActionLecture ().setEnabled (false);
				}
				this.actualiseHM ();
				this.fp.getDefileur ().setViewportView (this.jtable);

			}
			try
			{
				synchronized (this)
				{
					Thread.sleep (1000L);
				}				
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace ();
			}
		}
	}

}
