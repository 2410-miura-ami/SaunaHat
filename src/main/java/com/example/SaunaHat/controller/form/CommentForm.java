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

    private Integer messageId;

    private Date createdDate;

    private Date updatedDate;

    private String userName;

    private String userAccount;

    //コメントにエラーが合った際に使用。
    //邪魔になったら阿部に教えてください！
    private String errorText;
}
