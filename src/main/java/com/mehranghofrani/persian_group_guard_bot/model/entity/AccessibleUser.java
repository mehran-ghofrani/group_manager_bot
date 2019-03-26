package com.mehranghofrani.persian_group_guard_bot.model.entity;

import javax.persistence.*;

@Entity
@Table(name = "accessible_user")
public class AccessibleUser {

    @Id
    @GeneratedValue
    Integer id;

    @Column(name = "user_id")
    Integer userId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
