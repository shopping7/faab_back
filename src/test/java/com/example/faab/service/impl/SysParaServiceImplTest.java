package com.example.faab.service.impl;

import com.example.faab.config.DoublePairing;
import com.example.faab.config.Serial;
import com.example.faab.domain.UserVO;
import com.example.faab.entity.*;
import com.example.faab.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.List;


@SpringBootTest
class SysParaServiceImplTest {

    @Autowired
    SysParaService sysParaService;

    @Autowired
    UserService userService;

    @Autowired
    UserKeyService userKeyService;

    @Autowired
    DecryptService decryptService;

    @Autowired
    UploadFileService uploadFileService;

    @Autowired
    ListTranceService listTranceService;

    @Test
    public void setup(){
        DoublePairing doublePairing = new DoublePairing();
        doublePairing.getStart();
        sysParaService.Setup();
//        sysParaService.getSysPara();
    }

    @Test
    public void keyGen() {
        DoublePairing.getStart();
        SysPara sysPara = sysParaService.getSysPara();
        UserVO user = userService.getOneUser("001");
        String username = user.getUsername();
        List<String> userAttr = user.getAttr();
        String[] attrs = userAttr.toArray(new String[userAttr.size()]);
        Serial serial = new Serial();
        MSK msk = (MSK)serial.deserial(sysPara.getMsk());
        PP pp = (PP)serial.deserial(sysPara.getPp());
        userKeyService.SKGen(pp,msk,username,attrs,true);
    }

    @Test
    public void uploadTest(){
        try{
            DoublePairing.getStart();
            String username = "001";
//            String[] attrs = {"Doctor","Patient","RafflesHospital"};
            UserVO oneUser = userService.getOneUser(username);
            List<String> userAttr = oneUser.getAttr();
            String[] attrs = userAttr.toArray(new String[userAttr.size()]);
            SysPara sysPara = sysParaService.getSysPara();//1.系统初始化
            Serial serial = new Serial();
            MSK msk = (MSK)serial.deserial(sysPara.getMsk());
            PP pp = (PP)serial.deserial(sysPara.getPp());
            UserKey userKey = userKeyService.getUserKey(username);//2.密钥生成
            SK sk = (SK)serial.deserial(userKey.getSk());
//            decryptService.TKGen(sk, attrs);//3.用户转换密钥生成
            String ACCESSPOLICY = "((RafflesHospital OR CentralHospital) AND (SurgeryDepartment OR (Doctor AND Patient)))";
            File file = new File("C:\\Users\\shopping3\\Downloads\\123.txt");
            UploadFile uploadFile = uploadFileService.Encryption(pp, "124", file, ACCESSPOLICY, attrs);//4.多媒体加密，假设数据内容为fzu
            Theta_CT theta_ct = uploadFileService.Sign(pp, sk);//5.多媒体密文签名
            uploadFileService.Verify(pp,theta_ct,uploadFile);//6.签名校验后上传
//            System.out.println(resultofverify);
//            Trans trans = decryptService.Transform(uploadFile, attrs);//7. 密文转换
//            decryptService.Decrypt(sk, trans);//8. 解密
////            sysParaService.SecProvenance();//9.安全溯源
        }catch(Exception e){
            System.out.println("解密密文失败，有如下三种可能：");
            System.out.println("1.用户属性集合格式错误，只输入访问策略所包含的属性即可！如属性策略为A OR B OR C,用户存在属性{A,B,D},则设置用户属性{A,B}");
            System.out.println("2.访问策略格式错误。正确的格式如(A OR B) AND (C AND (D OR F))");
            System.out.println("3.用户属性不满足访问策略");
        }
    }
    @Test
    public void downloadTest(){
        UploadFile uploadFile = uploadFileService.getFile("12");
        if(uploadFile != null){
            DoublePairing.getStart();
            String username = "001";
//            String[] attrs = {"Doctor","Patient","RafflesHospital"};
            UserVO oneUser = userService.getOneUser(username);
            List<String> userAttr = oneUser.getAttr();
            String[] attrs = userAttr.toArray(new String[userAttr.size()]);
            Serial serial = new Serial();
            UserKey userKey = userKeyService.getUserKey(username);//2.密钥生成
            SK sk = (SK)serial.deserial(userKey.getSk());
            decryptService.TKGen(sk, attrs);//3.用户转换密钥生成
            Trans trans = decryptService.Transform(uploadFile, attrs);//7. 密文转换
            byte[] decrypt = decryptService.Decrypt(sk, trans);//8. 解密
            System.out.println(new String(decrypt));
        }
//        try{
//            DoublePairing.getStart();
//            String username = "001";
//            String[] attrs = {"Doctor","Patient","RafflesHospital"};
//            SysPara sysPara = sysParaService.getSysPara();//1.系统初始化
//            Serial serial = new Serial();
//            MSK msk = (MSK)serial.deserial(sysPara.getMsk());
//            PP pp = (PP)serial.deserial(sysPara.getPp());
//            UserKey userKey = userKeyService.getUserKey(username);//2.密钥生成
//            SK sk = (SK)serial.deserial(userKey.getSk());
//            decryptService.TKGen(sk, attrs);//3.用户转换密钥生成
//
//            Theta_CT theta_ct = uploadFileService.Sign(pp, sk);//5.多媒体密文签名
//            boolean resultofverify = uploadFileService.Verify(pp,theta_ct,uploadFile);//6.签名校验
//            System.out.println(resultofverify);
//            Trans trans = decryptService.Transform(uploadFile, attrs);//7. 密文转换
//            decryptService.Decrypt(sk, trans);//8. 解密
////            sysParaService.SecProvenance();//9.安全溯源
//        }catch(Exception e){
//            System.out.println("解密密文失败，有如下三种可能：");
//            System.out.println("1.用户属性集合格式错误，只输入访问策略所包含的属性即可！如属性策略为A OR B OR C,用户存在属性{A,B,D},则设置用户属性{A,B}");
//            System.out.println("2.访问策略格式错误。正确的格式如(A OR B) AND (C AND (D OR F))");
//            System.out.println("3.用户属性不满足访问策略");
//        }
    }

    @Test
    public void tranceTest() {
        UploadFile uploadFile = uploadFileService.getFile("12");
        if (uploadFile != null) {
            DoublePairing.getStart();
            SysPara sysPara = sysParaService.getSysPara();//1.系统初始化
            Serial serial = new Serial();
            MSK msk = (MSK) serial.deserial(sysPara.getMsk());
            listTranceService.SecProvenance(uploadFile, msk);
        }
    }

}