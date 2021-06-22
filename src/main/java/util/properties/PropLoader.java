package util.properties;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class PropLoader {

	Properties properties;
	
	public PropLoader() {
		properties = new Properties();
		try {
			properties.load(new FileInputStream("simulator.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public boolean getSound() {
		if(properties.getProperty("sound").equals(new String("true")))
			return true;
		return false;
	}
	
	public void setSound(String e) {
		this.properties.setProperty("sound", e);
		try {
			properties.store(new FileOutputStream("simulator.properties"), "Autosave");
		} catch (IOException ex) {
		}
	}
	
	
	public String getPort() {
		return properties.getProperty("tutorport");
	}
	
	public String getHost() {
		return properties.getProperty("tutorhost");
	}
	
	public String getRole() {
		return properties.getProperty("role");
	}
	
	public String getLanguage() {
		return properties.getProperty("language");
	}
	
	public void setLanguage(String value) {
		this.properties.setProperty("language", value);
		try {
			properties.store(new FileOutputStream("simulator.properties"), "Autosave");
		} catch (IOException e) {
		}
	}
	
}
