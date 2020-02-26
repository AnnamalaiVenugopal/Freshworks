package filesystem.common;

import javax.crypto.spec.SecretKeySpec;

public class FileSecretKey extends SecretKeySpec {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FileSecretKey(byte[] key, int offset, int len, String algorithm) {
		super(key, offset, len, algorithm);
	}

	public FileSecretKey(byte[] key, String algorithm) {
		super(key, algorithm);
	}

}
