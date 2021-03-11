package com.example.faab.config.cipher;


import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class CryptUtils {
	public static SecretKeySpec FinalKey;
	private IvParameterSpec iv;
	private Cipher cipher;
	private String CryptFunction;

	private void DES(String Key) throws Exception {

		KeyGenerator KeyGen = KeyGenerator.getInstance("DES");
		KeyGen.init(56, new SecureRandom(Key.getBytes()));
		SecretKey secretKey = KeyGen.generateKey();

		byte[] enCodeFormat = secretKey.getEncoded();
		FinalKey = new SecretKeySpec(enCodeFormat, "DES");
		cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
	}

	private void DESede(String Key) throws Exception {
		KeyGenerator KeyGen = KeyGenerator.getInstance("DESede");
		KeyGen.init(168, new SecureRandom(Key.getBytes()));
		SecretKey secretKey = KeyGen.generateKey();
		byte[] enCodeFormat = secretKey.getEncoded();
		FinalKey = new SecretKeySpec(enCodeFormat, "DESede");

		byte[] keyiv = { 1, 2, 3, 4, 5, 6, 7, 8 };
		iv = new IvParameterSpec(keyiv);
		cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
	}

	private void AES(String Key) throws Exception {

		KeyGenerator KeyGen = KeyGenerator.getInstance("AES");
		SecretKey secretKey = KeyGen.generateKey();
		byte[] enCodeFormat = secretKey.getEncoded();

		FinalKey = new SecretKeySpec(enCodeFormat, "AES");
		cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
	}

	public  void InitCryptUtils(String encryptfunc, String encryptkey) throws Exception {
		CryptFunction = encryptfunc;
		switch (encryptfunc) {
		case "DES":
			DES(encryptkey);
			break;
		case "DESede":
			DESede(encryptkey);
			break;
		case "AES":
			AES(encryptkey);
			break;
		default:
			break;
		}
	}

	public  byte[] Encrypt(byte[] data) throws Exception {
		cipher.init(Cipher.ENCRYPT_MODE, FinalKey, new SecureRandom());
		return Base64.encodeBase64(cipher.doFinal(data));
	}

	public byte[] Decrypt(byte[] data) throws Exception {
		cipher.init(Cipher.DECRYPT_MODE, FinalKey, new SecureRandom());
		return cipher.doFinal(Base64.decodeBase64(data));
	}

	public byte[] DESedeEncrypt(byte[] data) throws Exception {
		cipher.init(Cipher.ENCRYPT_MODE, FinalKey, iv);
		return Base64.encodeBase64(cipher.doFinal(data));
	}

	public byte[] DESedeDecrypt(byte[] data) throws Exception {
		cipher.init(Cipher.DECRYPT_MODE, FinalKey, iv);
		return cipher.doFinal(Base64.decodeBase64(data));
	}

}
