package com.example.SaunaHat.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "departments")
@Getter
@Setter
public class Department {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String name;

    //Userと一対多でリレーションを形成
    //mappedByがある所は読み取り専用になり指定しているところdepartment(User.department)がリレーションシップのオーナーと言う意味
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", insertable = true, updatable = false)
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date", insertable = false, updatable = false)
    private Date updatedDate;
}
