package stdu.wzw.modeljpa;

import lombok.Data;
import stdu.wzw.model.Area;
import stdu.wzw.model.City;
import stdu.wzw.model.Province;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/*@Data
@Entity
@Table(name = "department")
public class DepartmentJpa implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_id")
    private Integer departmentId;

    @Column(name = "department_name")
    private String departmentName;


  @JoinColumn(name = "parent_id")
    @OneToOne(cascade = CascadeType.ALL)
    private DepartmentJpa department;

    @Column(name = "create_date")
    private Date createDate;

 @JoinColumn(name = "province_id",referencedColumnName = "province_id")
    @ManyToOne(cascade = CascadeType.ALL)
    private Province province;

    @JoinColumn(name = "city_id",referencedColumnName = "city_id")
    @ManyToOne(cascade = CascadeType.ALL)
    private City city;

    @JoinColumn(name = "area_id",referencedColumnName = "area_id")
    @ManyToOne(cascade = CascadeType.ALL)
    private Area area;

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public DepartmentJpa getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentJpa department) {
        this.department = department;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }
}*/
