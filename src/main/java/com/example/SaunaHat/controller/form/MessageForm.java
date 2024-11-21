package com.example.SaunaHat.controller.form;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MessageForm {

    private int id;

    private String title;

    private String text;

    private int userId;

    private Date createdDate;

    private Date updatedDate;
}

