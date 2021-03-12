package com.example.faab.service;



import com.baomidou.mybatisplus.extension.service.IService;
import com.example.faab.entity.PP;
import com.example.faab.entity.SK;
import com.example.faab.entity.Theta_CT;
import com.example.faab.entity.UploadFile;

import java.io.File;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 公众号：java思维导图
 * @since 2021-03-11
 */
public interface UploadFileService extends IService<UploadFile> {

    public UploadFile Encryption(PP pp, String filename, File file, String ACCESSPOLICY, String[] attributes);

    public Theta_CT Sign(PP pp, SK sk);

    public void Verify(PP pp, Theta_CT theta_ct, UploadFile uploadFile);

    public UploadFile getFile(String filename);

    public List<String> getAllFileName();

    public void deleteFile(String fileName);
}
