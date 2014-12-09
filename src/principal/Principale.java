
package principal;

import gui.FenetrePrincipale;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import connexionBase.SingletonSessionHSQL;
import connexionBase.SingletonSessionSQLServer;

public class Principale
{
	public static void main (String [] args)
	{
		try
		{
			UIManager.setLookAndFeel (UIManager.getSystemLookAndFeelClassName ());
			SingletonSessionHSQL.getInstance ();
			SingletonSessionSQLServer.getInstance ();
			new FenetrePrincipale ();
		}
	
		catch (IllegalAccessException | ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException e)
		{
			// handle exception
		}
		
	}
}
