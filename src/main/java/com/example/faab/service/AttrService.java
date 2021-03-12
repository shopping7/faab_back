package com.example.faab.service;

import com.example.faab.entity.Attr;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 公众号：java思维导图
 * @since 2021-03-11
 */
public interface AttrService extends IService<Attr> {
    public List<Attr> getAllAttr();

    public void deleteAttr(String attr);

    public void addAttr(Attr attr);

    public void editAttr(Attr attr);
}
