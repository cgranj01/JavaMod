
package principal;

import gui.FenetrePrincipale;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import connexionBase.Connexion;
import connexionBase.ConnexionDBrec;

public class Principale
{
	public static void main (String [] args)
	{
		try
		{
			UIManager.setLookAndFeel (UIManager.getSystemLookAndFeelClassName ());
			Connexion.getInstance();		
			ConnexionDBrec cdb = ConnexionDBrec.getInstance ();
			String testtable = "select count(*) FROM INFORMATION_SCHEMA.TABLES where TABLE_NAME ='enr_variables'";
			if (cdb.isConnecte ())
			{
				ResultSet res = cdb.getOrdreSql ().executeQuery (testtable);
				while (res.next ())
				{
					if (res.getInt (1)==0)
					{
						String ctable = "CREATE TABLE enr_variables (id_var int, tagname nvarchar(50), text1 nvarchar(150), " +
								"text2 nvarchar(150), text3 nvarchar(150), valeur nvarchar(50), adresse nvarchar(50), horodatage datetime)";
						cdb.getOrdreSql ().execute (ctable);
					}					
				}		
			}		
			new FenetrePrincipale ();
		}
	
		catch (IllegalAccessException | ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | SQLException e)
		{
			// handle exception
		}
		
	}
}
