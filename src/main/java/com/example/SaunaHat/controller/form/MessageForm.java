package com.example.SaunaHat.controller.form;

import java.util.Date;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MessageForm {

    private Integer id;

    @Size(max = 30, message = "・件名は30文字以内で入力してください")
    private String title;

    @Size(max = 1000, message = "・本文は1000文字以内で入力してください")
    private String text;

    @Size(max = 10, message = "・カテゴリは10文字以内で入力してください")
    private String category;

    private int userId;

    private Date createdDate;

    private Date updatedDate;

    private String userAccount;

    private String userName;
}

