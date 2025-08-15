package be.tav.connection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import javax.swing.JOptionPane;

public class SkiConnection {
	private static Connection snglConnection = null;
	
	private SkiConnection() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			String fileConfig = "config.properties";
			Properties props = ConfigLoader.loadProperties(fileConfig);
			String ip = props.getProperty("db.ip");
			String port = props.getProperty("db.port");
			String service_name = props.getProperty("db.service_name");
			String connectionString = "jdbc:oracle:thin:@//" + ip + ":" + port + "/" + service_name;  
			String username = props.getProperty("db.username");
			String password = props.getProperty("db.password");
			snglConnection = DriverManager.getConnection(connectionString, username, password);
		}
		catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Impossible de trouver le driver pour la base de donnée!");
			System.out.println("[ERROR]: " + e.getMessage());
			e.printStackTrace();
		} 
		catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Impossible de se connecter à la base de donnée.");
			System.out.println("[ERROR]: " + e.getMessage());
			e.printStackTrace();
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Erreur lors du chargement du fichier de configuration.");
			System.out.println("[ERROR]: " + e.getMessage());
			e.printStackTrace();
		}
		
		if (snglConnection == null) {
			JOptionPane.showMessageDialog(null, "La base de donnée est innaccessible, fermeture du programme.");
			System.out.println("[ERROR]: La base de donnée est innaccessible, fermeture du programme.");
			System.exit(0);
		}
	}
	
	public static Connection getInstance() {
		if (snglConnection == null) {
			new SkiConnection();
		}
		
		return snglConnection;
	}
}
