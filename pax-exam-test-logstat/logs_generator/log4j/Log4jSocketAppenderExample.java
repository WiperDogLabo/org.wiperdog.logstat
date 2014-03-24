package log4j;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Log4jSocketAppenderExample {
	static Logger logger = Logger.getLogger(Log4jSocketAppenderExample.class);

	public static void main(String[] args) {
		logsGenerator();
	}

	public static void logsGenerator() {		
		while (true) {
			try {
				// PropertiesConfigurator is used to configure logger from log4j.properties
				// properties file
				String wd = System.getProperty("user.dir");
				PropertyConfigurator.configure(wd + "/log4j/log4j.properties");

				// These logs will be sent to socket server & stdout as configured in log4j.properties
				logger.error("Log4j error message!!");
				//logger.debug("Log4j debug message!!");				
				Thread.sleep(3000);
			} catch (Exception ex) {
				System.out.println("Error !");
			}
		}
	}
}