package com.example.faab.service.impl;

import com.example.faab.config.DoublePairing;
import com.example.faab.config.Serial;
import com.example.faab.entity.*;
import com.example.faab.mapper.SysParaMapper;
import com.example.faab.service.SysParaService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.Element;
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

    @Override
    public SysPara Setup() {
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

        return sysPara;
//        sysParaMapper.insert(sysPara);
    }


    @Override
    public SysPara getSysPara() {
        SysPara sysPara = sysParaMapper.selectOne(null);
        return sysPara;
    }
}
