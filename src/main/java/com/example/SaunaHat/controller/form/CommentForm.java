package com.example.SaunaHat.controller.form;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CommentForm {
    private int id;

    private String text;

    private int userId;

    private int messageId;

    private Date createdDate;

    private Date updatedDate;

    private String userName;

    private String userAccount;
}
