package stdu.wzw.mapper;

import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import stdu.wzw.model.SensitiveWord;

import java.util.List;

public interface SensitiveWordMapper {
    int deleteByPrimaryKey(Integer sensitiveId);

    int insert(SensitiveWord record);

    int insertSelective(SensitiveWord record);

    SensitiveWord selectByPrimaryKey(Integer sensitiveId);

    int updateByPrimaryKeySelective(SensitiveWord record);

    int updateByPrimaryKey(SensitiveWord record);

    List<SensitiveWord> findBylist();

    @Select("select * from sensitiveword")
    @ResultMap("BaseResultMap")
    List<SensitiveWord> findAll();

   /* List<SensitiveWord> findByType();*/
}