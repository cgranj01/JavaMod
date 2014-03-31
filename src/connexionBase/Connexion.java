
package connexionBase;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

public class Connexion
{
	private static Connexion	conn;
	private Statement			ordreSql;
	private PreparedStatement	pStatement;
	private CallableStatement	cStatement;
	private Connection			connexionSqlite	= null;
	private boolean				connecte			= false;
	private DatabaseMetaData	metaDonnéesBase;

	private Connexion ()
	{
		try
		{
			Class.forName ("org.sqlite.JDBC");
			System.out.println ("Pilote Chargé");
			this.connexionSqlite = DriverManager.getConnection ("jdbc:sqlite:dbjmaitre.db");
			this.connexionSqlite.setAutoCommit (false);
			this.connecte = true;

			System.out.println ("Connection réussie " + this.connexionSqlite.getClass ().getName ());

		}
		catch (ClassNotFoundException aCNFE)
		{
			System.err.println ("Chargement du pilote impossible " + aCNFE.getMessage ());
			return;
		}
		catch (SQLException aSQLE)
		{
			JOptionPane.showMessageDialog (null, aSQLE.getMessage (), "Erreur Acces Base", JOptionPane.ERROR_MESSAGE);
			System.exit ( - 1);
		}
		catch (Exception aE)
		{
			JOptionPane.showMessageDialog (null, aE.getMessage (), "Erreur Acces Base", JOptionPane.ERROR_MESSAGE);
			System.exit ( - 1);
		}
	}

	public void closeConnexion ()
	{
		if (this.connexionSqlite != null)
		{
			try
			{
				this.connexionSqlite.close ();
				this.connecte = false;
			}
			catch (SQLException aSQLE)
			{
			}
			finally
			{
				this.connexionSqlite = null;
				this.connecte = false;
			}
		}
	}
	public static Connexion getInstance ()
	{
		
		if (Connexion.conn == null)
		{
			synchronized (Connexion.class)
			{
				Connexion.conn = new Connexion();
			}
			
		}
		return Connexion.conn;
	}

	public static void setInstanceNull ()
	{
		Connexion.conn = null;
	}

	public PreparedStatement getpStatement (String aReqSQL)
	{

		try
		{
			this.pStatement = this.connexionSqlite.prepareStatement (aReqSQL);
		}
		catch (SQLException e)
		{
			e.printStackTrace ();
		}
		return this.pStatement;
	}

	public CallableStatement getCallableStatement (String aReqSQL)
	{
		try
		{
			this.cStatement = this.connexionSqlite.prepareCall (aReqSQL, ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			System.out.println (this.cStatement);
		}
		catch (SQLException aSQLE)
		{
			System.out.println ("erreur getcallablestatement");
			return null;
		}
		return this.cStatement;
	}

	public void setPStatementNull ()
	{
		if (this.pStatement != null)
			this.pStatement = null;
	}

	public Statement getOrdreSql ()
	{
		// if (this.ordreSql != null)
		// return this.ordreSql;
		//
		// if (this.isConnecte () && this.ordreSql == null)
		// {
		try
		{
			this.ordreSql = this.connexionSqlite.createStatement ();
		}
		catch (SQLException aSQLE)
		{
			System.out.println (aSQLE.getMessage ());
			return null;
			
		}
		// }
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

	public Connection getConnexionSqlite ()
	{
		return connexionSqlite;
	}

	public void setConnexionSqlite (Connection connexionSqlite)
	{
		this.connexionSqlite = connexionSqlite;
	}
}
