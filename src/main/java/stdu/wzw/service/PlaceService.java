package stdu.wzw.service;

import stdu.wzw.model.Area;
import stdu.wzw.model.City;
import stdu.wzw.model.Province;

import java.util.List;

public interface PlaceService {
    List<Province> getAllProvince();

    List<City> getCityByProvinceId(String provinceId);

    List<Area> getAreaByCityId(String cityId);

    List<City> getAllCity();

    List<Area> getAllArea();

    City getByCityId(String cityId);

    Province getByProvinceId(String provinceId);

    Province getBylikeProvince(String province_name);
}
