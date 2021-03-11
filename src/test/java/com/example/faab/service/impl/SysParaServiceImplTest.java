package com.example.faab.service.impl;

import com.example.faab.config.DoublePairing;
import com.example.faab.config.Serial;
import com.example.faab.domain.UserVO;
import com.example.faab.entity.*;
import com.example.faab.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        sysParaService.getSysPara();
        UserVO user = userService.getOneUser("001");
        String username = user.getUsername();
        List<String> userAttr = user.getAttr();
        String[] attrs = userAttr.toArray(new String[userAttr.size()]);
//        UserKeyService.SKGen(username,attrs);
    }

    @Test
    public void test(){
        try{
            DoublePairing.getStart();
            String username = "001";
            String[] attrs = {"Doctor","Patient","RafflesHospital"};
            SysPara sysPara = sysParaService.Setup();//1.系统初始化
            Serial serial = new Serial();
            MSK msk = (MSK)serial.deserial(sysPara.getMsk());
            PP pp = (PP)serial.deserial(sysPara.getPp());
            UserKey userKey = userKeyService.SKGen(pp, msk, username, attrs);//2.密钥生成
            SK sk = (SK)serial.deserial(userKey.getSk());
            decryptService.TKGen(sk, attrs);//3.用户转换密钥生成
            String ACCESSPOLICY = "((RafflesHospital OR CentralHospital) AND (SurgeryDepartment OR (Doctor AND Patient)))";
            UploadFile uploadFile = uploadFileService.Encryption(pp, "fzu", ACCESSPOLICY, attrs);//4.多媒体加密，假设数据内容为fzu
            Theta_CT theta_ct = uploadFileService.Sign(pp, sk);//5.多媒体密文签名
            boolean resultofverify = uploadFileService.Verify(pp,theta_ct,uploadFile);//6.签名校验
            System.out.println(resultofverify);
            Trans trans = decryptService.Transform(uploadFile, attrs);//7. 密文转换
            decryptService.Decrypt(sk, trans);//8. 解密
//            sysParaService.SecProvenance();//9.安全溯源
        }catch(Exception e){
            System.out.println("解密密文失败，有如下三种可能：");
            System.out.println("1.用户属性集合格式错误，只输入访问策略所包含的属性即可！如属性策略为A OR B OR C,用户存在属性{A,B,D},则设置用户属性{A,B}");
            System.out.println("2.访问策略格式错误。正确的格式如(A OR B) AND (C AND (D OR F))");
            System.out.println("3.用户属性不满足访问策略");
        }
    }
}