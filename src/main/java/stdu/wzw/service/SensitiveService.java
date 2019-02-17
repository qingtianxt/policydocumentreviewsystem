package stdu.wzw.service;

import com.github.pagehelper.PageInfo;
import stdu.wzw.model.SensitiveWord;

import java.util.List;

public interface SensitiveService {
    PageInfo<SensitiveWord> findByPage(Integer pageCode, Integer pageSize);

    List<SensitiveWord> findAll();

    SensitiveWord getById(int sensitiveId);

    /* List<SensitiveWord> findByType();*/
}
