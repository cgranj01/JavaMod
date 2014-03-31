
package connexionBase;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ConnexionDBrec
{
	private static ConnexionDBrec	conn;
	private Statement			ordreSql;
	private PreparedStatement	pStatement;
	private CallableStatement	cStatement;
	private Connection			connexionSqlServer	= null;
	private boolean				connecte			= false;
	private DatabaseMetaData	metaDonnéesBase;
	private JDialog 			dialog;

	private ConnexionDBrec ()
	{
		try
		{
			Properties prop = new Properties();
			InputStream input = new FileInputStream("config.properties");		 
			prop.load(input);
			String aIP = prop.getProperty("database");
			String aUser = prop.getProperty("dbuser");
			String aPwd = prop.getProperty("dbpassword");
			String aDbName = prop.getProperty ("dbname");
			if (!(aIP.isEmpty ()))
			{
				Thread t = new Thread () {
					public void run()
					{ 
						dialog = null;
						JOptionPane jo;
						jo = new JOptionPane ("Connexion a la base de données en cours...",
								JOptionPane.INFORMATION_MESSAGE,JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
						dialog = jo.createDialog (null,"");
						dialog.setDefaultCloseOperation (JFrame.DO_NOTHING_ON_CLOSE);
						dialog.setModal (false);
						dialog.setVisible (true);										
					}					
				};
				t.start ();
				Class.forName ("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				System.out.println ("Pilote Chargé");
				String url = "jdbc:sqlserver://" + aIP + ";databaseName=" + aDbName;
				this.connexionSqlServer = DriverManager.getConnection (url, aUser, aPwd);
				
				this.connecte = true;
				dialog.dispose ();
				System.out.println ("Connection réussie " + this.connexionSqlServer.getClass ().getName ());
			}
			else
			{
				JOptionPane.showMessageDialog (null, "Aucune base de données paramétrée");
				
			}
		
		}
		catch (ClassNotFoundException aCNFE)
		{
			System.err.println ("Chargement du pilote impossible " + aCNFE.getMessage ());
			dialog.dispose ();
		}
		catch (FileNotFoundException e)
		{
			JOptionPane.showMessageDialog (null, e.getMessage (), "Erreur",  JOptionPane.ERROR_MESSAGE);
			dialog.dispose ();
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog (null, e.getMessage (), "Erreur",  JOptionPane.ERROR_MESSAGE);
			dialog.dispose ();
		}
		catch (SQLException aSQLE)
		{
			dialog.dispose ();
			JOptionPane.showMessageDialog (null, "Base de données inaccessible", "Erreur Acces Base", JOptionPane.ERROR_MESSAGE);
			
		}
		catch (Exception aE)
		{
			JOptionPane.showMessageDialog (null, aE.getMessage (), "Erreur Acces Base", JOptionPane.ERROR_MESSAGE);
			dialog.dispose ();
		}
	}

	public void closeConnexion ()
	{
		if (this.connexionSqlServer != null)
		{
			try
			{
				this.connexionSqlServer.close ();
				this.connecte = false;
			}
			catch (SQLException aSQLE)
			{
			}
			finally
			{
				this.connexionSqlServer = null;
				this.connecte = false;
			}
		}
	}
	public static ConnexionDBrec getInstance ()
	{
		if (ConnexionDBrec.conn == null)
		{			
			ConnexionDBrec.conn = new ConnexionDBrec();
		}
		return ConnexionDBrec.conn;
	}

	public static void setInstanceNull ()
	{
		ConnexionDBrec.conn = null;
	}

//	public PreparedStatement getpStatement (String aReqSQL)
//	{
//		if (this.isConnecte ())
//		{
//			try
//			{
//				this.pStatement = this.connexionSqlServer.prepareStatement (aReqSQL);
//				return this.pStatement;
//			}
//			catch (SQLException e)
//			{
//				System.out.println ("jesuisnull");
//				return null;
//			}
//		}
//		return this.pStatement;		
//	}

	public CallableStatement getCallableStatement (String aReqSQL)
	{
		try
		{
			this.cStatement = this.connexionSqlServer.prepareCall (aReqSQL, ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
		}
		catch (SQLException aSQLE)
		{
			return null;
		}
		return this.cStatement;
	}

	public Statement getOrdreSql ()
	{
		if (this.ordreSql != null)
		{
			return this.ordreSql;
		}
			
		if (this.isConnecte () && this.ordreSql == null)
		{
			try
			{
				this.ordreSql = this.connexionSqlServer.createStatement (ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			}
			catch (SQLException aSQLE)
			{
			return null;
			}
		}
		return this.ordreSql;
	}

	public boolean isConnecte ()
	{
		return this.connecte;
	}

	public DatabaseMetaData getMetaDonnéesBase ()
	{
		return this.metaDonnéesBase;
	}

	public Connection getConnexionSqlServer ()
	{
		return connexionSqlServer;
	}

	public void setConnexionSqlServer (Connection connexionSqlServer)
	{
		this.connexionSqlServer = connexionSqlServer;
	}

	public PreparedStatement getpStatement ()
	{
		return this.pStatement;
	}

	public void setpStatement (PreparedStatement pStatement)
	{
		this.pStatement = pStatement;
	}
}
