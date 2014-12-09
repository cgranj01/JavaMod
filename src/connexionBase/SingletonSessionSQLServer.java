package connexionBase;

import javax.swing.JOptionPane;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class SingletonSessionSQLServer
{
	private static SingletonSessionSQLServer	applicationInstance;

	public static SingletonSessionSQLServer getInstance ()
	{
		if (applicationInstance == null)
		{
			applicationInstance = new SingletonSessionSQLServer ();
		}
		return SingletonSessionSQLServer.applicationInstance;
	}

	/*
	 * Méthode permettant d'instancier la connexion via le contructeur avec 2
	 * arguments dans le but de paramétrer la configuration Hibernate avant la
	 * construction de session.
	 */
	public static SingletonSessionSQLServer getInstance (String aEnvironnement, String aValeur)
	{
		if (applicationInstance == null)
		{
			applicationInstance = new SingletonSessionSQLServer (aEnvironnement, aValeur);
		}
		return applicationInstance;
	}

	private Configuration	configuration;
	private SessionFactory	sessionFactory;
	public static Session			session;

	private SingletonSessionSQLServer ()
	{
		this.configuration = new Configuration ().configure ("hibernateSQLServer.cfg.xml");
		configuration.setProperty			("hibernate.show_sql", "false");
		configuration.setProperty			("hibernate.format_sql", "false");
		ServiceRegistry serviceRegistry = new ServiceRegistryBuilder ().applySettings (
							this.configuration.getProperties ()).buildServiceRegistry ();
		this.sessionFactory = configuration.buildSessionFactory (serviceRegistry);
		this.session = sessionFactory.openSession ();
	}

	private SingletonSessionSQLServer (String aEnvironnement, String aValeur)
	{
		this.configuration = new Configuration ().configure ();
		this.configuration.setProperty (aEnvironnement, aValeur);
		ServiceRegistry serviceRegistry = new ServiceRegistryBuilder ().applySettings (
							this.configuration.getProperties ()).buildServiceRegistry ();
		this.sessionFactory = configuration.buildSessionFactory (serviceRegistry);
		this.session = sessionFactory.openSession ();
	}

	public void closeSession ()
	{
		if (this.isConnected ())
		{
			try
			{
				this.session.close ();
			}
			catch (HibernateException aHE)
			{
				JOptionPane.showMessageDialog (null,
						"Problème grave à la fermeture de la base :\n" + aHE.getMessage (), "Problème grave",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			SingletonSessionSQLServer.applicationInstance = null;
			System.gc ();
		}
	}

	/* Méthode de test de connexion retournant un booléen */
	public boolean isConnected ()
	{
		if (SingletonSessionSQLServer.applicationInstance == null)
		{
			return false;
		}
		return this.session.isConnected ();
	}

	public Configuration getConfiguration ()
	{
		return configuration;
	}

	public SessionFactory getSessionFactory ()
	{
		return sessionFactory;
	}

	public Session getSession ()
	{
		if(this.session.isConnected ())
			return this.session;
		
		this.session = sessionFactory.openSession ();
		return this.session;
	}

}
