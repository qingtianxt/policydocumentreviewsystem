package stdu.wzw.mapper;

import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import stdu.wzw.model.Province;

import java.util.List;

public interface ProvinceMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Province record);

    int insertSelective(Province record);

    Province selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Province record);

    int updateByPrimaryKey(Province record);

    @Select("select * from a_province")
    @ResultMap("BaseResultMap")
    List<Province> getAll();

    Province getByProvinceId(String provinceId);

    Province getBylikeProvince(String provinceName);
}