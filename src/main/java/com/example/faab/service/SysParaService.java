package com.example.faab.service;

import com.example.faab.config.lsss.LSSSPolicyParameter;
import com.example.faab.domain.UserVO;
import com.example.faab.entity.PP;
import com.example.faab.entity.SK;
import com.example.faab.entity.SysPara;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.faab.entity.Trans;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 公众号：java思维导图
 * @since 2021-03-09
 */
public interface SysParaService extends IService<SysPara> {

    public SysPara Setup();

    public SysPara getSysPara();

//    public void SecProvenance();
}
