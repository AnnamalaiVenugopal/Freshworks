package filesystem.deletefile;

import java.util.Properties;
import java.util.Scanner;

import filesystem.common.CommonFileUtils;

public class DeleteFileUtils {

	Scanner scanner;
	
	Properties properties;
	
	public DeleteFileUtils(Scanner scanner, Properties properties) {
		this.scanner = scanner;
		this.properties = properties;
	}

	public boolean deleteFile() {
		boolean isFound = true;
		try {
			System.out.println("Enter the key to be deleted");
			String key = scanner.next();
			isFound = CommonFileUtils.deleteIfFound(key,properties);
		} catch (Exception e) {
			System.out.println("Exception occured while reading the file ");
			e.printStackTrace();
		}
		return isFound;

	}
}
