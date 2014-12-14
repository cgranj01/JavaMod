
package modbus;

import gui.FenetrePrincipale;
import gui.MapTableModel;

import java.awt.Font;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JOptionPane;
import javax.swing.JTable;

import persistence.Variables;

public class Lecture extends Thread
{
	private Construire			construire;
	private FenetrePrincipale	fp;
	private static Lecture		lecture;
	private boolean				running			= true;
	private CopyOnWriteArrayList<Variables>		listvar			= new CopyOnWriteArrayList <Variables> ();
	private Map<String, Float>	hmafficheModif	= new TreeMap<> ();
	private MapTableModel		tm;
	private JTable				jtable			= new JTable ();

	private Lecture (FenetrePrincipale aFp)
	{
		this.fp = aFp;
		try
		{
			this.tm = new MapTableModel (this.hmafficheModif, "Variable", "Valeur");
			this.jtable.setModel (this.tm);
			this.jtable.setFont (new Font ("Tahoma", Font.PLAIN, 14));
			this.jtable.setRowHeight (30);
			this.jtable.setRowSelectionAllowed (true);
			this.fp.getDefileur ().setViewportView (this.jtable);
			this.construire = new Construire ();
			
			//this.demarrageLecture ();
			this.actualiseHM ();

		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog (null, e.getMessage (), "Avertissement", JOptionPane.ERROR_MESSAGE);
		}

	}

	public static Lecture getInstance (FenetrePrincipale aFp)
	{
		if (Lecture.lecture == null)
		{
			Lecture.lecture = new Lecture (aFp);
		}
		return Lecture.lecture;
	}

	public static Lecture getInstance ()
	{
		return Lecture.lecture;
	}

	public static void setInstanceNull ()
	{
		Lecture.lecture = null;
	}

	public void actualiseHM ()
	{
		this.listvar.clear ();
		for (Blocks bm : this.construire.getVecteur ())
		{
			this.listvar.addAll (bm.getListvar ());
		}

		for (Variables var : this.listvar)
		{
			if (var.getAffichage ().equals ("Y"))
			{
				this.hmafficheModif.put (var.getTagname (), var.getValeur ());
			}
		}

		// for (Entry<Integer, Variables> entreeaffiche :
		// this.treeMapVar.entrySet ())
		// {
		// if (entreeaffiche.getValue ().getAffichage ().equals ("Y"))
		// {
		// this.hmafficheModif.put (entreeaffiche.getValue ().getTagname (),
		// entreeaffiche.getValue ().getValeur ());
		// }
		// }
	}

//	public void demarrageLecture ()
//	{
//		synchronized (this)
//		{
//			for (Blocks bm : this.construire.getVecteur ())
//			{
//				System.out.println ("demarrage lecture " + bm.toString ());
//				bm.start ();
//				try
//				{
//					Thread.sleep (100L);
//				}
//				catch (InterruptedException e)
//				{
//					// TODO Auto-generated catch block
//					e.printStackTrace ();
//				}
//			}
//
//		}
//
//	}

	public void arretLecture ()
	{
		for (Blocks bm : this.construire.getVecteur ())
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
			for (Blocks bm : this.construire.getVecteur ())
			{
				if (bm.isRunning () == false)
				{
					bm.getSlave ();
					this.fp.setInfoCourante (" Erreur de requetes sur esclave " + bm.getSlave ().getIdslave ());
					this.arretLecture ();
				}
			}

//			if (this.fp.getEnregistrement ().isRecsurfichier ())
//			{
//				this.fp.setInfoCourante (" Aucune base connectée");
//				this.fp.getBarreDeMenu ().getActionLecture ().setEnabled (false);
//			}

			this.actualiseHM ();
			this.fp.getDefileur ().setViewportView (this.jtable);
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

	public CopyOnWriteArrayList<Variables> getListvar ()
	{
		return listvar;
	}

	public void setListvar (CopyOnWriteArrayList<Variables> listvar)
	{
		this.listvar = listvar;
	}

}
