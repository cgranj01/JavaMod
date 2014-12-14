
package modbus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.hibernate.Session;

import persistence.Esclaves;
import persistence.Variables;
import connexionBase.SingletonSessionHSQL;

public class Construire
{
	private Session				sessionHQL	= SingletonSessionHSQL.getInstance ().getSession ();
	private ArrayList<Blocks>	vecteur		= new ArrayList<Blocks> ();

	public Construire () throws Exception
	{
		this.sessionHQL.createQuery ("DELETE FROM Variables ").executeUpdate ();
		this.creationEsclaves ();
		this.lirefichierVariables ("variables.csv");
		//this.creationBlock ();
		this.lectureComplete ();
		System.out.println ("okcomplet");
	}

	public void lirefichierVariables (String aFichier)
	{
		BufferedReader br;
		Variables var = new Variables ();
		String line;
		String csvSplitBy = ";";
		try
		{
			br = new BufferedReader (new FileReader (new File (aFichier)));
			while ( (line = br.readLine ()) != null)
			{
				if ( ! (line.startsWith ("idtag")))
				{
					String [] variable = line.split (csvSplitBy);
					var.setIdtag (Long.parseLong (variable [0]));
					var.setFormat (variable [1]);
					var.setType (variable [2]);
					var.setAdresse (Integer.parseInt (variable [3]));					
					var.setNumbit  (Integer.parseInt (variable [4]));
					var.setPeriode (variable[5]);
					var.setRecdelay (Integer.parseInt (variable [6]));
					var.setSlave ((Esclaves)this.sessionHQL.get (Esclaves.class, Long.parseLong (variable [7])));
					var.setTagname (variable [8]);
					var.setZone (variable [9]);
					var.setUserfield1 (variable [10]);
					var.setUserfield2 (variable [11]);
					var.setCoefficient (Float.parseFloat (variable [12]));
					var.setAffichage (variable [13]);
					var.setLibelle0 (variable [14]);
					var.setLibelle1 (variable [15]);
					var.setLibelleEtendu (variable [16]);
					var.setAstreinte (Integer.parseInt (variable [17]));
					this.sessionHQL.merge (var);
				}
			}
			this.sessionHQL.flush ();
		}
		catch (FileNotFoundException e)
		{
			JOptionPane.showMessageDialog (null, e.getMessage (), "Erreur", JOptionPane.ERROR_MESSAGE);
			System.exit ( - 1);
		}
		catch (IOException e)
		{
			e.printStackTrace ();
		}
		catch (NumberFormatException aNfe)
		{
			JOptionPane.showMessageDialog (null, "Erreur nfe "+ aNfe.getMessage (), "Erreur", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void lectureComplete ()
	{
		
		String query = "SELECT v.format, min(v.adresse), max(v.adresse), s.idslave "
		+ "from Variables v join v.Slave s on s.idslave = idslave "
		+ "group by v.format , s.idslave order by 1, 3";
		Blocks bm;
		int amax;
		int amin;
		Esclaves esclave;
		ConnexionModbusTCP conTCP;
		List<Object []> list = this.sessionHQL.createQuery (query).list ();
		for (Object[] t : list)
		{	
			System.out.println ((String)t[0]);
			System.out.println ((int)t[1]);
			System.out.println ((int)t[2]);
			amax = (int)t [2];
			amin = (int)t [1];
			esclave = (Esclaves) this.sessionHQL.get (Esclaves.class, Long.parseLong (t[3].toString ()));
			conTCP = esclave.connexionModbusTCP ();
			while (amin < amax + 1)
			{
				switch (t [0].toString ())
				{		
					case "REAL" :
					bm = new BlockReel ();
					bm.setFormat ("REAL");
					bm.setStartadress (amin);
					bm.setLongueur (123);
					bm.setFreq (1000L);
					bm.setSlave (esclave);
					bm.setConnModbus (conTCP);
					bm.initialize ();
					bm.start ();

					if (bm.getRow () != 0)
					{
						this.vecteur.add (bm);
					}
					break;
					
					case "INT" :
					bm = new BlockInt  ();
					bm.setFormat ("INT");
					bm.setStartadress (amin);
					bm.setLongueur (123);
					bm.setFreq (1000L);
					bm.setSlave (esclave);
					bm.setConnModbus (conTCP);
					bm.initialize ();
					bm.start ();

					if (bm.getRow () != 0)
					{
						this.vecteur.add (bm);
					}
					break;
					
					case "DINT" :
					bm = new BlockDInt ();
					bm.setFormat ("DINT");
					bm.setStartadress (amin);
					bm.setLongueur (123);
					bm.setFreq (1000L);
					bm.setSlave (esclave);
					bm.setConnModbus (conTCP);
					bm.initialize ();
					bm.start ();

					if (bm.getRow () != 0)
					{
						this.vecteur.add (bm);
					}
					break;

					case "WORD" :
					bm = new BlockWord ();
					bm.setFormat ("WORD");
					bm.setStartadress (amin);
					bm.setLongueur (123);
					bm.setFreq (1000L);
					bm.setSlave (esclave);
					bm.setConnModbus (conTCP);
					bm.initialize ();
					bm.start ();
						
					if (bm.getRow () != 0)
					{
						this.vecteur.add (bm);
						//System.out.println (this.vecteur);
					}
					break;
					
					case "DWORD" :
					bm = new BlockDWord ();
					bm.setFormat ("DWORD");
					bm.setStartadress (amin);
					bm.setLongueur (123);
					bm.setFreq (1000L);
					bm.setSlave (esclave);
					bm.setConnModbus (conTCP);
					bm.initialize ();
					bm.start ();
					if (bm.getRow () != 0)
					{
						this.vecteur.add (bm);
					}
					break;
					
					case "BIT" :
					bm = new BlockBit ();
					bm.setFormat ("BIT");
					bm.setStartadress (amin);
					bm.setLongueur (123);
					bm.setFreq (1000L);
					bm.setSlave (esclave);
					bm.setConnModbus (conTCP);
					bm.initialize ();
					bm.start ();
					if (bm.getRow () != 0)
					{
						this.vecteur.add (bm);
					}
					break;							
				}
				amin = amin + 122;
				System.out.println ("amin devient : " + amin);
				
				try
				{
					Thread.sleep (50L);
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}			
		}
	}

	
	public void creationEsclaves()
	{
		Esclaves esclaves = new Esclaves ();
		esclaves.setIdslave (2L);
		esclaves.setIpAdress ("LOCALHOST");
		esclaves.setName ("SOFREL");
		esclaves.setPort (502);
		esclaves.setTimeout (2000);
		
		this.sessionHQL.persist (esclaves);
		this.sessionHQL.flush ();
		System.out.println ("inserer");
	}

	public ArrayList<Blocks> getVecteur ()
	{
		return vecteur;
	}

	public void setVecteur (ArrayList<Blocks> vecteur)
	{
		this.vecteur = vecteur;
	}
}
