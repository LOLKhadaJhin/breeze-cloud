package com.nhwb.breeze.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nhwb.breeze.domain.Avoid;
import com.nhwb.breeze.service.AvoidService;
import com.nhwb.breeze.mapper.AvoidMapper;
import org.springframework.stereotype.Service;

/**
* @author 怒火无边
* @description 针对表【tb_avoid】的数据库操作Service实现
* @createDate 2022-04-03 18:43:45
*/
@Service
public class AvoidServiceImpl extends ServiceImpl<AvoidMapper, Avoid> implements AvoidService{

    @Override
    public Avoid getAvoidByAvoid(Avoid avoid) {
        return getOne(new QueryWrapper<Avoid>()
                .lambda()
                .eq(Avoid::getUserId,avoid.getUserId())
                .eq(Avoid::getUuid,avoid.getUuid())
                .eq(Avoid::getAuthorize,true)
        );
    }
}
