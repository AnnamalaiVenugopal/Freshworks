package filesystem.readfile;

import java.util.Properties;
import java.util.Scanner;

import org.codehaus.jettison.json.JSONObject;

import filesystem.common.CommonFileUtils;

public class ReadFileUtils {

	Scanner scanner;
	
	Properties properties;

	public ReadFileUtils(Scanner scanner,Properties properties) {
		this.scanner = scanner;
		this.properties = properties;
	}

	public String readFile() {
		try {
			System.out.println("Enter the key to be read");
			String key = scanner.next();
			JSONObject resultJson = CommonFileUtils.isKeyFound(key,properties);
			boolean isFound = resultJson.optBoolean("isFound");
			if (!isFound) {
				System.out.println("The Key " + key + " does not exists");
				return null;
			}
			return resultJson.getString("value");
		} catch (Exception e) {
			System.out.println("Exception occured while reading the file ");
			e.printStackTrace();
		}
		return null;

	}

}
