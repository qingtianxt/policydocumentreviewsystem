package stdu.wzw.model;

import lombok.Data;

import java.io.Serializable;

//@Entity
@Data
//@Table(name = "a_city")
public class City  implements Serializable{
    //@Id
    private Integer id;

    //@Column(name = "city_id")
    private String cityId;

   // @Column(name = "city_name")
    private String cityName;

    //@Column(name = "province_id")
    private String provinceId;

}