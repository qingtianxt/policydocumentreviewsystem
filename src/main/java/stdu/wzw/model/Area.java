package stdu.wzw.model;

import lombok.Data;

import java.io.Serializable;

//@Entity
@Data
//@Table(name = "a_area")
public class Area implements Serializable {

  //  @Id
    private Integer id;

   // @Column(name = "area_id")
    private String areaId;

  //  @Column(name = "area_name")
    private String areaName;

  //  @Column(name = "city_id")
    private String cityId;

}