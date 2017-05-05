package com.bbva.caas;

import java.io.ByteArrayInputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;
import org.bouncycastle.util.encoders.Base64;
import org.junit.Test;

public class Configuration {

	private static final String SECRET_TYPE = "AES";
	private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

	static final Logger log = Logger.getLogger(Configuration.class);

	@Test	
	public void buildKeyCAAS()
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
				BadPaddingException, InvalidAlgorithmParameterException {
		Random rnd;
		String transKey, keyTo, password, resHex;
		byte [] transKeyByte, keyToByte, iv, transKeyToFile, encryptedData, resByte;
		IvParameterSpec ivParam;
		SecretKey key;
		Cipher cipherDES;

		transKey = "01010101010101010101010101010101";
		keyTo = "02020202020202020202020202020202";
		password = "hfy4688j23hg3889";

		rnd = new Random();
		iv = new byte[16];

		transKeyByte =  hexStringToByteArray(transKey);
		keyToByte = hexStringToByteArray(keyTo);
		rnd.nextBytes(iv);

		transKeyToFile = xorWithKey(transKey.getBytes(), password.getBytes());

		log.debug("Transport Key File : " + new String(encodeUrlSafe(transKeyToFile)));

		ivParam = new IvParameterSpec(iv);

		key = new SecretKeySpec(transKeyByte, SECRET_TYPE);
	    cipherDES = Cipher.getInstance(ALGORITHM);
	    cipherDES.init(Cipher.ENCRYPT_MODE, key, ivParam);
		encryptedData = cipherDES.doFinal(keyToByte);

	    resHex = this.toHexadecimal(iv) + this.toHexadecimal(encryptedData);
	    resByte = this.hexStringToByteArray(resHex);

		log.debug("Key ciphered to policy:" + new String(encodeUrlSafe(resByte)));
	}


	// **************************
	// *	PRIVATE METHODS		*
	// **************************


	/**
	 *
	 * @param a
	 * @param key
	 * @return
	 */
	private byte [] xorWithKey(byte [] a, byte [] key) {
		byte [] out;

		out = new byte[a.length];

		for (int i = 0; i < a.length; i++) {
			out[i] = (byte) (a[i] ^ key[i % key.length]);
		}

		return out;
	}


	/**
	 *
	 * @param data
	 * @return
	 */
	private byte [] encodeUrlSafe(byte [] data) {
	    byte [] encode;

		encode = Base64.encode(data);

	    for (int i = 0; i < encode.length; i++) {
	        if (encode[i] == '+')
	            encode[i] = '-';
	        else if (encode[i] == '/')
	            encode[i] = '_';
	    }

	    return encode;
	}

	/**
	 *
	 * @param data
	 * @return
	 */
	private byte [] decodeUrlSafe(byte[] data) {
	    byte[] encode;

		encode = Arrays.copyOf(data, data.length);

	    for (int i = 0; i < encode.length; i++) {
	        if (encode[i] == '-')
	            encode[i] = '+';
	        else if (encode[i] == '_')
	            encode[i] = '/';
	    }

	    return Base64.decode(encode);
	}

	/**
	 *
	 * @param s
	 * @return
	 */
	private byte [] hexStringToByteArray(String s) {
		int len;
		byte [] data;

		len = s.length();
		data = new byte[len / 2];

		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
					+ Character.digit(s.charAt(i+1), 16));
		}

		return data;
	}

	/**
	 *
	 * @param data
	 * @return
	 */
	private String toHexadecimal(byte [] data){
		String result, cadAux;
		ByteArrayInputStream input;
		int read;

		result = "";
		input = new ByteArrayInputStream(data);

		read = input.read();
		while (read != -1) {
			cadAux = Integer.toHexString(read);

			if(cadAux.length() < 2)
				result += "0";

			result += cadAux;
			read = input.read();

		}

		return result.toUpperCase();
	}





}
