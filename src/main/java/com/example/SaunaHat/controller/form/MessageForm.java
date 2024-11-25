package com.example.SaunaHat.controller.form;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MessageForm {

    private Integer id;

    private String title;

    private String text;

    private String category;

    private int userId;

    private Date createdDate;

    private Date updatedDate;

    private String userAccount;

    private String userName;
}

