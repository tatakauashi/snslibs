package net.meiteampower.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.util.ByteArrayBuffer;

/**
 * 暗号化・復号を行う。
 *
 * @author kie
 */
public class Crypto {

	private static final byte[] ivs;
	static {

		byte[] tmp = new byte[] {-10, 2, 5, 99, 102, 3, -3, 30, 21, -9, -84, 88, 0, 16, 127, -128};
//		try {
//			tmp = "5621611953562161".getBytes("UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
		ivs = tmp;
	}

	/**
	 * 外部から指定したパスワードをシャッフルして実際に使用するパスワードに変換する。
	 * @param p 外部から指定したパスワード
	 * @return システムで使用するパスワード
	 */
	public static String getPassword(final String p) {

		final String mySeed = "bReKp1nK";
	    MessageDigest md = null;
	    StringBuilder sb = null;
	    try {
	        md = MessageDigest.getInstance("SHA-512");
	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    }
	    md.update((p + mySeed).getBytes());
	    sb = new StringBuilder();
	    for (byte b : md.digest()) {
	        String hex = String.format("%02x", b);
	        sb.append(hex);
	    }
	    return sb.toString();
	}

	private static Cipher getCipher(final String p, final boolean encrypt) throws Exception {
		ByteArrayBuffer bab = new ByteArrayBuffer(16);
		int size = 0;
		byte[] b = p.getBytes("UTF-8");
		do {
			bab.append(b, 0, b.length);
			size += b.length;
		} while (size < 16);
		bab.setLength(16);

		IvParameterSpec iv = new IvParameterSpec(ivs);
		SecretKeySpec key = new SecretKeySpec(bab.toByteArray(), "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		if (encrypt) {
			cipher.init(Cipher.ENCRYPT_MODE, key, iv);
		} else {
			cipher.init(Cipher.DECRYPT_MODE, key, iv);
		}
		return cipher;
	}

	/**
	 * 暗号化する。
	 * @param plain 暗号化する文字列
	 * @param p パスワード
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(final String plain, final String p) throws Exception {

		Cipher cipher = getCipher(getPassword(p), true);
		byte[] crypto = cipher.doFinal(plain.getBytes());
		String encrypted = Base64.getEncoder().encodeToString(crypto);
		return encrypted;
	}

	/**
	 * 復号を行う。
	 * @param encrypted 暗号化された文字列
	 * @param p パスワード
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(final String encrypted, final String p) throws Exception {

		byte[] crypto = Base64.getDecoder().decode(encrypted);
		Cipher cipher = getCipher(getPassword(p), false);
		byte[] plain = cipher.doFinal(crypto);
		return new String(plain);
	}
}
