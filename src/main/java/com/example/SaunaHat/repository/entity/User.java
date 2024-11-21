package com.example.SaunaHat.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

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

    @Column
    private int branchId;

    @Column
    private int departmentId;

    @Column
    private int isStopped;

    @Column
    private Date createdDate;

    @Column
    private Date updatedDate;
}