package stdu.wzw.mapper;

import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import stdu.wzw.model.Area;

import java.util.List;

public interface AreaMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Area record);

    int insertSelective(Area record);

    Area selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Area record);

    int updateByPrimaryKey(Area record);

    List<Area> getByCityId(String cityId);
    @Select("select * from a_area")
    @ResultMap("BaseResultMap")
    List<Area> getAll();
}