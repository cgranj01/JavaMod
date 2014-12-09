
package gui;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import modbus.Lecture;
import persistence.Variables;
import principal.Enregistrement;
import connexionBase.SingletonSessionHSQL;

@SuppressWarnings("serial")
public class FenetrePrincipale extends JFrame
		implements
			WindowListener,
			PropertyChangeListener,
			WindowStateListener,
			ActionListener
{
	private BarreDeMenu		barreDeMenu;
	private Lecture			lecture;
	private Enregistrement	enregistrement;
	private Traitements		traitements		= Traitements.getInstance ();
	private JLabel			infoCourante	= new JLabel (" ");
	private TrayIcon		trayIcon;
	private Image			image;
	private SystemTray		tray;
	private JScrollPane		defileur		= new JScrollPane ();

	public FenetrePrincipale ()
	{
		super ("JMaitre");
		this.initialisationInterface ();
		this.traitements.ajouteEcouteur (this.barreDeMenu);
		this.traitements.ajouteEcouteur (this);
		this.infoCourante.setHorizontalAlignment (SwingConstants.LEFT);
		this.recupereErrorsSql ();
		this.lire ();

		this.setDefaultCloseOperation (JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener (this);
		this.initialisationInterface ();
		// this.setExtendedState (JFrame.MAXIMIZED_BOTH);
		// this.setPreferredSize (new Dimension (820, 700));

		this.pack ();
		this.setLocationRelativeTo (null);
		this.setVisible (true);
	}

	private void constructionTrayIcon ()
	{
		PopupMenu popup = new PopupMenu ();
		MenuItem MIrouvrir = new MenuItem ("Ouvrir");
		popup.add (MIrouvrir);
		MIrouvrir.addActionListener (this);
		MIrouvrir.setActionCommand ("rouvrir");
		if (SystemTray.isSupported ())
		{
			this.tray = SystemTray.getSystemTray ();
			this.trayIcon = new TrayIcon (this.image, "JMaitre", popup);
			this.trayIcon.setImageAutoSize (true);
		}
	}

	private void initialisationInterface ()
	{
		this.image = new ImageIcon (FenetrePrincipale.class.getResource ("/images/enbase32.png")).getImage ();
		this.setIconImage (image);
		this.barreDeMenu = new BarreDeMenu (this);
		this.setJMenuBar (this.barreDeMenu);
		this.constructionTrayIcon ();
		this.add (this.barreDeMenu.getBarreOutils (), BorderLayout.PAGE_START);
		this.add (this.infoCourante, BorderLayout.SOUTH);
		this.add (this.defileur);
		this.setResizable (false);
		// this.setSize (160, 50);
	}

	public void lire ()
	{
		this.lecture = Lecture.getInstance (this);
		this.lecture.start ();
		this.enregistrement = new Enregistrement ();
		this.enregistrement.start ();

	}

	public void recupereErrorsSql ()
	{
		File file = new File ("bufferErrors.sql");
		if (file.exists ())
		{
			String insertSQL = "insert into enr_variables "
					+ "(id_var, tagname, text1, text2, text3, adresse, valeur, horodatage)"
					+ " values (?,?,?,?,?,?,?,?)";
			BufferedReader br;
			String currentline;
			String csvSplitBy = ";";
			SingletonSessionHSQL cdbrec = SingletonSessionHSQL.getInstance ();
			if (cdbrec.isConnected ())
			{

				try
				{
					//PreparedStatement ps = cdbrec.getConnexionSqlServer ().prepareStatement (insertSQL);
					br = new BufferedReader (new FileReader (file));
					while ( (currentline = br.readLine ()) != null)
					{
						Variables var = new Variables ();
						String [] variable = currentline.split (csvSplitBy);
						var.setIdtag (Long.parseLong (variable [0]));
						var.setType (variable [1]);
						
					}
					br.close ();
					file.delete ();
				}
				catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace ();
				}
			}
		}
	}

	public void confirmationQuitter ()
	{
		int reponse = JOptionPane.showConfirmDialog (null, "Voulez vous vraiment quitter l'application ?",
				"Fermeture application", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (reponse == JOptionPane.YES_OPTION)
		{
			System.exit (0);
		}
	}

	@Override
	public void windowActivated (WindowEvent arg0)
	{

	}

	@Override
	public void windowClosed (WindowEvent arg0)
	{

	}

	@Override
	public void windowClosing (WindowEvent arg0)
	{
		this.confirmationQuitter ();
	}

	@Override
	public void windowDeactivated (WindowEvent arg0)
	{

	}

	@Override
	public void windowDeiconified (WindowEvent arg0)
	{

	}

	@Override
	public void windowIconified (WindowEvent e)
	{
		try
		{
			this.tray.add (trayIcon);
		}
		catch (AWTException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace ();
		}
		this.setVisible (false);
	}

	@Override
	public void windowOpened (WindowEvent arg0)
	{

	}

	@Override
	public void propertyChange (PropertyChangeEvent evt)
	{
		String cause = evt.getPropertyName ();

		if (cause.equals ("comModbus"))
		{
			new DialogParamComModbus ();
			return;
		}

		if (cause.equals ("course"))
		{

			// this.pCourant.removeAll ();
			// this.pCourant.add (this.pCourse);
			// this.pCourant.repaint ();
			// this.pack ();
			return;
		}

		if (cause.equals ("manuel"))
		{
			// this.pCourant.removeAll ();
			// this.pCourant.add (new PanneauBoutonPassage ());
			// this.pCourant.repaint ();
			// this.pack ();
			return;
		}

		if (cause.equals ("lecture"))
		{
			this.lire ();
			return;
		}

		if (cause.equals ("pause"))
		{
			if (Lecture.getInstance () != null)
			{
				Lecture.getInstance ().arretLecture ();
				Lecture.setInstanceNull ();
			}
			return;
		}

		if (cause.equals ("quitter"))
		{
			this.confirmationQuitter ();
		}

	}

	public JLabel getInfoCourante ()
	{
		return infoCourante;
	}

	public void setInfoCourante (String aInfoCourante)
	{
		this.infoCourante.setText (aInfoCourante);
	}

	@Override
	public void windowStateChanged (WindowEvent e)
	{

	}

	@Override
	public void actionPerformed (ActionEvent aAction)
	{
		String cause = aAction.getActionCommand ();
		if ("rouvrir".equals (cause))
		{
			this.tray.remove (this.trayIcon);
			this.setExtendedState (JFrame.NORMAL);
			this.setVisible (true);
		}
	}

	public BarreDeMenu getBarreDeMenu ()
	{
		return barreDeMenu;
	}

	public void setBarreDeMenu (BarreDeMenu barreDeMenu)
	{
		this.barreDeMenu = barreDeMenu;
	}

	public JScrollPane getDefileur ()
	{
		return defileur;
	}

	public void setDefileur (JScrollPane defileur)
	{
		this.defileur = defileur;
	}

	public Enregistrement getEnregistrement ()
	{
		return enregistrement;
	}

	public void setEnregistrement (Enregistrement enregistrement)
	{
		this.enregistrement = enregistrement;
	}
}
