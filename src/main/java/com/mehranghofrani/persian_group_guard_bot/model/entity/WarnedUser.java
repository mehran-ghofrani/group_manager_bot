package com.mehranghofrani.persian_group_guard_bot.model.entity;

import javax.persistence.*;

@Entity
@Table(name = "warned_user")
public class WarnedUser {

    @Id
    @GeneratedValue
    Long id;

    @Column(name = "user_id")
    Integer userId;

    @Column(name = "warns_count")
    Integer warnsCount;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getWarnsCount() {
        return warnsCount;
    }

    public void setWarnsCount(Integer warnsCount) {
        this.warnsCount = warnsCount;
    }
}
