package filesystem.CreateFile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import filesystem.common.CommonFileUtils;
import filesystem.common.SizeLimitExceededException;

public class CreateFileUtils {

	Scanner scanner = null;

	int ttl;

	public CreateFileUtils(Scanner scanner, int ttl) {
		this.scanner = scanner;
		this.ttl = ttl;
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

	@SuppressWarnings("unchecked")
	private void writeToFile(HashMap<String, JSONObject> dataMap) throws IOException, InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException {
		try {
			HashMap<String, JSONObject> resultMap = new HashMap<>();
			String filePath = "data".concat(File.separator);
			File file = new File(filePath);
			if (!file.exists()) {
				file.mkdirs();
			}
			String[] files = file.list();
			boolean isFileFound = false;
			if (files.length > 0) {
				for (String fileTemp : files) {
					file = new File(fileTemp);
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
			if (isFileFound) {
				cipher.init(Cipher.DECRYPT_MODE, k);
				try (CipherInputStream cipherInputStream = new CipherInputStream(
						new DataInputStream(new FileInputStream(file)), cipher);
						ObjectInputStream inputStream = new ObjectInputStream(cipherInputStream)) {
					SealedObject sob = (SealedObject) inputStream.readObject();
					resultMap = (HashMap<String, JSONObject>) sob.getObject(cipher);
				}
			}
			for(Map.Entry<String, JSONObject> entry: dataMap.entrySet()) {
				JSONObject jsonObject = new JSONObject();
				if(resultMap.containsKey(entry.getKey())) {
					jsonObject = resultMap.get(entry.getKey());
				}
				resultMap.put(entry.getKey(), getMergedJson(jsonObject, entry.getValue()));
			}
			cipher.init(Cipher.ENCRYPT_MODE, k);
			SealedObject sob = new SealedObject(resultMap, cipher);
			try (CipherOutputStream cipherOutputStream = new CipherOutputStream(
					new DataOutputStream(new FileOutputStream(file)), cipher);
					ObjectOutputStream outputStream = new ObjectOutputStream(cipherOutputStream)) {
				outputStream.writeObject(sob);
			}
		} catch (Exception e) {

		}
	}

	@SuppressWarnings("unchecked")
	private JSONObject getMergedJson(JSONObject jsonObject, JSONObject value) throws JSONException {
		Iterator<String> key = value.keys();
		while (key.hasNext()) {
			String jsonKey = key.next();
			jsonObject.put(jsonKey, value.get(jsonKey));
		}
		return jsonObject;
	}

}
