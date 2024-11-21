package com.example.SaunaHat.repository.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    private int id;
    private String account;
    private String password;
    private String name;
    private int branchId;
    private int departmentId;
    private int isStopped;
    private Date createdDate;
    private Date updatedDate;
}