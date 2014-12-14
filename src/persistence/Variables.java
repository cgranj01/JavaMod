package persistence;

public class Variables
{
	private Long idtag;
	private String format;
	private String type;
	private int adresse;
	private int numbit;
	private String periode;
	private int recdelay;
	private Esclaves slave;
	private String tagname;
	private String zone;
	private String userfield1;
	private String userfield2;
	private float coefficient;
	private String affichage;
	private String libelle0;
	private String libelle1;
	private String libelleEtendu;
	private int astreinte;
	private float valeur;
	private float saisie;
	
	public Variables()
	{
		
	}
	
	public Variables (String tagname, String format, String type, Integer adresse, Integer recdelay,
			String userfield1, String userfield2, String zone, Float coefficient, String affichage, Float valeur)
	{
		tagname = this.tagname;
		format = this.format;
		type = this.type;
		adresse = this.adresse;
		recdelay = this.recdelay;
		userfield1 = this.userfield1;
		userfield2 = this.userfield2;
		zone = this.zone;
		coefficient = this.coefficient;
		affichage = this.affichage;
		valeur = this.valeur;
	}
	
	public Long getIdtag ()
	{
		return idtag;
	}
	public void setIdtag (Long idtag)
	{
		this.idtag = idtag;
	}
	public String getType ()
	{
		return type;
	}
	public void setType (String type)
	{
		this.type = type;
	}
	public int getAdresse ()
	{
		return adresse;
	}
	public void setAdresse (int adresse)
	{
		this.adresse = adresse;
	}
	public int getRecdelay ()
	{
		return recdelay;
	}
	public void setRecdelay (int recdelay)
	{
		this.recdelay = recdelay;
	}
	public Esclaves getSlave ()
	{
		return slave;
	}
	public void setSlave (Esclaves idslave)
	{
		this.slave = idslave;
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
	public Float getCoefficient ()
	{
		return coefficient;
	}
	public void setCoefficient (Float coefficient)
	{
		this.coefficient = coefficient;
	}
	public String getAffichage ()
	{
		return affichage;
	}
	public void setAffichage (String affichage)
	{
		this.affichage = affichage;
	}
	public String getLibelle0 ()
	{
		return libelle0;
	}
	public void setLibelle0 (String libelle0)
	{
		this.libelle0 = libelle0;
	}
	public String getLibelle1 ()
	{
		return libelle1;
	}
	public void setLibelle1 (String libelle1)
	{
		this.libelle1 = libelle1;
	}
	public String getLibelleEtendu ()
	{
		return libelleEtendu;
	}
	public void setLibelleEtendu (String libelleEtendu)
	{
		this.libelleEtendu = libelleEtendu;
	}
	public String getFormat ()
	{
		return format;
	}
	public void setFormat (String format)
	{
		this.format = format;
	}
	public int getAstreinte ()
	{
		return astreinte;
	}
	public void setAstreinte (int astreinte)
	{
		this.astreinte = astreinte;
	}
	public String getPeriode ()
	{
		return periode;
	}
	public void setPeriode (String periode)
	{
		this.periode = periode;
	}
	public float getValeur ()
	{
		return valeur;
	}
	public void setValeur (float valeur)
	{
		this.valeur = valeur;
	}

	public float getSaisie ()
	{
		return saisie;
	}

	public void setSaisie (float saisie)
	{
		this.saisie = saisie;
	}

	public int getNumbit ()
	{
		return numbit;
	}

	public void setNumbit (int numbit)
	{
		this.numbit = numbit;
	}
	
	
}
