package com.example.faab.service;

import com.example.faab.entity.SK;
import com.example.faab.entity.Trans;
import com.example.faab.entity.UploadFile;

public interface DecryptService {
    public void TKGen(SK sk, String[] attributes);

    public Trans Transform(UploadFile uploadFile, String[] attributes);

    public byte[] Decrypt(SK sk, Trans trans);
}
