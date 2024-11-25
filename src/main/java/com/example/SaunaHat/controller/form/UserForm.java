package com.example.SaunaHat.controller.form;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserForm {

    private int id;

    private String account;

    private String password;

    //パスワード確認用のフィールド
    @Transient
    private String passwordConfirmation;

    private String name;

    private int branchId;

    private int departmentId;

    private int isStopped;

    private Date createdDate;

    private Date updatedDate;

    private String branchName;

    private String departmentName;
}
