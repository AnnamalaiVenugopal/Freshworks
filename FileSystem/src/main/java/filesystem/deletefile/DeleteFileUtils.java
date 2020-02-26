package filesystem.deletefile;

import java.util.Scanner;

import filesystem.common.CommonFileUtils;

public class DeleteFileUtils {

	Scanner scanner = null;
	
	public DeleteFileUtils(Scanner scanner) {
		this.scanner = scanner;
	}

	public boolean deleteFile() {
		boolean isFound = true;
		try {
			System.out.println("Enter the key to be deleted");
			String key = scanner.next();
			isFound = CommonFileUtils.deleteIfFound(key);
		} catch (Exception e) {
			System.out.println("Exception occured while reading the file ");
			e.printStackTrace();
		}
		return isFound;

	}
}
