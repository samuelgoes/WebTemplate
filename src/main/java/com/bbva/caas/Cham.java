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

import org.bouncycastle.util.encoders.Base64;
import org.junit.Test;



public class Cham {

	@Test	
	public void buildKeyCAAS()
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
				BadPaddingException, InvalidAlgorithmParameterException {
		Random rnd;
		String transKey, keyTo, password, resHex;
		byte[] transKeyByte, keyToByte, iv, transKeyToFile, datosCifrados, resByte;
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

	    System.out.println("Transport Key File :"+new String(encodeUrlSafe(transKeyToFile)));

		ivParam = new IvParameterSpec(iv);

		key = new SecretKeySpec(transKeyByte,"AES");
	    cipherDES = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    cipherDES.init(Cipher.ENCRYPT_MODE, key, ivParam);
	    datosCifrados = cipherDES.doFinal(keyToByte);

	    resHex = this.toHexadecimal(iv) + this.toHexadecimal(datosCifrados);
	    resByte = this.hexStringToByteArray(resHex);

		System.out.println("Key ciphered to policy:"+new String(encodeUrlSafe(resByte)));
	}

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
	 * @param datos
	 * @return
	 */
	private String toHexadecimal(byte [] datos){
		String result, cadAux;
		ByteArrayInputStream input;
		int read;

		result = "";
		input = new ByteArrayInputStream(datos);

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
