package com.nhwb.breeze.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nhwb.breeze.domain.Avoid;


/**
* @author 怒火无边
* @description 针对表【tb_avoid】的数据库操作Service
* @createDate 2022-04-03 18:43:45
*/
public interface AvoidService extends IService<Avoid> {
    Avoid getAvoidByAvoid(Avoid avoid);
}
