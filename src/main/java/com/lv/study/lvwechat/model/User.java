package com.lv.study.lvwechat.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "tbl_wechat_user")
@Setter
@Getter
public class User {

    @Id
    private String id;

    @Column
    private String openid;

    @Column
    private Date created_date;

    @Column
    private Date last_updated_date;
}
