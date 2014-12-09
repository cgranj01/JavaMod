
package gui;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.hibernate.Session;

import utilitaires.Tools;
import connexionBase.SingletonSessionHSQL;

@SuppressWarnings("serial")
public class DialogParamComModbus extends JDialog implements ActionListener
{
	private Session				sessionsqlite		= SingletonSessionHSQL.getInstance ().getSession ();
	private JComboBox<String>	comboIdSlave		= new JComboBox<String> ();
	private JLabel				lblInfoValidation	= new JLabel ("Saisir un esclave");
	private JTextField			textFieldAdresseIP;
	private JTextField			textFieldPort;
	private JTextField			textFieldTimeout;
	PreparedStatement			preparedStatement;

	public DialogParamComModbus ()
	{
		setModal (true);
		setResizable (false);
		setDefaultCloseOperation (JDialog.DISPOSE_ON_CLOSE);
		setAlwaysOnTop (true);
		JPanel panel = new JPanel ();
		GridBagLayout gbl_panel = new GridBagLayout ();
		gbl_panel.columnWidths = new int [] {88, 27, 70, 85, 0};
		gbl_panel.rowHeights = new int [] {30, 20, 20, 20, 20, 31, 14, 0};
		gbl_panel.columnWeights = new double [] {0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double [] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout (gbl_panel);

		JLabel lblId = new JLabel ("ID");
		GridBagConstraints gbc_lblId = new GridBagConstraints ();
		gbc_lblId.anchor = GridBagConstraints.EAST;
		gbc_lblId.insets = new Insets (0, 0, 5, 5);
		gbc_lblId.gridx = 0;
		gbc_lblId.gridy = 1;
		panel.add (lblId, gbc_lblId);
		this.comboIdSlave.setEditable (true);
		GridBagConstraints gbc_comboIdSlave = new GridBagConstraints ();
		gbc_comboIdSlave.anchor = GridBagConstraints.WEST;
		gbc_comboIdSlave.gridwidth = 2;
		gbc_comboIdSlave.fill = GridBagConstraints.VERTICAL;
		gbc_comboIdSlave.insets = new Insets (0, 0, 5, 5);
		gbc_comboIdSlave.gridx = 1;
		gbc_comboIdSlave.gridy = 1;
		panel.add (this.comboIdSlave, gbc_comboIdSlave);

		this.comboIdSlave.addActionListener (this);

		JLabel lblAdresseIp = new JLabel ("Adresse IP");
		GridBagConstraints gbc_lblAdresseIp = new GridBagConstraints ();
		gbc_lblAdresseIp.anchor = GridBagConstraints.EAST;
		gbc_lblAdresseIp.insets = new Insets (0, 0, 5, 5);
		gbc_lblAdresseIp.gridx = 0;
		gbc_lblAdresseIp.gridy = 2;
		panel.add (lblAdresseIp, gbc_lblAdresseIp);

		this.textFieldAdresseIP = new JTextField ();
		GridBagConstraints gbc_textFieldAdresseIP = new GridBagConstraints ();
		gbc_textFieldAdresseIP.fill = GridBagConstraints.BOTH;
		gbc_textFieldAdresseIP.insets = new Insets (0, 0, 5, 5);
		gbc_textFieldAdresseIP.gridwidth = 2;
		gbc_textFieldAdresseIP.gridx = 1;
		gbc_textFieldAdresseIP.gridy = 2;
		panel.add (this.textFieldAdresseIP, gbc_textFieldAdresseIP);
		this.textFieldAdresseIP.setColumns (10);

		JLabel lblPort = new JLabel ("Port TCP");
		GridBagConstraints gbc_lblPort = new GridBagConstraints ();
		gbc_lblPort.anchor = GridBagConstraints.EAST;
		gbc_lblPort.insets = new Insets (0, 0, 5, 5);
		gbc_lblPort.gridx = 0;
		gbc_lblPort.gridy = 3;
		panel.add (lblPort, gbc_lblPort);

		textFieldPort = new JTextField ();
		GridBagConstraints gbc_textFieldPort = new GridBagConstraints ();
		gbc_textFieldPort.fill = GridBagConstraints.VERTICAL;
		gbc_textFieldPort.anchor = GridBagConstraints.WEST;
		gbc_textFieldPort.insets = new Insets (0, 0, 5, 5);
		gbc_textFieldPort.gridwidth = 2;
		gbc_textFieldPort.gridx = 1;
		gbc_textFieldPort.gridy = 3;
		panel.add (textFieldPort, gbc_textFieldPort);
		textFieldPort.setColumns (5);

		JLabel lblTimeout = new JLabel ("Timeout (ms)");
		lblTimeout.setHorizontalAlignment (SwingConstants.RIGHT);
		GridBagConstraints gbc_lblTimeout = new GridBagConstraints ();
		gbc_lblTimeout.anchor = GridBagConstraints.EAST;
		gbc_lblTimeout.insets = new Insets (0, 0, 5, 5);
		gbc_lblTimeout.gridx = 0;
		gbc_lblTimeout.gridy = 4;
		panel.add (lblTimeout, gbc_lblTimeout);

		this.textFieldTimeout = new JTextField ();
		GridBagConstraints gbc_textFieldTimeout = new GridBagConstraints ();
		gbc_textFieldTimeout.fill = GridBagConstraints.VERTICAL;
		gbc_textFieldTimeout.anchor = GridBagConstraints.WEST;
		gbc_textFieldTimeout.insets = new Insets (0, 0, 5, 5);
		gbc_textFieldTimeout.gridwidth = 2;
		gbc_textFieldTimeout.gridx = 1;
		gbc_textFieldTimeout.gridy = 4;
		panel.add (this.textFieldTimeout, gbc_textFieldTimeout);
		this.textFieldTimeout.setColumns (10);
		getContentPane ().setLayout (new FlowLayout (FlowLayout.CENTER, 5, 5));

		JButton btnValider = new JButton ("Valider");
		btnValider.addActionListener (this);

		JButton btnAnnuler = new JButton ("  Annuler  ");
		btnAnnuler.addActionListener (this);
		btnAnnuler.setActionCommand ("Annuler");
		GridBagConstraints gbc_btnAnnuler = new GridBagConstraints ();
		gbc_btnAnnuler.gridwidth = 2;
		gbc_btnAnnuler.fill = GridBagConstraints.VERTICAL;
		gbc_btnAnnuler.insets = new Insets (0, 0, 5, 5);
		gbc_btnAnnuler.gridx = 0;
		gbc_btnAnnuler.gridy = 5;
		panel.add (btnAnnuler, gbc_btnAnnuler);

		JButton btnSupprimer = new JButton ("Supprimer");
		btnSupprimer.addActionListener (this);
		btnSupprimer.setActionCommand ("Supprimer");
		GridBagConstraints gbc_btnSupprimer = new GridBagConstraints ();
		gbc_btnSupprimer.anchor = GridBagConstraints.WEST;
		gbc_btnSupprimer.fill = GridBagConstraints.VERTICAL;
		gbc_btnSupprimer.insets = new Insets (0, 0, 5, 5);
		gbc_btnSupprimer.gridx = 2;
		gbc_btnSupprimer.gridy = 5;
		panel.add (btnSupprimer, gbc_btnSupprimer);
		btnValider.setActionCommand ("Valider");
		GridBagConstraints gbc_btnValider = new GridBagConstraints ();
		gbc_btnValider.fill = GridBagConstraints.BOTH;
		gbc_btnValider.insets = new Insets (0, 0, 5, 0);
		gbc_btnValider.gridx = 3;
		gbc_btnValider.gridy = 5;
		panel.add (btnValider, gbc_btnValider);
		GridBagConstraints gbc_lblInfoValidation = new GridBagConstraints ();
		gbc_lblInfoValidation.anchor = GridBagConstraints.NORTH;
		gbc_lblInfoValidation.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblInfoValidation.gridwidth = 4;
		gbc_lblInfoValidation.gridx = 0;
		gbc_lblInfoValidation.gridy = 6;
		panel.add (lblInfoValidation, gbc_lblInfoValidation);
		lblInfoValidation.setHorizontalAlignment (SwingConstants.RIGHT);

		Tools.faitBordureEtTexte (panel, "Parametrages esclaves Modbus");
		getContentPane ().add (panel);

		this.initialize ();
		this.pack ();
		this.setLocationRelativeTo (null);
		// this.setSize (507, 243);
		this.setVisible (true);
	}

	public void initialize ()
	{
		String reqSQL = "SELECT id_slave FROM esclaves order by 1";
		List<String> list = this.sessionsqlite.createQuery (reqSQL).list ();
		this.comboIdSlave.removeAllItems ();
		for (String item : list)
		{	
				this.comboIdSlave.addItem (item);
		}

	}

	public int testNombreEsclave (String aItem) throws SQLException
	{
		String reqSQL = "SELECT count(*) FROM esclaves WHERE id_slave = '" + aItem + "'";
		int nb = ((Long)this.sessionsqlite.createQuery(reqSQL).uniqueResult()).intValue();
		return nb;
	}

	public int testNombreEsclave () throws SQLException
	{

		String reqSQL = "SELECT count(*) FROM esclaves";
		int nb = ((Long)this.sessionsqlite.createQuery(reqSQL).uniqueResult()).intValue();
		return nb;
	}

	public void valide ()
	{
		String item = (String) this.comboIdSlave.getSelectedItem ();
		String adresse = this.textFieldAdresseIP.getText ();
		String port = this.textFieldPort.getText ();
		String timeout = this.textFieldTimeout.getText ();

		try
		{

			if (this.testNombreEsclave (item) == 1)
			{

				String uSQL = "UPDATE esclaves SET adresseip = '" + adresse + "', port = '" + port + "', timeout= '"
						+ timeout + "'" + " WHERE id_slave = '" + item + "'";

				this.co.getOrdreSql ().executeUpdate (uSQL);
				this.co.getConnexionSqlite ().commit ();
				this.lblInfoValidation.setText ("Esclave " + item + " mis à jour");
			}
			else
			{
				String iSQL = "INSERT INTO esclaves (id_slave, adresseip, port, timeout) values ('" + item + "'" + ",'"
						+ adresse + "','" + port + "','" + timeout + "')";
				if (item == null)
				{
					this.lblInfoValidation.setText ("Saisir un numéro d'eclave !");
				}
				else
				{
					this.co.getOrdreSql ().executeUpdate (iSQL);
					this.co.getConnexionSqlite ().commit ();
					this.lblInfoValidation.setText ("Esclave " + item + " ajouté !");
					this.initialize ();
				}
			}

		}
		catch (SQLException aSQLE)
		{
			System.out.println ("catch " + aSQLE.getLocalizedMessage ());
		}
	}

	public void afficheEsclave ()
	{
		String item = (String) this.comboIdSlave.getSelectedItem ();
		String reqSQL = "SELECT id_slave, adresseip, port, timeout FROM esclaves WHERE id_slave = '" + item + "'";
		try
		{
			ResultSet res = this.co.getOrdreSql ().executeQuery (reqSQL);
			while (res.next ())
			{
				// this.saisieBadge.setText (res.getString (1));
				this.textFieldAdresseIP.setText (res.getString ("adresseip"));
				this.textFieldPort.setText (res.getString ("port"));
				this.textFieldTimeout.setText (res.getString ("timeout"));
			}
			res.close ();
		}

		catch (SQLException aSQLE)
		{
			System.out.println (aSQLE.getLocalizedMessage ());
		}
	}

	@Override
	public void actionPerformed (ActionEvent aAe)
	{
		String cause = aAe.getActionCommand ();
		if (cause.equals ("Valider"))
		{
			this.valide ();
			return;
		}

		if (cause.equals ("Annuler"))
		{
			this.dispose ();
		}

		if (cause.equals ("Supprimer"))
		{
			String item = (String) this.comboIdSlave.getSelectedItem ();
			System.out.println (item);
			String dSQL = "DELETE FROM esclaves WHERE id_slave = '" + item + "'";
			if (item == null)
			{
				this.lblInfoValidation.setText (" Pas d'Esclave à supprimer !");
				return;
			}

			try
			{
				if (this.testNombreEsclave () == 1)
				{
					this.lblInfoValidation.setText (" Au moins un esclave doit etre paramétré !");
					return;
				}
				else
				{
					this.co.getOrdreSql ().executeUpdate (dSQL);
					this.co.getConnexionSqlite ().commit ();
					this.lblInfoValidation.setText (" Esclave " + item + " supprimé");

					this.initialize ();
					return;
				}
			}
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace ();
			}
		}

		this.afficheEsclave ();
	}

}
