package com.mehranghofrani.persian_group_guard_bot.model.entity;

import javax.persistence.*;

@Entity
@Table(name = "open_chat")
public class OpenChat {

    @Id
    @GeneratedValue
    Integer id;

    @Column(name = "chat_id")
    Long chatId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}
