package com.example.faab.config;

import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.util.Random;

public class DoublePairing {
   public static Pairing pairing;
   public static Field G1, GT, Zr, K;

   public static void getStart(){
      pairing = PairingFactory.getPairing("application.properties");
      PairingFactory.getInstance().setUsePBCWhenPossible(true);
      if(!pairing.isSymmetric()){
         throw new RuntimeException("密钥不对称");
      }

      Zr = pairing.getZr();
      G1 = pairing.getG1();
      K = pairing.getG2();
      GT = pairing.getGT();
   }

   public static String setK(int length) { //取一段随机的字符串，用作密钥生成
      String base = "abcdefghijklmnopqrstuvwxyz0123456789";
      int randomNum;
      char randomChar;
      Random random = new Random();
      // StringBuffer类型的可以append增加字符
      StringBuffer str = new StringBuffer();

      for (int i = 0; i < length; i++) {
         // 可生成[0,n)之间的整数，获得随机位置
         randomNum = random.nextInt(base.length());
         // 获得随机位置对应的字符
         randomChar = base.charAt(randomNum);
         // 组成一个随机字符串
         str.append(randomChar);
      }
//      k = str.toString();
        return str.toString();
   }
}