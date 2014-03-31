
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

	public static GregorianCalendar contrôleDate (String aDate)
	{
		ParsePosition débutAnalyse = new ParsePosition (0);
		Date dateSaisie = null;
		GregorianCalendar dateNaissance = new GregorianCalendar ();
		GregorianCalendar maintenant = new GregorianCalendar ();

		dateSaisie = Tools.formatDate.parse (aDate, débutAnalyse);

		if (dateSaisie == null)
			return null;
		if (débutAnalyse.getIndex () != aDate.length ())
			return null;

		dateNaissance.setTime (dateSaisie);
		maintenant.setTime (new Date ());
		int âge = maintenant.get (Calendar.YEAR) - dateNaissance.get (Calendar.YEAR);
		if (âge < 1)
			return null;
		if (maintenant.get (Calendar.MONTH) < dateNaissance.get (Calendar.MONTH))
		{
			âge -- ;
		}
		else
		{
			if (maintenant.get (Calendar.MONTH) == dateNaissance.get (Calendar.MONTH))
			{
				if (maintenant.get (Calendar.DAY_OF_MONTH) < dateNaissance.get (Calendar.DAY_OF_MONTH))
					âge -- ;
			}
		}
		if (âge < 16 || âge > 70)
			return null;
		return dateNaissance;
	}

	public static Double contrôleDouble (String aValeurDouble, double aBorneMin, double aBorneMax)
	{
		try
		{
			Double valeurRetournée = new Double (aValeurDouble);
			if (valeurRetournée < aBorneMin || valeurRetournée > aBorneMax)
			{
				throw new NumberFormatException ("Valeur hors borne");
			}
			return valeurRetournée;
		}
		catch (NumberFormatException aNFE)
		{
			return null;
		}
	}

	public static String contrôleInsee (String aNuméroInsee)
	{
		if ( ! Tools.motifSS.matcher (aNuméroInsee).matches ())
			return null;

		return aNuméroInsee;
	}

	public static Integer contrôleInt (String aEntier, int aBorneMin, int aBorneMax)
	{
		try
		{
			Integer valeurRetournée = new Integer (aEntier);
			if (valeurRetournée < aBorneMin || valeurRetournée > aBorneMax)
			{
				throw new NumberFormatException ("Valeur hors bornes");
			}
			return valeurRetournée;
		}
		catch (NumberFormatException aNFE)
		{
			return null;
		}
	}

	public static boolean contrôleTexte (String aTexte, int aLongueurMin, int aLongueurMax)
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

	public static void définitFonte (JComponent aZone)
	{
		Tools.définitFonte (aZone, "Arial", 14);
	}

	public static void définitFonte (JComponent aZone, int aTaille)
	{
		Tools.définitFonte (aZone, "Arial", aTaille);
	}

	public static void définitFonte (JComponent aZone, String aPolice, int aTaille)
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