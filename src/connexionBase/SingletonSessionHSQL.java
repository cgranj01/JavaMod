package connexionBase;

import javax.swing.JOptionPane;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class SingletonSessionHSQL
{
	private static SingletonSessionHSQL	applicationInstance;

	public static SingletonSessionHSQL getInstance ()
	{
		if (applicationInstance == null)
		{
			applicationInstance = new SingletonSessionHSQL ();
		}
		return SingletonSessionHSQL.applicationInstance;
	}

	/*
	 * Méthode permettant d'instancier la connexion via le contructeur avec 2
	 * arguments dans le but de paramétrer la configuration Hibernate avant la
	 * construction de session.
	 */
	public static SingletonSessionHSQL getInstance (String aEnvironnement, String aValeur)
	{
		if (applicationInstance == null)
		{
			applicationInstance = new SingletonSessionHSQL (aEnvironnement, aValeur);
		}
		return applicationInstance;
	}

	private Configuration	configuration;
	private SessionFactory	sessionFactory;
	public static Session			session;

	private SingletonSessionHSQL ()
	{
		this.configuration = new Configuration ().configure ("hibernateHQL.cfg.xml");
		configuration.setProperty			("hibernate.show_sql", "true");
		configuration.setProperty			("hibernate.format_sql", "false");
		ServiceRegistry serviceRegistry = new ServiceRegistryBuilder ().applySettings (
							this.configuration.getProperties ()).buildServiceRegistry ();
		this.sessionFactory = configuration.buildSessionFactory (serviceRegistry);
		this.session = sessionFactory.openSession ();
	}

	private SingletonSessionHSQL (String aEnvironnement, String aValeur)
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
			SingletonSessionHSQL.applicationInstance = null;
			System.gc ();
		}
	}

	/* Méthode de test de connexion retournant un booléen */
	public boolean isConnected ()
	{
		if (SingletonSessionHSQL.applicationInstance == null)
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
