package filesystem.userinterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import filesystem.createfile.CreateFileUtils;
import filesystem.deletefile.DeleteFileUtils;
import filesystem.readfile.ReadFileUtils;

public class UserInterface {

	static Properties properties;

	public static void main(String[] args) throws IOException {

		loadProperties();
		Executor executor = Executors.newFixedThreadPool(10);
		Scanner sc = new Scanner(System.in);
		int noOfUsers = sc.nextInt();
		sc.close();
		for (int i = 0; i < noOfUsers; i++) {
			executor.execute(new ProcessThread(properties));
		}

	}

	private static void loadProperties() throws IOException {
		properties = new Properties();
		properties.load(new FileInputStream(new File("filesystem.properties")));
	}
}

class ProcessThread implements Runnable {

	Properties properties;

	public ProcessThread(Properties properties) {
		this.properties = properties;
	}

	@Override
	public void run() {
		int choice;
		String continueTask = null;
		try (Scanner scanner = new Scanner(System.in)) {
			synchronized (scanner) {

				do {
					System.out.println("What to do with the file");
					System.out.println("Enter 1 for creating it");
					System.out.println("Enter 2 for reading it");
					System.out.println("Enter 3 for deleting it");
					choice = scanner.nextInt();
					switch (choice) {
					case 1:
						createFile(scanner);
						break;

					case 2:
						readFile(scanner);
						break;
					case 3:
						deleteFile(scanner);
						break;
					}
					System.out.println("Do you want to continue?(y/n)");
					continueTask = scanner.next();
					if (!continueTask.equalsIgnoreCase("y")) {
						break;
					}
				} while (true);
			}
		}
	}

	private void deleteFile(Scanner scanner) {
		try {
			DeleteFileUtils deleteFileUtils = new DeleteFileUtils(scanner, properties);
			boolean isDeleted = deleteFileUtils.deleteFile();
			if (isDeleted) {
				System.out.println("File deleted successfully");
			} else {
				System.out.println("File not deleted successfully");
			}

		} catch (Exception e) {

		}

	}

	private void readFile(Scanner scanner) {
		try {
			ReadFileUtils readFileUtils = new ReadFileUtils(scanner, properties);
			String content = readFileUtils.readFile();
			if (content != null) {
				System.out.println(content);
			}
		} catch (Exception e) {

		}
	}

	public void createFile(Scanner scanner) {
		try {
			CreateFileUtils createFileUtils = new CreateFileUtils(scanner, properties);
			boolean isCreated = createFileUtils.createFile();
			if (isCreated) {
				System.out.println("File created successfully");
			} else {
				System.out.println("File not created successfully");
			}
		} catch (Exception e) {

		}
	}

}
