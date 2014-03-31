
package utilitaires;

import java.awt.Color;
import java.awt.Font;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

public final class Tools
{
	private static SimpleDateFormat	formatDate	= new SimpleDateFormat ("dd/MM/yy");
	private static Pattern			motifSS		= Pattern.compile ("^(1|2)\\d\\d((0[1-9])|(1[0-2]))\\d{10}+$");

	static
	{
		Tools.formatDate.setLenient (false);
	}

	public static GregorianCalendar contr�leDate (String aDate)
	{
		ParsePosition d�butAnalyse = new ParsePosition (0);
		Date dateSaisie = null;
		GregorianCalendar dateNaissance = new GregorianCalendar ();
		GregorianCalendar maintenant = new GregorianCalendar ();

		dateSaisie = Tools.formatDate.parse (aDate, d�butAnalyse);

		if (dateSaisie == null)
			return null;
		if (d�butAnalyse.getIndex () != aDate.length ())
			return null;

		dateNaissance.setTime (dateSaisie);
		maintenant.setTime (new Date ());
		int �ge = maintenant.get (Calendar.YEAR) - dateNaissance.get (Calendar.YEAR);
		if (�ge < 1)
			return null;
		if (maintenant.get (Calendar.MONTH) < dateNaissance.get (Calendar.MONTH))
		{
			�ge -- ;
		}
		else
		{
			if (maintenant.get (Calendar.MONTH) == dateNaissance.get (Calendar.MONTH))
			{
				if (maintenant.get (Calendar.DAY_OF_MONTH) < dateNaissance.get (Calendar.DAY_OF_MONTH))
					�ge -- ;
			}
		}
		if (�ge < 16 || �ge > 70)
			return null;
		return dateNaissance;
	}

	public static Double contr�leDouble (String aValeurDouble, double aBorneMin, double aBorneMax)
	{
		try
		{
			Double valeurRetourn�e = new Double (aValeurDouble);
			if (valeurRetourn�e < aBorneMin || valeurRetourn�e > aBorneMax)
			{
				throw new NumberFormatException ("Valeur hors borne");
			}
			return valeurRetourn�e;
		}
		catch (NumberFormatException aNFE)
		{
			return null;
		}
	}

	public static String contr�leInsee (String aNum�roInsee)
	{
		if ( ! Tools.motifSS.matcher (aNum�roInsee).matches ())
			return null;

		return aNum�roInsee;
	}

	public static Integer contr�leInt (String aEntier, int aBorneMin, int aBorneMax)
	{
		try
		{
			Integer valeurRetourn�e = new Integer (aEntier);
			if (valeurRetourn�e < aBorneMin || valeurRetourn�e > aBorneMax)
			{
				throw new NumberFormatException ("Valeur hors bornes");
			}
			return valeurRetourn�e;
		}
		catch (NumberFormatException aNFE)
		{
			return null;
		}
	}

	public static boolean contr�leTexte (String aTexte, int aLongueurMin, int aLongueurMax)
	{
		if (aTexte.length () < aLongueurMin || aTexte.length () > aLongueurMax)
		{
			return false;
		}
		if ( ! Character.isUpperCase (aTexte.charAt (0)))
		{
			return false;
		}
		return true;
	}

	public static void d�finitFonte (JComponent aZone)
	{
		Tools.d�finitFonte (aZone, "Arial", 14);
	}

	public static void d�finitFonte (JComponent aZone, int aTaille)
	{
		Tools.d�finitFonte (aZone, "Arial", aTaille);
	}

	public static void d�finitFonte (JComponent aZone, String aPolice, int aTaille)
	{
		aZone.setFont (new Font (aPolice, Font.BOLD, aTaille));
		aZone.setForeground (Color.BLUE);
	}

	public static void faitBordureEtTexte (JComponent aControle, String aTitre)
	{
		aControle.setBorder (BorderFactory.createCompoundBorder (BorderFactory.createEmptyBorder (3, 3, 3, 3),
				BorderFactory.createTitledBorder (BorderFactory.createEtchedBorder (), aTitre)));
	}

	public static void faitBordureEtTexte (JComponent aControle, String aTitre, Color aCouleur)
	{
		aControle.setBorder (BorderFactory.createCompoundBorder (BorderFactory.createEmptyBorder (3, 3, 3, 3),
				BorderFactory.createTitledBorder (BorderFactory.createLineBorder (aCouleur), aTitre)));
	}

	public static String formatGregorian (GregorianCalendar aDate)
	{
		return aDate.get (Calendar.DAY_OF_MONTH) + "/" + (aDate.get (Calendar.MONTH) + 1) + "/"
				+ aDate.get (Calendar.YEAR);
	}

	private Tools ()
	{
	}
}