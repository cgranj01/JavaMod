
package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public class BarreDeMenu extends JMenuBar implements PropertyChangeListener, ActionListener
{
	private Traitements			traitements		= Traitements.getInstance ();
	private JMenu				menuFichier		= new JMenu ("Fichier");
	private JMenu				menuParametres	= new JMenu ("Parametres");
	private JToolBar			barreOutils		= new JToolBar ();
	private FenetrePrincipale	fenetrePrincipale;

	private AbstractAction		actionQuitter;
	private AbstractAction		actionLecture;
	private AbstractAction		actionPause;
	private AbstractAction		actionComModbus;

	public BarreDeMenu (FenetrePrincipale aFp)
	{
		this.fenetrePrincipale = aFp;

		this.menuFichier.setMnemonic (KeyEvent.VK_F);
		this.menuParametres.setMnemonic (KeyEvent.VK_P);
		this.initialiseActions ();

		this.menuFichier.addSeparator ();
		this.menuFichier.add (this.actionQuitter);

		this.menuParametres.add (this.actionComModbus);
		this.menuParametres.addSeparator ();

		this.menuParametres.add (this.actionLecture);
		this.menuParametres.add (this.actionPause);

		this.add (this.menuFichier);
		this.add (this.menuParametres);

	}

	public JToolBar getBarreOutils ()
	{
		this.barreOutils.add (this.actionComModbus);
		this.barreOutils.addSeparator ();
		this.barreOutils.add (this.actionLecture);
		this.barreOutils.add (this.actionPause);
		this.barreOutils.addSeparator ();
		this.barreOutils.add (this.actionQuitter);
		this.barreOutils.setRollover (true);
		this.barreOutils.setFloatable (false);
		return this.barreOutils;
	}

	private void initialiseActions ()
	{
		this.traitements.ajouteEcouteur (this);

		this.actionQuitter = new ActionsCourantes ("Quitter", "Terminer cette application", "quitter",
				"Permet de quitter cette application", KeyStroke.getKeyStroke (KeyEvent.VK_Q, InputEvent.CTRL_MASK),
				KeyEvent.VK_Q, "fermer16.png", "fermer32.png", this);

		this.actionLecture = new ActionsCourantes ("Exploitation", "Exploitation", "lecture", "Mode exploitation",
				KeyStroke.getKeyStroke (KeyEvent.VK_E, InputEvent.CTRL_MASK), KeyEvent.VK_E, "enbase16.png",
				"enbase32.png", this);

		this.actionPause = new ActionsCourantes ("Configuration", "Configuration", "pause", "Mode configuration",
				KeyStroke.getKeyStroke (KeyEvent.VK_N, InputEvent.CTRL_MASK), KeyEvent.VK_N, "pause16.png",
				"pause32.png", this);

		this.actionComModbus = new ActionsCourantes ("Clients ModbusIP", "Parametre client modbus", "comModbus",
				"Parametre client modbus", KeyStroke.getKeyStroke (KeyEvent.VK_S, InputEvent.CTRL_MASK), KeyEvent.VK_S,
				"parametres16.png", "parametres32.png", this);
	}

	public AbstractAction getActionQuitter ()
	{
		return this.actionQuitter;
	}

	public JMenu getMenuFichier ()
	{
		return menuFichier;
	}

	public void setMenuFichier (JMenu menuFichier)
	{
		this.menuFichier = menuFichier;
	}

	public JMenu getMenuParametres ()
	{
		return this.menuParametres;
	}

	public void setMenuParametres (JMenu menuParametres)
	{
		this.menuParametres = menuParametres;
	}

	@Override
	public void propertyChange (PropertyChangeEvent evt)
	{

	}

	public FenetrePrincipale getFenetrePrincipale ()
	{
		return fenetrePrincipale;
	}

	public void setFenetrePrincipale (FenetrePrincipale fenetrePrincipale)
	{
		this.fenetrePrincipale = fenetrePrincipale;
	}

	@Override
	public void actionPerformed (ActionEvent aAction)
	{
		
	}

	public AbstractAction getActionLecture ()
	{
		return actionLecture;
	}

	public void setActionLecture (AbstractAction actionLecture)
	{
		this.actionLecture = actionLecture;
	}
}