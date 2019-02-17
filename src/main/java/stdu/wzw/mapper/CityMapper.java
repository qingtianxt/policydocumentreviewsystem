package stdu.wzw.mapper;

import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import stdu.wzw.model.City;

import java.util.List;

public interface CityMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(City record);

    int insertSelective(City record);

    City selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(City record);

    int updateByPrimaryKey(City record);

    List<City> getByProvinceId(String provinceId);

    @Select("select * from a_city")
    @ResultMap("BaseResultMap")
    List<City> getAll();

    City getByCityId(String cityId);
}