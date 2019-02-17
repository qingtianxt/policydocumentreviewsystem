package stdu.wzw.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stdu.wzw.mapper.AreaMapper;
import stdu.wzw.mapper.CityMapper;
import stdu.wzw.mapper.ProvinceMapper;
import stdu.wzw.model.Area;
import stdu.wzw.model.City;
import stdu.wzw.model.Province;
import stdu.wzw.service.PlaceService;

import java.util.List;

@Service("PlaceService")
public class PlaceServiceImpl implements PlaceService {

    @Autowired
    private ProvinceMapper provinceMapper;

    @Autowired
    private CityMapper cityMapper;

    @Autowired
    private AreaMapper areaMapper;

    @Override
    public List<Province> getAllProvince() {
        return provinceMapper.getAll();
    }

    @Override
    public List<City> getCityByProvinceId(String provinceId) {
        return cityMapper.getByProvinceId(provinceId);
    }

    @Override
    public List<Area> getAreaByCityId(String cityId) {
        return areaMapper.getByCityId(cityId);
    }

    @Override
    public List<City> getAllCity() {
        return cityMapper.getAll();
    }

    @Override
    public List<Area> getAllArea() {
        return areaMapper.getAll();
    }

    @Override
    public City getByCityId(String cityId) {
        return cityMapper.getByCityId(cityId);
    }

    @Override
    public Province getByProvinceId(String provinceId) {
        return provinceMapper.getByProvinceId(provinceId);
    }

    @Override
    public Province getBylikeProvince(String province_name) {
        province_name = province_name + "%";
        return provinceMapper.getBylikeProvince(province_name);
    }
}
