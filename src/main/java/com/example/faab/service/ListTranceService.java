package com.example.faab.service;

import com.example.faab.entity.ListTrance;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.faab.entity.MSK;
import com.example.faab.entity.UploadFile;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 公众号：java思维导图
 * @since 2021-03-11
 */
public interface ListTranceService extends IService<ListTrance> {

    public String SecProvenance(UploadFile uploadFile, MSK msk);
}
