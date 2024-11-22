package com.example.SaunaHat.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String account;

    @Column
    private String password;

    @Column
    private String name;

    //管理画面表示
    //Branchと多対一でリレーションを形成
    @ManyToOne
    @JoinColumn(name="branch_id")
    private Branch branch;

    //Departmentと多対一でリレーションを形成
    @ManyToOne
    @JoinColumn(name="department_id")
    private Department department;

    @Column
    private int isStopped;

    @Column(name = "created_date", insertable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;

    //Messageと一対多でリレーションを形成
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages;

}