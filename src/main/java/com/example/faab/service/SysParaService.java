package com.example.faab.service;

import com.example.faab.config.lsss.LSSSPolicyParameter;
import com.example.faab.domain.UserVO;
import com.example.faab.entity.PP;
import com.example.faab.entity.SK;
import com.example.faab.entity.SysPara;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 公众号：java思维导图
 * @since 2021-03-09
 */
public interface SysParaService extends IService<SysPara> {

    public void Setup();

    public SysPara getSysPara();

    public void TKGen(SK sk,String[] attributes);

    public LSSSPolicyParameter Encryption(PP pp, String M, String ACCESSPOLICY, String[] attributes);

    public void Sign(PP pp, SK sk, LSSSPolicyParameter lsssPolicyParameter);

    public boolean Verify(PP pp);

    public void Transform(boolean result,LSSSPolicyParameter lsssPolicyParameter, String[] attributes);

    public void Decrypt(SK sk, boolean result);

//    public void SecProvenance();
}
