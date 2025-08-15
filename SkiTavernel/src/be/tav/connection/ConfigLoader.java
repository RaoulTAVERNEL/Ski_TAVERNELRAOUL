package be.tav.connection;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
	public static Properties loadProperties(String fileName) throws IOException {
		Properties props = new Properties();
		
		try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream(fileName)) {
			if (input == null) {
				throw new IOException("Erreur lors du chargement du fichier de configuration");
			}
			
			props.load(input);
		}
		
		return props;
	}
}
