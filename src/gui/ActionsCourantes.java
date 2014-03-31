
package gui;

import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public class ActionsCourantes extends AbstractAction
{
	private Traitements			traitements	= Traitements.getInstance ();
	private BarreDeMenu			barreDesMenus;
	//private FenetrePrincipale	fenetrePrincipale;

	public ActionsCourantes (String aNom, String aDescription, String aActionCommande, String aTexteCourt,
			KeyStroke aAccelerator, int aMnemonique, String aImageSmall, String aImageLarge, BarreDeMenu aBarre)
	{
		super (aNom);
		this.putValue (Action.LONG_DESCRIPTION, aDescription);
		this.putValue (Action.ACTION_COMMAND_KEY, aActionCommande);
		this.putValue (Action.SHORT_DESCRIPTION, aTexteCourt);
		this.putValue (Action.ACCELERATOR_KEY, aAccelerator);
		this.putValue (Action.MNEMONIC_KEY, aMnemonique);
		this.setBarreDesMenus (aBarre);
		
		String imgLocation = "/images/" + aImageSmall;
		URL imageURL = this.getClass ().getResource (imgLocation);
		ImageIcon icone = new ImageIcon (imageURL, aNom);
		this.putValue (Action.SMALL_ICON, icone);

		imgLocation = "/images/" + aImageLarge;
		imageURL = this.getClass ().getResource (imgLocation);
		icone = new ImageIcon (imageURL, aNom);
		this.putValue (Action.LARGE_ICON_KEY, icone);

	}

	public void actionPerformed (ActionEvent aAction)
	{
		String cause = aAction.getActionCommand ();
		if ("quitter".equals (cause))
		{
			this.traitements.quitter ();
			return;
		}
		if ("lecture".equals (cause))
		{
			this.traitements.lecture ();
			return;
		}
		if ("pause".equals (cause))
		{
			this.traitements.pause ();
			return;
		}
		if ("comModbus".equals (cause))
		{
			this.traitements.com ();
			return;
		}
		if ("rouvrir".equals (cause))
		{
			this.traitements.rouvrir ();
			return;
		}
	}

	public BarreDeMenu getBarreDesMenus ()
	{
		return barreDesMenus;
	}

	public void setBarreDesMenus (BarreDeMenu barreDesMenus)
	{
		this.barreDesMenus = barreDesMenus;
	}
}
