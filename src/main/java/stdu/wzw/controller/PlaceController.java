package stdu.wzw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stdu.wzw.model.Area;
import stdu.wzw.model.City;
import stdu.wzw.model.Province;
import stdu.wzw.service.PlaceService;

import java.util.List;

/**
 * 用来管理省市县信息
 */
@RestController
@RequestMapping(value = {"/place"})
public class PlaceController {

    @Autowired
    private PlaceService placeService;


    @RequestMapping(value = "/getByProvinceId")
    public Province getByProvinceId(String provinceId) {
        Province province = placeService.getByProvinceId(provinceId);
        province.setProvinceName(province.getProvinceName().replaceAll("省", "").replaceAll("市", ""));
        return province;
    }

    @RequestMapping(value = "/getAllProvince")
    public List<Province> getAllProvince() {
        List<Province> list = placeService.getAllProvince();
        return list;
    }

    @RequestMapping(value = "/getCityByProvinceId")
    public List<City> getCityByProvinceId(String provinceId) {
        List<City> list = placeService.getCityByProvinceId(provinceId);
        return list;
    }

    @RequestMapping(value = "/getAreaByCityId")
    public List<Area> getAreaByCityId(String cityId) {
        List<Area> list = placeService.getAreaByCityId(cityId);
        return list;
    }
}
