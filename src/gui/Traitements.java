package gui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Traitements
{
	private static Traitements			instance;
	private PropertyChangeSupport		changeur;

	// private Vector <Personne> service;
	private Traitements ()
	{
		this.changeur = new PropertyChangeSupport (this);
		
	}

	public void ajouteEcouteur (PropertyChangeListener aEcouteur)
	{
		this.changeur.addPropertyChangeListener (aEcouteur);
	}

	public void ajouteEcouteur (String aCause, PropertyChangeListener aEcouteur)
	{
		this.changeur.addPropertyChangeListener (aCause, aEcouteur);
	}
	
	public void supprimeEcouteur (PropertyChangeListener aEcouteur)
	{
		this.changeur.removePropertyChangeListener (aEcouteur);
	}

	public void fichierVariable ()
	{
		this.changeur.firePropertyChange ("variables", null, null);
	}

	public void lecture ()
	{
		this.changeur.firePropertyChange ("lecture", null, null);
	}
	public void pause ()
	{
		this.changeur.firePropertyChange ("pause", null, null);
	}
	public void com ()
	{
		this.changeur.firePropertyChange ("comModbus", null, null);
	}
	public void quitter ()
	{
		this.changeur.firePropertyChange ("quitter", null, null);
	}
	public void rouvrir ()
	{
		this.changeur.firePropertyChange ("rouvrir", null, null);
	}
	
	public static Traitements getInstance ()
	{
		if (Traitements.instance == null)
			Traitements.instance = new Traitements ();
		return Traitements.instance;
	}
}
