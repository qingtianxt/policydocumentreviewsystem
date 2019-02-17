package stdu.wzw.model;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private Integer userId;

    private String username;

    private String password;

    //private Role role;
    private Integer roleId;

    private Date createDate;

    private Integer departmentId;

}