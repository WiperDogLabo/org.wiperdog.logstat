package org.wiperdog.logstat.libs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

public class Common {
	String configFile;
	private Properties prop = null;
	
	/**
	 * Common: process ConfigInput.properties file
	 */
	public Common(String configFile) {
		this.configFile = configFile;
		InputStream is = null;
		try {
			this.prop = new Properties();
			//is = getClass().getClassLoader().getResourceAsStream("conf/defaultInput.properties");
			Reader reader = new FileReader(new File(configFile));
			prop.load(reader);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block			
			e.printStackTrace();
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} 
	}
	
	/**
	 * getInputConfig: get all configuration informations
	 * @return mapData: configuration informations
	 */
	public HashMap<String, Object> getInputConfig() {
		HashMap<String, Object> mapData = new HashMap<String, Object>();
		HashMap<String, Object> mapInput = new HashMap<String, Object>();
		HashMap<String, Object> mapOutput = new HashMap<String, Object>();
		Common cm = new Common(configFile);
		Set<Object> keys = cm.getAllKeys();
		for(Object k:keys) {
			String key = (String)k;
			String value = "";
			if (key.contains("input.")) {
				value = cm.getPropertyValue(key).toString();
				key = key.replaceFirst("input.", "");
				mapInput.put(key, value);
			} else if (key.contains("output.")) {
				value = cm.getPropertyValue(key).toString();
				key = key.replaceFirst("output.", "");
				mapOutput.put(key, value);
			}
		}
		mapData.put("input", mapInput);
		mapData.put("output", mapOutput);
		return mapData;
	}
	
	/**
	 * getAllKeys: get all keys from properties file
	 * @return keys
	 */
	public Set<Object> getAllKeys(){
        Set<Object> keys = prop.keySet();
        return keys;
    }
	
	/**
	 * getPropertyValue: get value of corresponding key
	 * @param key
	 * @return value of key
	 */
	public String getPropertyValue(String key){
        return this.prop.getProperty(key);
    }
	public File createTmpFile(String fileName,String extension,String content) throws IOException{
		File tmpFile = File.createTempFile(fileName, extension);
		BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFile));
		bw.write(content);
		bw.close();
		return tmpFile;
	}
}