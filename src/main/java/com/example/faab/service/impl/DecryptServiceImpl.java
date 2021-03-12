package com.example.faab.service.impl;

import com.example.faab.config.AccessControlEngine;
import com.example.faab.config.DoublePairing;
import com.example.faab.config.Serial;
import com.example.faab.config.UnsatisfiedAccessControlException;
import com.example.faab.config.cipher.Crytpto;
import com.example.faab.config.cipher.Hash;
import com.example.faab.config.lsss.LSSSPolicyParameter;
import com.example.faab.config.lsss.lw10.LSSSLW10Engine;
import com.example.faab.entity.CT;
import com.example.faab.entity.SK;
import com.example.faab.entity.Trans;
import com.example.faab.entity.UploadFile;
import com.example.faab.service.DecryptService;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DecryptServiceImpl implements DecryptService {




    private Element td1, td2[], td3, td4;
    public void TKGen(SK sk, String[] attributes){
        Field G1 = DoublePairing.G1;
        Field Zr = DoublePairing.Zr;
        Element d1 = G1.newElementFromBytes(sk.getD1()).getImmutable();
        Element[] d2 = new Element[attributes.length];
        byte[][] d2_b = sk.getD2();
        for (int i = 0; i < d2.length; i++) {
            d2[i] = G1.newElementFromBytes(d2_b[i]).getImmutable();
        }
        Element d3 = G1.newElementFromBytes(sk.getD3()).getImmutable();
        Element d4 = G1.newElementFromBytes(sk.getD4()).getImmutable();
        Element d5 = Zr.newElementFromBytes(sk.getD5()).getImmutable();
        td1 = d1.powZn(d5).getImmutable();
        td2 = new Element[attributes.length];
        for (int i = 0; i <attributes.length ; i++) {
            td2[i] = d2[i].powZn(d5).getImmutable();
        }
        td3 = d3.powZn(d5).getImmutable();
        td4 = d4.powZn(d5).getImmutable();
    }

    //7 密文转换
//    private Element ct_t;
    public Trans Transform(UploadFile uploadFile, String[] attributes) {
        Field GT = DoublePairing.GT;
        Field G1 = DoublePairing.G1;
        Field Zr = DoublePairing.Zr;
        Pairing pairing = DoublePairing.pairing;
        Serial serial = new Serial();
        LSSSPolicyParameter lsssPolicyParameter = (LSSSPolicyParameter)serial.deserial(uploadFile.getLsssPolicy());
        CT ct = (CT)serial.deserial(uploadFile.getCt());
        Element C0 = GT.newElementFromBytes(ct.getC0()).getImmutable();
        Element C1 = G1.newElementFromBytes(ct.getC1()).getImmutable();
        Element C2 = G1.newElementFromBytes(ct.getC2()).getImmutable();
        byte[][] C3_b = ct.getC3();
        byte[][] C4_b = ct.getC4();
        Element[] C3 = new Element[C3_b.length];
        Element[] C4 = new Element[C4_b.length];
        for (int i = 0; i < C3.length; i++) {
            C3[i] = Zr.newElementFromBytes(C3_b[i]).getImmutable();
            C4[i] = G1.newElementFromBytes(C4_b[i]).getImmutable();
        }
        byte[] CM = ct.getCm();
        Element VK = G1.newElementFromBytes(uploadFile.getVkm());

        AccessControlEngine accessControlEngine = LSSSLW10Engine.getInstance();
        Map<String, Element> omegaElementsMap = null;
        String[] satisfiedRhos = attributes;
        try {
            //get vector 'v' which made Mt.mul(v)=(1,0,..,0);
            // *****若lamada*V 可得 秘密S --- 代表用户属性满足访问策略，可以解密密文。
            omegaElementsMap = accessControlEngine.reconstructOmegas(pairing, satisfiedRhos, lsssPolicyParameter);
        }catch (UnsatisfiedAccessControlException e){
            // throw if the given attribute set does not satisfy the access policy represented by access tree.
            System.out.println("User's Attribute Set [ ");
            for (String str:satisfiedRhos
            ) {
                System.out.println(str + ",");
            }
            System.out.println("] does not satisfy the access policy!");
        }


        Element sum = G1.newElement().setToOne();
        Element sum2 = G1.newElement().setToOne();



        for (int i = 0; i < attributes.length ; i++) {

            //获取属性满足访问策略对应的索引，如用户属性ABD，访问策略集合A or B or C and D and E，则获取索引rohindex = 0,1,3
            int rhoindex = lsssPolicyParameter.getIndex(satisfiedRhos[i]);

            sum = sum.mul(td2[i].powZn(C3[rhoindex].mul(omegaElementsMap.get(satisfiedRhos[i])))).getImmutable();
            sum2 = sum2.mul(C4[rhoindex].powZn(omegaElementsMap.get(satisfiedRhos[i]))).getImmutable();
        }
        Element pair1,pair2,pair3;
        pair1 = pairing.pairing(C1,td1.mul(td3)).getImmutable();
        pair2 = pairing.pairing(C2,sum).getImmutable();
        pair3 = pairing.pairing(sum2,td4).getImmutable();
        Element ct_t = pair1.mul(pair2).div(pair3).getImmutable();

        Trans trans = new Trans();
        trans.setCt(ct_t.toBytes());
        trans.setC0(C0.toBytes());
        trans.setCM(CM);
        trans.setVK(VK.toBytes());
        return trans;
    }

    //8 解密
    public byte[] Decrypt(SK sk, Trans trans){
        Field GT = DoublePairing.GT;
        Field G1 = DoublePairing.G1;
        Field Zr = DoublePairing.Zr;
        Element C0 = GT.newElementFromBytes(trans.getC0()).getImmutable();
        Element VK = G1.newElementFromBytes(trans.getVK()).getImmutable();
        byte[] CM = trans.getCM();
        Element ct = GT.newElementFromBytes(trans.getCt()).getImmutable();
        Element d0 = Zr.newElementFromBytes(sk.getD0()).getImmutable();
        Element d5 = Zr.newElementFromBytes(sk.getD5()).getImmutable();
        Element temp = ct.powZn(d0.mulZn(d5).mul(2).invert()).getImmutable();
        Element Γ = C0.div(temp).getImmutable();
        String hash = Hash.md5DigestAsHex(Γ.toString()+ Base64.encodeBase64String(CM));
        Element VK_1 = G1.newElementFromHash(hash.getBytes(),0,hash.length()).getImmutable();

        if (VK_1.isEqual(VK)){
            String kf = Γ.toString();
            byte[] m = Crytpto.SDec(kf, CM);
            String M = new String(m);
            System.out.println(M);
            return m;
        }else{
            System.out.println("解密失败");
            return null;
        }
    }
}
