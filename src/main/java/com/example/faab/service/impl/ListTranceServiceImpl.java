package com.example.faab.service.impl;

import com.example.faab.config.DoublePairing;
import com.example.faab.config.Serial;
import com.example.faab.config.cipher.Crytpto;
import com.example.faab.config.cipher.Hash;
import com.example.faab.entity.*;
import com.example.faab.mapper.ListTranceMapper;
import com.example.faab.service.ListTranceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 公众号：java思维导图
 * @since 2021-03-11
 */
@Service
public class ListTranceServiceImpl extends ServiceImpl<ListTranceMapper, ListTrance> implements ListTranceService {

    @Autowired
    ListTranceMapper mapper;

    //9 安全溯源(追踪恶意用户id)
    public String SecProvenance(UploadFile uploadFile, MSK msk){
        Field Zr = DoublePairing.Zr;
        Field G1 = DoublePairing.G1;
        Field GT = DoublePairing.GT;
        Serial serial = new Serial();
        String k = msk.getK();
        Theta_CT theta_ct = (Theta_CT) serial.deserial(uploadFile.getThetaCt());
        Element T4 = G1.newElementFromBytes(theta_ct.getT4()).getImmutable();
        long st = theta_ct.getSt();
        CT ct = (CT)serial.deserial(uploadFile.getCt());
        Element C0 = GT.newElementFromBytes(ct.getC0()).getImmutable();
        Element C1 = G1.newElementFromBytes(ct.getC1()).getImmutable();
        Element C2 = G1.newElementFromBytes(ct.getC2()).getImmutable();
        byte[] CM = ct.getCm();
        byte[][] C3_b = ct.getC3();
        byte[][] C4_b = ct.getC4();
        Element[] C3 = new Element[C3_b.length];
        Element[] C4 = new Element[C4_b.length];
        String str = st + "";
        String sum1 = ""  , sum2 = "";
        for (int i = 0; i < C3.length ; i++) {
            C3[i] = Zr.newElementFromBytes(C3_b[i]).getImmutable();
            C4[i] = G1.newElementFromBytes(C4_b[i]).getImmutable();
            sum1 = sum1 + C3[i].toString();
            sum2 = sum2 + C4[i].toString();
        }
        String CT = C0.toString() + C1.toString() + C2.toString() + sum1 + sum2 + Base64.encodeBase64String(CM);
        String md5 = Hash.md5DigestAsHex(CT + str);
        Element hash = G1.newElementFromHash(md5.getBytes(),0,md5.length()).getImmutable();
        List<ListTrance> listTrances = mapper.selectList(null);
        for(ListTrance lt : listTrances){
            byte[] temp = lt.getThetaId();
            String theta_id = Base64.encodeBase64String(temp);
            Element test = hash.powZn(Zr.newElementFromHash(theta_id.getBytes(),0,theta_id.length()).getImmutable());
            if (test.isEqual(T4)){
                byte[] id = Crytpto.SDec(k,temp);
                String Id = new String(id);
                System.out.println("叛变用户ID = " + Id);
                return Id;
            }
        }
        return null;
//
//        //if id:001 user in the demo goes wrongs.
//        Element test = hash.powZn(Zr.newElementFromHash(theta_id.getBytes(),0,theta_id.length()).getImmutable());
//
//        //功能要完善,这里要遍历追踪列表List
//        if (test.isEqual(T4)){
//            byte[] id = Crytpto.SDec(MSK.k1,temp);
//            String Id = new String(id);
//            System.out.println("叛变用户ID = " + Id);
//        }
    }
}
