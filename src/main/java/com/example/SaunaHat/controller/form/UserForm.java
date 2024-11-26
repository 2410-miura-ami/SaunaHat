package com.example.SaunaHat.controller.form;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserForm {


    private int id;

    @Pattern(regexp="^[a-zA-Z0-9]{6,20}+$", message = "アカウントは半角英数字かつ6文字以上20文字以下で入力してください")
    private String account;

    //@Pattern(regexp="^[!-~]{6,20}+$", message = "パスワードは半角文字かつ6文字以上20文字以下で入力してください")
    private String password;

    //パスワード確認用のフィールド
    @Transient
    private String passwordConfirmation;

    @Size(max = 10, message = "氏名は10文字以下で入力してください")
    private String name;

    private int branchId;

    private int departmentId;

    private int isStopped;

    private Date createdDate;

    private Date updatedDate;

    private String branchName;

    private String departmentName;
}
