package stdu.wzw.mapper;

import stdu.wzw.model.Authority;

public interface AuthorityMapper {
    int deleteByPrimaryKey(Integer authorityId);

    int insert(Authority record);

    int insertSelective(Authority record);

    Authority selectByPrimaryKey(Integer authorityId);

    int updateByPrimaryKeySelective(Authority record);

    int updateByPrimaryKey(Authority record);
}