
package persitenceRec;

import java.time.LocalDateTime;
import java.util.Date;

public class Datarec
{
	private long			rowid;
	private Date			horodatage;
	private int				adresse;
	private float			valeur;
	private int				idslave;
	private String			tagname;
	private String			zone;
	private String			userfield1;
	private String			userfield2;

	public long getRowid ()
	{
		return rowid;
	}

	public void setRowid (long rowid)
	{
		this.rowid = rowid;
	}
	public Date getHorodatage ()
	{
		return horodatage;
	}

	public void setHorodatage (Date aHorodatage)
	{
		this.horodatage = aHorodatage;
	}

	public int getAdresse ()
	{
		return adresse;
	}

	public void setAdresse (int adresse)
	{
		this.adresse = adresse;
	}

	public float getValeur ()
	{
		return valeur;
	}

	public void setValeur (float valeur)
	{
		this.valeur = valeur;
	}

	public int getIdslave ()
	{
		return idslave;
	}

	public void setIdslave (int idslave)
	{
		this.idslave = idslave;
	}

	public String getTagname ()
	{
		return tagname;
	}

	public void setTagname (String tagname)
	{
		this.tagname = tagname;
	}

	public String getZone ()
	{
		return zone;
	}

	public void setZone (String zone)
	{
		this.zone = zone;
	}

	public String getUserfield1 ()
	{
		return userfield1;
	}

	public void setUserfield1 (String userfield1)
	{
		this.userfield1 = userfield1;
	}

	public String getUserfield2 ()
	{
		return userfield2;
	}

	public void setUserfield2 (String userfield2)
	{
		this.userfield2 = userfield2;
	}



}
