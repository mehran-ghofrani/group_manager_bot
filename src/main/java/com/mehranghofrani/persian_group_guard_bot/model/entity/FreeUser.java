package com.mehranghofrani.persian_group_guard_bot.model.entity;

import javax.persistence.*;

@Entity
@Table(name = "free_user")
public class FreeUser {

    @Id
    @GeneratedValue
    Integer id;

    @Column(name = "user_id")
    Integer userId;

    @Column(name = "chat_id")
    Long chatId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}
