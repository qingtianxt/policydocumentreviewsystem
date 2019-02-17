package stdu.wzw.controller;

/**
 * 主要用于分页常规参数类抽取
 */
public class BaseController {
    private Integer pageCode = 1;

    public void setPageCode(Integer pageCode) {
        if(pageCode==null){
            pageCode=1;
        }
        this.pageCode = pageCode;
    }
    private Integer pageSize=5;

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageCode() {
        return pageCode;
    }

    public Integer getPageSize() {
        return pageSize;
    }

}
