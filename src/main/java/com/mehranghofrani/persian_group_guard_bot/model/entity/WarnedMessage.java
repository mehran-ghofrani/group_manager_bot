package com.mehranghofrani.persian_group_guard_bot.model.entity;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "warned_message")
public class WarnedMessage {

    @Id
    @GeneratedValue
    Long id;

    @Column(name = "chat_id")
    private long chatId;

    @Column(name = "message_id")
    private Integer messageId;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Integer> warnerIds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public List<Integer> getWarnerIds() {
        return warnerIds;
    }

    public void setWarnerIds(List<Integer> warnerIds) {
        this.warnerIds = warnerIds;
    }
}
