package com.example.faab.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.faab.config.AccessControlEngine;
import com.example.faab.config.DoublePairing;
import com.example.faab.config.Serial;
import com.example.faab.config.cipher.Crytpto;
import com.example.faab.config.cipher.CrytptpFile;
import com.example.faab.config.cipher.Hash;
import com.example.faab.config.lsss.LSSSPolicyParameter;
import com.example.faab.config.lsss.lw10.LSSSLW10Engine;
import com.example.faab.config.parser.ParserUtils;
import com.example.faab.config.parser.PolicySyntaxException;
import com.example.faab.entity.*;
import com.example.faab.mapper.UploadFileMapper;
import com.example.faab.service.UploadFileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 公众号：java思维导图
 * @since 2021-03-11
 */
@Service
public class UploadFileServiceImpl extends ServiceImpl<UploadFileMapper, UploadFile> implements UploadFileService {


    @Autowired
    UploadFileMapper mapper;

    //4.多媒体加密阶段
    private byte[] CM;
    private Element VK, C0, C1, C2, C3[], C4[],rou[];
    public UploadFile Encryption(PP pp,String filename, File file, String ACCESSPOLICY, String[] attributes){
        Pairing pairing = DoublePairing.pairing;
        Field G1 = DoublePairing.G1;
        Field Zr = DoublePairing.Zr;
        Field GT = DoublePairing.GT;
        Element g = G1.newElementFromBytes(pp.getG()).getImmutable();//generator of group
        Element Y = G1.newElementFromBytes(pp.getY()).getImmutable();
        Element Z = GT.newElementFromBytes(pp.getZ()).getImmutable();
        Element s = Zr.newRandomElement().getImmutable();
        String accessPolicyString = ACCESSPOLICY;

        // Using Lewko-Waters LSSS
        AccessControlEngine accessControlEngine = LSSSLW10Engine.getInstance();
        LSSSPolicyParameter lsssPolicyParameter = null;
        int[][] accessPolicy = null;
        Map<String, Element> lambdaElementsMap = null;
        try {
            // parse access policy
            accessPolicy = ParserUtils.GenerateAccessPolicy(accessPolicyString);
            //生成访问策略对应的属性集合，如策略为"A and B and (C or D)"，生成集合String[] rhos = {A,B,C,D};
            String[] rhos = ParserUtils.GenerateRhos(accessPolicyString);
            //强制转化 lsssPolicyParameter类提供了一些方法供后面算法使用 如LSSS矩阵的各个元素为lsssPolicyParameter.lsssmatrix[i][j]
            lsssPolicyParameter = (LSSSPolicyParameter) accessControlEngine.generateAccessControl(accessPolicy, rhos);

            // secret sharing, 说明文档中的变量为lamada_i = A_i*v
            lambdaElementsMap = accessControlEngine.secretSharing(pairing, s, lsssPolicyParameter);
        } catch (PolicySyntaxException e) {
            // throw if invalid access policy representation.
            System.out.println("invalid access policy representation.");
        } catch (Exception e){
            System.out.println("access policy format occurs errors");
        }

        String ssss = lsssPolicyParameter.toString();
        //logger.info("LSSS={}", ssss);
        String[] rhos = lsssPolicyParameter.getRhos();


        Element Γ = GT.newRandomElement().getImmutable();
        String kf = Γ.toString();
        //System.out.println(Γ);
        try {
            CM = CrytptpFile.encrypt(file,kf.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String hash = Hash.md5DigestAsHex(Γ.toString()+ Base64.encodeBase64String(CM));
        VK = G1.newElementFromHash(hash.getBytes(),0,hash.length()).getImmutable();

        C3 = new Element[lsssPolicyParameter.getRow()];
        C4 = new Element[lsssPolicyParameter.getRow()];
        rou = new Element[lsssPolicyParameter.getRow()];
        Element[] lambda = new Element[lsssPolicyParameter.getRow()];
        Element s_1 = Zr.newRandomElement().getImmutable();


        C0 = Γ.mul(Z.powZn(s)).getImmutable();
        C1 = g.powZn(s).getImmutable();
        C2 = Y.powZn(s_1).getImmutable();
        byte[][] C3_b = new byte[C3.length][];
        byte[][] C4_b = new byte[C4.length][];
        for (int i = 0; i < lsssPolicyParameter.getRow() ; i++) {
            lambda[i] = lambdaElementsMap.get(rhos[i]).getImmutable();
            String hash2 = Hash.md5DigestAsHex(rhos[i]);
            rou[i] = Zr.newElementFromHash(hash2.getBytes(), 0, hash2.length()).getImmutable();
            C3[i] = rou[i].mul(lambda[i]).div(s_1).getImmutable();
            C3_b[i] = C3[i].toBytes();
            C4[i] = Y.powZn(lambda[i]).getImmutable();
            C4_b[i] = C4[i].toBytes();
        }

        CT ct = new CT();
        ct.setC0(C0.toBytes());
        ct.setC1(C1.toBytes());
        ct.setC2(C2.toBytes());
        ct.setC3(C3_b);
        ct.setC4(C4_b);
        ct.setCm(CM);
        Serial serial = new Serial();
        UploadFile uploadFile = new UploadFile();
        uploadFile.setCt(serial.serial(ct));
        uploadFile.setFileName(filename);
        uploadFile.setLsssPolicy(serial.serial(lsssPolicyParameter));
        uploadFile.setVkm(VK.toBytes());

        return uploadFile;
//        mapper.insert(uploadFile);
    }

    //5.多媒体密文签名
    private Element T1, T2, T3, T4, U1, U2, U3, c, phi_beta, phi_delta_id, hash;
    private String CT;
    private long st;
    public Theta_CT Sign(PP pp, SK sk){
        Field G1 = DoublePairing.G1;
        Field Zr = DoublePairing.Zr;
        Field GT = DoublePairing.GT;
        Element f = G1.newElementFromBytes(pp.getF()).getImmutable();
        Element h = G1.newElementFromBytes(pp.getH()).getImmutable();
        Element g = G1.newElementFromBytes(pp.getG()).getImmutable();
        Element d0 = Zr.newElementFromBytes(sk.getD0());
        Element delta_id = d0.getImmutable();
        Element d3 = G1.newElementFromBytes(sk.getD3()).getImmutable();
        Element d4 = G1.newElementFromBytes(sk.getD4()).getImmutable();
        Element Z = GT.newElementFromBytes(pp.getZ()).getImmutable();
        Element Z_1 = GT.newElementFromBytes(pp.getZ_1()).getImmutable();
        Element beta = Zr.newRandomElement().getImmutable();
        Element r_beta = Zr.newRandomElement().getImmutable();
        Element r_delta_id = Zr.newRandomElement().getImmutable();
        T1 = g.powZn(beta).getImmutable();
        T2 = d3.mul(f.powZn(beta)).getImmutable();
        T3 = d4.mul(h.powZn(beta)).getImmutable();
        //T4
        st = System.currentTimeMillis();
        String str = st + "";
        String sum1 = ""  , sum2 = "";
        for (int i = 0; i < C3.length ; i++) {
            sum1 = sum1 + C3[i].toString();
            sum2 = sum2 + C4[i].toString();
        }
        CT = C0.toString() + C1.toString() + C2.toString() + sum1 + sum2 + Base64.encodeBase64String(CM);
        String md5 = Hash.md5DigestAsHex(CT + str);
        hash = G1.newElementFromHash(md5.getBytes(),0,md5.length()).getImmutable();
        T4 = hash.powZn(delta_id).getImmutable();

        U1 = Z_1.powZn(r_beta).mul(Z.powZn(r_delta_id)).getImmutable();
        U2 = g.powZn(r_beta).getImmutable();
        U3 = hash.powZn(r_delta_id).getImmutable();
        String longstr = T1.toString()+T2.toString()+T3.toString()+T4.toString()+U1.toString()+U2.toString()
                +U3.toString()+ CT + str;
        String md5_1 = Hash.md5DigestAsHex(longstr);
        c = Zr.newElementFromHash(md5_1.getBytes(),0,md5_1.length()).getImmutable();
        phi_beta = r_beta.sub(c.mul(beta)).getImmutable();
        phi_delta_id = r_delta_id.sub(c.mul(delta_id)).getImmutable();

        Theta_CT theta_ct = new Theta_CT();
        theta_ct.setT1(T1.toBytes());
        theta_ct.setT2(T2.toBytes());
        theta_ct.setT3(T3.toBytes());
        theta_ct.setT4(T4.toBytes());
        theta_ct.setPhi_beta(phi_beta.toBytes());
        theta_ct.setPhi_delta_id(phi_delta_id.toBytes());
        theta_ct.setC(c.toBytes());
        theta_ct.setSt(st);
        return theta_ct;
    }

//6 签名校验
    public void Verify(PP pp, Theta_CT theta_ct, UploadFile uploadFile){
        Field Zr = DoublePairing.Zr;
        Field G1 = DoublePairing.G1;
        Pairing pairing = DoublePairing.pairing;
        Field GT = DoublePairing.GT;
        Serial serial = new Serial();
        CT ct = (CT)serial.deserial(uploadFile.getCt());
        byte[] CM = ct.getCm();
        Element T1 = G1.newElementFromBytes(theta_ct.getT1()).getImmutable();
        Element T2 = G1.newElementFromBytes(theta_ct.getT2()).getImmutable();
        Element T3 = G1.newElementFromBytes(theta_ct.getT3()).getImmutable();
        Element T4 = G1.newElementFromBytes(theta_ct.getT4()).getImmutable();
        Element c = Zr.newElementFromBytes(theta_ct.getC()).getImmutable();
        Element phi_beta = Zr.newElementFromBytes(theta_ct.getPhi_beta()).getImmutable();
        Element phi_delta_id = Zr.newElementFromBytes(theta_ct.getPhi_delta_id()).getImmutable();
        long st = theta_ct.getSt();

        Element Y = G1.newElementFromBytes(pp.getY()).getImmutable();
        Element g = G1.newElementFromBytes(pp.getG()).getImmutable();
        Element Z = GT.newElementFromBytes(pp.getZ()).getImmutable();
        Element Z_1 = GT.newElementFromBytes(pp.getZ_1()).getImmutable();

        String sum1 = ""  , sum2 = "";
        for (int i = 0; i < C3.length ; i++) {
            sum1 = sum1 + C3[i].toString();
            sum2 = sum2 + C4[i].toString();
        }

        String str = st + "";
        String CT_h = C0.toString() + C1.toString() + C2.toString() + sum1 + sum2 + Base64.encodeBase64String(CM);
        String md5 = Hash.md5DigestAsHex(CT_h + str);
        Element hash = G1.newElementFromHash(md5.getBytes(),0,md5.length()).getImmutable();

        Element temp = pairing.pairing(T2,g).div(pairing.pairing(T3,Y)).powZn(c).getImmutable();
        Element U1_1 = Z_1.powZn(phi_beta).mul(Z.powZn(phi_delta_id)).mul(temp).getImmutable();
        Element U2_1 = g.powZn(phi_beta).mul(T1.powZn(c)).getImmutable();
        Element U3_1 = hash.powZn(phi_delta_id).mul(T4.powZn(c)).getImmutable();

        String CT = C0.toString() + C1.toString() + C2.toString() + sum1 + sum2 + Base64.encodeBase64String(CM);
        String longstr = T1.toString()+T2.toString()+T3.toString()+T4.toString()+U1_1.toString()+U2_1.toString()
                +U3_1.toString()+ CT + str;
        String md5_1 = Hash.md5DigestAsHex(longstr);
        Element c_1 = Zr.newElementFromHash(md5_1.getBytes(),0,md5_1.length()).getImmutable();

        if(c_1.isEqual(c)){
            uploadFile.setThetaCt(serial.serial(theta_ct));
            mapper.insert(uploadFile);
        }else{
            System.out.println("签名不合法");
        }
    }

    public UploadFile getFile(String filename){
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("file_name",filename);
        UploadFile uploadFile = mapper.selectOne(queryWrapper);
        return uploadFile;
    }

    @Override
    public List<String> getAllFileName() {
        return mapper.getAllFileName();
    }

    @Override
    public void deleteFile(String fileName) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("file_name",fileName);
        mapper.delete(queryWrapper);
    }
}
