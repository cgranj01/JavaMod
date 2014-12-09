
package principal;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

import modbus.Lecture;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import persistence.Variables;
import persitenceRec.Datarec;
import connexionBase.SingletonSessionSQLServer;

public class Enregistrement extends Thread
{
	private Lecture							lecture				= Lecture.getInstance ();
	private Session							sessionSQLServer	= SingletonSessionSQLServer.getInstance ()
																		.getSession ();
	private SimpleDateFormat				dtf					= new SimpleDateFormat ("dd/MM/yyyy HH:mm:ss");
	private boolean							recsurfichier		= false;
	private CopyOnWriteArrayList<Variables>	listvar;
	private BufferedWriter					bw;

	public Enregistrement ()
	{
		this.preparationFichierBuffer ();

	}

	public void run ()
	{
		Float dernierevaleur = 0f;
		while (true)
		{
			System.out.println ("ENREGISTREMENT");
			this.listvar = this.lecture.getListvar ();
			int s = LocalDateTime.now ().getSecond ();
			int m = LocalDateTime.now ().getMinute ();
			Date localedatetime = Date.from (Instant.now ());
			String dateString = this.dtf.format (localedatetime);
			System.out.println (dateString);

			for (Variables var : this.listvar)
			{

				// System.out.println (var.getPeriode ());
				if (s == 0 && var.getRecdelay () == 60)
				{
					this.insertion (var.getAdresse (), var.getValeur (), var.getTagname (), var.getUserfield1 (),
							var.getUserfield2 (), var.getZone (), var.getIdtag (), dateString);
				}
				if (m == 0 && s == 0 && var.getRecdelay () == 3600)
				{
					this.insertion (var.getAdresse (), var.getValeur (), var.getTagname (), var.getUserfield1 (),
							var.getUserfield2 (), var.getZone (), var.getIdtag (), dateString);
				}
				if ( ("C".equals (var.getPeriode ())) && (var.getValeur () != dernierevaleur))
				{
					this.insertion (var.getAdresse (), var.getValeur (), var.getTagname (), var.getUserfield1 (),
							var.getUserfield2 (), var.getZone (), var.getIdtag (), dateString);
					dernierevaleur = var.getValeur ();
				}

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

	public void insertion (Integer aAdresse, Float aValeur, String aTagname, String aText1, String aText2,
			String aZone, Long aId_var, String aHorodatage)
	{
		try
		{
			Datarec datarec = new Datarec ();
			datarec.setAdresse (aAdresse);
			datarec.setTagname (aTagname);
			datarec.setZone (aZone);
			datarec.setUserfield1 (aText1);
			datarec.setUserfield1 (aText2);
			datarec.setValeur (aValeur);
			datarec.setHorodatage (this.dtf.parse (aHorodatage));
			this.sessionSQLServer.persist (datarec);
			this.sessionSQLServer.flush ();
			System.out.println (aTagname);
		}

		catch (HibernateException aHe)
		{
			System.err.println (aHe.getMessage ());
			// this.recupereValeur (aAdresse, aValeur, aTagname, aText1, aText2,
			// aZone, aId_var, aHorodatage);
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void recupereValeur (String aAdresse, String aValeur, String aTagname, String aText1, String aText2,
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
			e.printStackTrace ();
		}
	}

	public void preparationFichierBuffer ()
	{
		File file = new File ("bufferErrors.sql");
		FileWriter writer = null;
		try
		{
			if ( ! file.exists ())
			{
				file.createNewFile ();
			}
			writer = new FileWriter (file, true);
			this.bw = new BufferedWriter (writer);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace ();
		}

	}

	public boolean isRecsurfichier ()
	{
		return recsurfichier;
	}

	public void setRecsurfichier (boolean recsurfichier)
	{
		this.recsurfichier = recsurfichier;
	}

}
