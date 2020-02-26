package filesystem.userinterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
	public static void main(String[] args) throws IOException, InterruptedException {

		loadProperties();
		Executor executor = Executors.newFixedThreadPool(10);
		for(int i = 0;i<2;i++) {
			executor.execute(new ProcessThread(properties));
		}

	}
	private static void loadProperties() throws FileNotFoundException, IOException {
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
					case 1: {
						CreateFileUtils createFileUtils = new CreateFileUtils(scanner, properties);
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
					case 3: {

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
					if (!continueTask.equalsIgnoreCase("y")) {
						break;
					}
				} while (true);
			}
		}
	}

}

