package stdu.wzw.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stdu.wzw.mapper.SensitiveWordMapper;
import stdu.wzw.model.SensitiveWord;
import stdu.wzw.service.SensitiveService;

import java.util.List;

@Service("SensitiveService")
public class SensitiveServiceImpl implements SensitiveService {
    @Autowired
    private SensitiveWordMapper sensitiveWordMapper;

    /**
     * 分页获取敏感词
     * @param pageCode
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo<SensitiveWord> findByPage(Integer pageCode, Integer pageSize) {
        PageHelper.startPage(pageCode, pageSize);
        List<SensitiveWord> list=sensitiveWordMapper.findBylist();
        return new PageInfo<SensitiveWord>(list);
    }

    /**
     *获取全部敏感词
     * @return
     */
    @Override
    public List<SensitiveWord> findAll() {
        return sensitiveWordMapper.findAll();
    }

    @Override
    public SensitiveWord getById(int sensitiveId) {
        return sensitiveWordMapper.selectByPrimaryKey(sensitiveId);
    }

    /*@Override
    public List<SensitiveWord> findByType() {
        return sensitiveWordMapper.findByType();
    }*/
}
