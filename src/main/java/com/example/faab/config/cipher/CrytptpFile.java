package com.example.faab.config.cipher;


import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.SecureRandom;

public class CrytptpFile {

    public CrytptpFile() {
    }

    public static final String postfix = ".crypt";



        public static byte[] encrypt(File file,  byte[] key) throws Exception {
            try {
                //ZipUtils.compress();
                SecureRandom random = new SecureRandom();
                DESKeySpec desKey = new DESKeySpec(key);
                //创建一个密匙工厂，然后用它把DESKeySpec转换成
                SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
                SecretKey securekey = keyFactory.generateSecret(desKey);
                Cipher cipher = Cipher.getInstance("DES");
                cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
                InputStream is = new FileInputStream(file);
                CipherInputStream cis = new CipherInputStream(is, cipher);
                ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
//                OutputStream out = new FileOutputStream("C:\\Users\\shopping\\Documents\\test\\2.txt");
                byte[] buffer = new byte[1024];
                int r;
                while ((r = cis.read(buffer)) > 0) {
                    bos.write(buffer, 0, r);
//                    out.write(buffer, 0, r);
                }
                cis.close();
                is.close();
                bos.close();
                byte[] data = bos.toByteArray();
                return data;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    public static byte[] SDec(byte[] data, byte[] key) {
        try {
            // DES算法要求有一个可信任的随机数源
            SecureRandom random = new SecureRandom();
            // 创建一个DESKeySpec对象
            DESKeySpec desKey = new DESKeySpec(key);
            // 创建一个密匙工厂
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            // 将DESKeySpec对象转换成SecretKey对象
            SecretKey securekey = keyFactory.generateSecret(desKey);
            // Cipher对象实际完成解密操作
            Cipher cipher = Cipher.getInstance("DES");
            // 用密匙初始化Cipher对象
            cipher.init(Cipher.DECRYPT_MODE, securekey, random);
            // 真正开始解密操作
            return cipher.doFinal(data);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }



}
