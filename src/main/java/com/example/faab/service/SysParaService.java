package com.example.faab.service;


import com.example.faab.entity.SysPara;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.faab.entity.UploadFile;

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
}
