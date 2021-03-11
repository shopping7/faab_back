package com.example.faab.service.impl;

import com.example.faab.config.DoublePairing;
import com.example.faab.config.Serial;
import com.example.faab.config.cipher.Crytpto;
import com.example.faab.config.cipher.Hash;
import com.example.faab.entity.MSK;
import com.example.faab.entity.PP;
import com.example.faab.entity.SK;
import com.example.faab.entity.UserKey;
import com.example.faab.mapper.UserKeyMapper;
import com.example.faab.service.UserKeyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 */
@Service
public class UserKeyServiceImpl extends ServiceImpl<UserKeyMapper, UserKey> implements UserKeyService {

    @Autowired
    UserKeyMapper mapper;

    //2.密钥（用户私钥）生成
//    private Element d0, d1, d2[], d3, d4, d5, delta_id;
//    private String theta_id;
//    byte[] temp;
    public UserKey SKGen(PP pp, MSK msk, String username, String[] attributes){
        Field Zr = DoublePairing.Zr;
        Field G1 = DoublePairing.G1;
        Element g = G1.newElementFromBytes(pp.getG()).getImmutable();
        Element Y = G1.newElementFromBytes(pp.getY()).getImmutable();
        Element a = Zr.newElementFromBytes(msk.getA()).getImmutable();
        Element alpha = Zr.newElementFromBytes(msk.getAlpha()).getImmutable();

        String k = msk.getK();
        byte[] temp = Crytpto.SEnc(k,username.getBytes());
        String theta_id = Base64.encodeBase64String(temp);
        Element delta_id = Zr.newElementFromHash(theta_id.getBytes(),0,theta_id.length()).getImmutable();
        Element d0 = delta_id.getImmutable();

        Element r = Zr.newRandomElement().getImmutable();
        Element tau = Zr.newRandomElement().getImmutable();
        Element d5 = Zr.newRandomElement().getImmutable();

        Element[] d2 = new Element[attributes.length];
        Element xi[] = new Element[attributes.length];
        Element d1 = g.powZn(alpha.mul(delta_id).sub(a.mul(r))).getImmutable();
        byte[][] d2_b = new byte[d2.length][];
        for (int i = 0; i <attributes.length ; i++) {
            String hash = Hash.md5DigestAsHex(attributes[i]);
            xi[i] = Zr.newElementFromHash(hash.getBytes(),0,hash.length()).getImmutable();
            d2[i] = g.powZn(r.div(xi[i])).getImmutable();
            d2_b[i] = d2[i].toBytes();
        }
        Element d3 = g.powZn(alpha.mul(delta_id)).mul(Y.powZn(tau)).getImmutable();
        Element d4 = g.powZn(tau).getImmutable();
        SK sk = new SK();
        sk.setD0(d0.toBytes());
        sk.setD1(d1.toBytes());
        sk.setD2(d2_b);
        sk.setD3(d3.toBytes());
        sk.setD4(d4.toBytes());
        sk.setD5(d5.toBytes());

        Serial serial = new Serial();
        UserKey userKey = new UserKey();
        userKey.setUsername(username);
        userKey.setSk(serial.serial(sk));
        return userKey;
//        mapper.insert(userKey);
    }

    @Override
    public UserKey getUserKey(String username) {
        return mapper.selectById(username);
    }
}
