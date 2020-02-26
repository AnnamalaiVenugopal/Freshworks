package filesystem.createfile;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.codehaus.jettison.json.JSONObject;

import filesystem.common.CommonFileUtils;
import filesystem.common.SizeLimitExceededException;

public class CreateFileUtils {

	Scanner scanner = null;

	Properties properties;
	int ttl;
	String fileLocation;

	public CreateFileUtils(Scanner scanner, Properties properties) {
		this.scanner = scanner;
		this.properties = properties;
		this.ttl = Integer.valueOf(properties.getProperty("ttl"));
		this.fileLocation = properties.getProperty("fileLocation");
	}

	public boolean createFile() {
		try {
			HashMap<String, JSONObject> dataMap = new HashMap<>();
			System.out.println("Enter the key to be created");
			String key = null;
			while (true) {
				key = scanner.next();
				if (key.length() > 32) {
					System.out.println("Key has more than 32 characters.Please enter within 32 characters");
				} else {
					break;
				}
			}
			while (CommonFileUtils.isKeyExist(key)) {
				System.out.println("The Key " + key + " already exists. Enter a new key");
				key = scanner.next();

			}
			JSONObject jsonObject = new JSONObject();
			do {

				System.out.println("Enter the Json Key");
				String jsonKey = scanner.next();

				System.out.println("Enter the Json Value");
				String jsonValue = scanner.next();
				jsonObject.put(jsonKey, jsonValue);
				boolean jsonLimit = false;
				try {
					jsonLimit = CommonFileUtils.isJSONLimitExceeded(jsonObject);
				} catch (SizeLimitExceededException e) {
					System.out.println(e.getMessage());
				}
				if (jsonLimit) {
					System.out.println("Enter y to add another value to the json or press any key");
					String choice = scanner.next();
					{
						if (!choice.equalsIgnoreCase("y")) {
							break;
						}
					}
				}
			} while (true);
			jsonObject.put("timeToLive", ttl);
			jsonObject.put("time", System.currentTimeMillis());
			dataMap.put(key, jsonObject);
			writeToFile(dataMap);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("File writing failed");
			return false;
		}
		return true;
	}

	private void writeToFile(HashMap<String, JSONObject> dataMap) throws IOException, InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException {
		try {
			String filePath = fileLocation.concat(File.separator).concat("data").concat(File.separator);
			File file = new File(filePath);
			if (!file.exists()) {
				file.mkdirs();
			}
			String[] files = file.list();
			boolean isFileFound = false;
			if (files.length > 0) {
				for (String fileTemp : files) {
					String filepath = filePath.concat(fileTemp);

					file = new File(filepath);
					double fileSize = (double) file.length() + dataMap.toString().getBytes().length;
					if (fileSize / (1024 * 1024) < 1) {
						break;
					}
					isFileFound = true;

				}
			}
			if (!isFileFound) {
				String timeStamp = System.currentTimeMillis() + "";
				filePath = filePath.concat(timeStamp).concat(".ser");
				file = new File(filePath);
				file.createNewFile();
			}
			SecretKey k = new SecretKeySpec( new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07 }, "Blowfish" );
			Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(Cipher.ENCRYPT_MODE, k);
			SealedObject sob = new SealedObject(dataMap, cipher);
			try (CipherOutputStream cipherOutputStream = new CipherOutputStream(
					new DataOutputStream(new FileOutputStream(file)), cipher);
					ObjectOutputStream outputStream = new ObjectOutputStream(cipherOutputStream)) {
				outputStream.writeObject(sob);
			}
		} catch (Exception e) {

		}
	}
}
