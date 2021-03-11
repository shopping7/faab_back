package com.example.faab.service.impl;

import com.example.faab.config.AccessControlEngine;
import com.example.faab.config.DoublePairing;
import com.example.faab.config.Serial;
import com.example.faab.config.UnsatisfiedAccessControlException;
import com.example.faab.config.cipher.Crytpto;
import com.example.faab.config.cipher.Hash;
import com.example.faab.config.lsss.LSSSPolicyParameter;
import com.example.faab.config.lsss.lw10.LSSSLW10Engine;
import com.example.faab.config.parser.ParserUtils;
import com.example.faab.config.parser.PolicySyntaxException;
import com.example.faab.entity.*;
import com.example.faab.mapper.SysParaMapper;
import com.example.faab.mapper.UserKeyMapper;
import com.example.faab.service.SysParaService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.Element;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 *
 */
@Service
public class SysParaServiceImpl extends ServiceImpl<SysParaMapper, SysPara> implements SysParaService {

    @Autowired
    SysParaMapper sysParaMapper;

    @Autowired
    UserKeyMapper userKeyMapper;

    @Override
    public void Setup() {
        Pairing pairing = DoublePairing.pairing;
        Field G1 = DoublePairing.G1;
        Field Zr = DoublePairing.Zr;

        Element g = G1.newRandomElement().getImmutable();//generator of group
        Element f = G1.newRandomElement().getImmutable();
        Element h = G1.newRandomElement().getImmutable();
        Element a = Zr.newRandomElement().getImmutable();
        Element alpha = Zr.newRandomElement().getImmutable();
        Element Y = g.powZn(a).getImmutable();
        Element Z = pairing.pairing(g,g).powZn(alpha).getImmutable();
        Element Z_1 = pairing.pairing(g,f).div(pairing.pairing(Y,h)).getImmutable();
        String k = DoublePairing.setK(40);

        PP pp = new PP();
        pp.setG(g.toBytes());
        pp.setF(f.toBytes());
        pp.setH(h.toBytes());
        pp.setY(Y.toBytes());
        pp.setZ(Z.toBytes());
        pp.setZ_1(Z_1.toBytes());
        //MSK = (α; a; k)

        MSK msk = new MSK();
        msk.setA(a.toBytes());
        msk.setAlpha(alpha.toBytes());
        msk.setK(k);

        Serial serial = new Serial();
        byte[] msk_b = serial.serial(msk);
        byte[] pp_b = serial.serial(pp);

        SysPara sysPara = new SysPara();
        sysPara.setPp(pp_b);
        sysPara.setMsk(msk_b);

        sysParaMapper.insert(sysPara);
    }

    private Pairing pairing;
    private Field Zr, G1, GT;
    @Override
    public SysPara getSysPara() {
        pairing = DoublePairing.pairing;
        G1 = DoublePairing.G1;
        GT = DoublePairing.GT;
        Zr = DoublePairing.Zr;
        SysPara sysPara = sysParaMapper.selectOne(null);
        return sysPara;
//        Serial serial = new Serial();
//        PP pp = (PP)serial.deserial(sysPara.getPp());
//        MSK msk = (MSK)serial.deserial(sysPara.getMsk());
//        Element g = G1.newElementFromBytes(pp.getG()).getImmutable();//generator of group
//        Element f = G1.newElementFromBytes(pp.getF()).getImmutable();
//        Element h = G1.newElementFromBytes(pp.getH()).getImmutable();
//        Element Y = G1.newElementFromBytes(pp.getY()).getImmutable();
//        Element Z = GT.newElementFromBytes(pp.getZ()).getImmutable();
//        Element Z_1 = GT.newElementFromBytes(pp.getZ_1()).getImmutable();
//        Element a = Zr.newElementFromBytes(msk.getA()).getImmutable();
//        Element alpha = Zr.newElementFromBytes(msk.getAlpha()).getImmutable();
//        String k = msk.getK();

    }

    //3.用户转换密钥生成
    Element td1, td2[], td3, td4;
    public void TKGen(SK sk, String[] attributes){
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

    //4.多媒体加密阶段
    private byte[] CM;
    private Element VK, C0, C1, C2, C3[], C4[],rou[];
    public LSSSPolicyParameter Encryption(PP pp, String M, String ACCESSPOLICY, String[] attributes){
        Element g = G1.newElementFromBytes(pp.getG()).getImmutable();//generator of group
        Element Y = G1.newElementFromBytes(pp.getY()).getImmutable();
        Element Z = GT.newElementFromBytes(pp.getZ()).getImmutable();
        Element s = Zr.newRandomElement().getImmutable();
        String accessPolicyString = ACCESSPOLICY;
        String[] satisfiedRhos = attributes;

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
        CM = Crytpto.SEnc(kf, M.getBytes());
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
        for (int i = 0; i < lsssPolicyParameter.getRow() ; i++) {
            lambda[i] = lambdaElementsMap.get(rhos[i]).getImmutable();
            String hash2 = Hash.md5DigestAsHex(rhos[i]);
            rou[i] = Zr.newElementFromHash(hash2.getBytes(), 0, hash2.length()).getImmutable();
            C3[i] = rou[i].mul(lambda[i]).div(s_1).getImmutable();
            C4[i] = Y.powZn(lambda[i]).getImmutable();
        }

        return lsssPolicyParameter;
    }

    //5.多媒体密文签名
    private Element T1, T2, T3, T4, U1, U2, U3, c, phi_beta, phi_delta_id, hash;
    private String CT;
    private long st;
    public void Sign(PP pp, SK sk, LSSSPolicyParameter lsssPolicyParameter){
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
        for (int i = 0; i < lsssPolicyParameter.getRow() ; i++) {
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
    }

    //6 签名校验
    public boolean Verify(PP pp){
        Element Y = G1.newElementFromBytes(pp.getY()).getImmutable();
        Element g = G1.newElementFromBytes(pp.getG()).getImmutable();
        Element Z = GT.newElementFromBytes(pp.getZ()).getImmutable();
        Element Z_1 = GT.newElementFromBytes(pp.getZ_1()).getImmutable();
        Element U1_1, U2_1, U3_1,temp;
        temp = pairing.pairing(T2,g).div(pairing.pairing(T3,Y)).powZn(c).getImmutable();
        U1_1 = Z_1.powZn(phi_beta).mul(Z.powZn(phi_delta_id)).mul(temp).getImmutable();
        U2_1 = g.powZn(phi_beta).mul(T1.powZn(c)).getImmutable();
        U3_1 = hash.powZn(phi_delta_id).mul(T4.powZn(c)).getImmutable();
        return U1_1.isEqual(U1) && U2_1.isEqual(U2) && U3_1.isEqual(U3);
    }

    //7 密文转换
    private Element ct;
    public void Transform(boolean result,LSSSPolicyParameter lsssPolicyParameter, String[] attributes) {
        if (result){
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
            ct = pair1.mul(pair2).div(pair3).getImmutable();
        }else{
            System.out.println("signature is invalid");
        }
    }

    //8 解密
    public void Decrypt(SK sk, boolean result){
        Element d0 = Zr.newElementFromBytes(sk.getD0()).getImmutable();
        Element d5 = Zr.newElementFromBytes(sk.getD5()).getImmutable();
        if (result){
            Element temp = ct.powZn(d0.mulZn(d5).mul(2).invert()).getImmutable();
            Element Γ = C0.div(temp).getImmutable();
            //System.out.println(Γ);
            String hash = Hash.md5DigestAsHex(Γ.toString()+ Base64.encodeBase64String(CM));
            Element VK_1 = G1.newElementFromHash(hash.getBytes(),0,hash.length()).getImmutable();

            if (VK_1.isEqual(VK)){
                String kf = Γ.toString();
                byte[] m = Crytpto.SDec(kf, CM);
                String M = new String(m);
                System.out.println("明文M = " + M);
            }else{
                System.out.println("解密失败");
            }
        }
    }

    //9 安全溯源(追踪恶意用户id)
//    public void SecProvenance(){
//        //if id:001 user in the demo goes wrongs.
//        Element test = hash.powZn(Zr.newElementFromHash(theta_id.getBytes(),0,theta_id.length()).getImmutable());
//
//        //功能要完善,这里要遍历追踪列表List
//        if (test.isEqual(T4)){
//            byte[] id = Crytpto.SDec(k,temp);
//            String Id = new String(id);
//            System.out.println("叛变用户ID = " + Id);
//        }
//    }
}
