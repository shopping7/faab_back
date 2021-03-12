package com.example.faab.mapper;

import com.example.faab.entity.UploadFile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 公众号：java思维导图
 * @since 2021-03-11
 */
public interface UploadFileMapper extends BaseMapper<UploadFile> {

    @Select("SELECT file_name FROM upload_file")
    public List<String> getAllFileName();
}
