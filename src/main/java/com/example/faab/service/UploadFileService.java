package com.example.faab.service;



import com.baomidou.mybatisplus.extension.service.IService;
import com.example.faab.entity.PP;
import com.example.faab.entity.SK;
import com.example.faab.entity.Theta_CT;
import com.example.faab.entity.UploadFile;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 公众号：java思维导图
 * @since 2021-03-11
 */
public interface UploadFileService extends IService<UploadFile> {

    public UploadFile Encryption(PP pp, String M, String ACCESSPOLICY, String[] attributes);

    public Theta_CT Sign(PP pp, SK sk);

    public boolean Verify(PP pp, Theta_CT theta_ct, UploadFile uploadFile);
}
