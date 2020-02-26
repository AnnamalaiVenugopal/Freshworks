package filesystem.UserInterface;

import java.util.Scanner;

import filesystem.CreateFile.CreateFileUtils;
import filesystem.ReadFile.ReadFileUtils;
import filesystem.deletefile.DeleteFileUtils;

public class UserInterface {

	public static void main(String[] args) {

		String continueTask = null;
		int choice;
		int ttl = 10000;
		try (Scanner scanner = new Scanner(System.in)) {
			do {
				System.out.println("What to do with the file");
				System.out.println("Enter 1 for creating it");
				System.out.println("Enter 2 for reading it");
				System.out.println("Enter 3 for deleting it");
				choice = scanner.nextInt();
				switch (choice) {
				case 1: {
					CreateFileUtils createFileUtils = new CreateFileUtils(scanner,ttl);
					boolean isCreated = createFileUtils.createFile();
					if (isCreated) {
						System.out.println("File created successfully");
					} else {
						System.out.println("File not created successfully");
					}
					break;
				}

				case 2: {
					ReadFileUtils readFileUtils = new ReadFileUtils(scanner);
					String content = readFileUtils.readFile();
					if (content != null) {
						System.out.println(content);
					}
					break;
				}
				case 3:{

					DeleteFileUtils deleteFileUtils = new DeleteFileUtils(scanner);
					boolean isDeleted = deleteFileUtils.deleteFile();
					if (isDeleted) {
						System.out.println("File deleted successfully");
					} else {
						System.out.println("File not deleted successfully");
					}
					break;
				
				}
				}
				System.out.println("Do you want to continue?(y/n)");
				continueTask = scanner.next();
				if(!continueTask.equalsIgnoreCase("y")) {
					break;
				}
			} while (true);
		}
	}

}
