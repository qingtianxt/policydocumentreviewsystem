package stdu.wzw.model;

import lombok.Data;

/*import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;*/
import java.io.Serializable;

//@Entity
@Data
//@Table(name = "a_province")
public class Province implements Serializable {
   // @Id
    private Integer id;

  //  @Column(name = "province_id")
    private String provinceId;

  //  @Column(name = "province_name")
    private String provinceName;


    @Override
    public String toString() {
        return "Province{" +
                "id=" + id +
                ", provinceId='" + provinceId + '\'' +
                ", provinceName='" + provinceName + '\'' +
                '}';
    }
}