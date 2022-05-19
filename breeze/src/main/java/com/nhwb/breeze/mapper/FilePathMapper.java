package com.nhwb.breeze.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nhwb.breeze.domain.FilePath;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.bind.annotation.Mapping;

/**
* @author 起风了
* @description 针对表【tb_file_path】的数据库操作Mapper
* @createDate 2022-05-01 23:10:20
* @Entity generator.domain.FilePath
*/
@Mapper
public interface FilePathMapper extends BaseMapper<FilePath> {


}
