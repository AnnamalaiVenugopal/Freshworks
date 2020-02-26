package filesystem.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Key;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.codehaus.jettison.json.JSONObject;

public class CommonFileUtils {


	@SuppressWarnings("unchecked")
	public static boolean isKeyExist(String key) {
		boolean isFileFound = false;
		try {

			String filePath = "data".concat(File.separator);
			File file = new File(filePath);
			if (!file.exists()) {
				file.mkdirs();
			}
			String[] files = file.list();
			HashMap<String, JSONObject> dataMap = new HashMap<>();
			SecretKey k = new SecretKeySpec( new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07 }, "Blowfish" );
			Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(Cipher.DECRYPT_MODE, k);
			if (files.length > 0) {
				for (String fileTemp : files) {
					String path = file.getAbsolutePath().concat(File.separator).concat(fileTemp);
					file = new File(path);
					try (CipherInputStream cipherInputStream = new CipherInputStream(
							new DataInputStream(new FileInputStream(file)), cipher);
							ObjectInputStream inputStream = new ObjectInputStream(cipherInputStream)) {

						SealedObject sob = (SealedObject) inputStream.readObject();
						dataMap = (HashMap<String, JSONObject>) sob.getObject(cipher);

						if (dataMap.containsKey(key)) {
							isFileFound = true;
							break;
						}
					}

				}
			}
		}catch(Exception e) {
			
		}
		return isFileFound;

	}

	public static boolean isJSONLimitExceeded(JSONObject json) throws SizeLimitExceededException {
		String jsonString = json.toString();
		if (jsonString.length() < (16 * 1024)) {
			return true;
		}
		if (jsonString.length() > (16 * 1024)) {
			throw new SizeLimitExceededException("Json Object size limit has been exceeded");
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public static JSONObject isKeyFound(String key) {
		JSONObject resultJson = new JSONObject();
		try {
			String filePath = "data".concat(File.separator);
			File file = new File(filePath);
			if (!file.exists()) {
				file.mkdirs();
			}
			String[] files = file.list();
			boolean isFileFound = false;
			HashMap<String, JSONObject> dataMap = new HashMap<>();
			SecretKey k = new SecretKeySpec( new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07 }, "Blowfish" );
			Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(Cipher.DECRYPT_MODE, k);
			String path = null;
			if (files.length > 0) {
				for (String fileTemp : files) {
					path = file.getAbsolutePath().concat(File.separator).concat(fileTemp);
					try (CipherInputStream cipherInputStream = new CipherInputStream(
							new DataInputStream(new FileInputStream(new File(path))), cipher);
							ObjectInputStream inputStream = new ObjectInputStream(cipherInputStream)) {

						SealedObject sob = (SealedObject) inputStream.readObject();
						dataMap = (HashMap<String, JSONObject>) sob.getObject(cipher);

						if (dataMap.containsKey(key)) {
							isFileFound = true;
							break;
						}
					}

				}
			}
			if (isFileFound) {
				JSONObject json = dataMap.get(key);
				int ttl = json.getInt("timeToLive");
				long time = json.getLong("time");
				if (System.currentTimeMillis() > time + (ttl * 1000)) {
					dataMap.remove(key);
					cipher.init(Cipher.ENCRYPT_MODE, k);
					SealedObject sob = new SealedObject(dataMap, cipher);
					try (CipherOutputStream cipherOutputStream = new CipherOutputStream(
							new BufferedOutputStream(new FileOutputStream(new File(path))), cipher);
							ObjectOutputStream outputStream = new ObjectOutputStream(cipherOutputStream)) {
						outputStream.writeObject(sob);
					}

				} else {
					resultJson.put("value", json.toString());
					resultJson.put("isFound", true);
				}
			} else {
				resultJson.put("isFound", false);
			}
		} catch (Exception e) {
			System.out.println("Exception occured while checking the file existence");
			e.printStackTrace();
		}
		return resultJson;
	}

	@SuppressWarnings("unchecked")
	public static boolean deleteIfFound(String key) {
		boolean isDeleted = false;
		try {
			String filePath = "data".concat(File.separator);
			File file = new File(filePath);
			if (!file.exists()) {
				file.mkdirs();
			}
			String[] files = file.list();
			boolean isFileFound = false;
			HashMap<String, JSONObject> dataMap = new HashMap<>();
			SecretKey k = new SecretKeySpec( new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07 }, "Blowfish" );
			Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(Cipher.DECRYPT_MODE, k);
			String path = null;
			if (files.length > 0) {
				for (String fileTemp : files) {
					path = file.getAbsolutePath().concat(File.separator).concat(fileTemp);
					try (CipherInputStream cipherInputStream = new CipherInputStream(
							new BufferedInputStream(new FileInputStream(new File(path))), cipher);
							ObjectInputStream inputStream = new ObjectInputStream(cipherInputStream)) {
						SealedObject sob = (SealedObject) inputStream.readObject();
						dataMap = (HashMap<String, JSONObject>) sob.getObject(cipher);
						if (dataMap.containsKey(key)) {
							isFileFound = true;
							break;
						}
					}

				}
			}
			if (isFileFound) {
				JSONObject json = dataMap.get(key);
				int ttl = json.getInt("timeToLive");
				long time = json.getLong("time");
				if (System.currentTimeMillis() > time + (ttl * 1000)) {
					dataMap.remove(key);
					cipher.init(Cipher.ENCRYPT_MODE, k);
					SealedObject sob = new SealedObject(dataMap, cipher);
					try (CipherOutputStream cipherOutputStream = new CipherOutputStream(
							new BufferedOutputStream(new FileOutputStream(new File(path))), cipher);
							ObjectOutputStream outputStream = new ObjectOutputStream(cipherOutputStream)) {
						outputStream.writeObject(sob);
					}
					isDeleted = true;
				}
			}
		} catch (Exception e) {
			System.out.println("Exception occured while checking the file existence");
			e.printStackTrace();
		}
		return isDeleted;
	}
}
