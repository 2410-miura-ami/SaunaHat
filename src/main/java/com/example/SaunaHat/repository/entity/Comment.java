package com.example.SaunaHat.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "comments")
@Getter
@Setter
public class Comment {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String text;

    @Column
    private int userId;

    @Column
    private int messageId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", insertable = true, updatable = false)
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date", insertable = false, updatable = false)
    private Date updatedDate;
}
