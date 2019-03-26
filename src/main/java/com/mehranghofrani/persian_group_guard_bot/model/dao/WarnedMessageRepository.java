package com.mehranghofrani.persian_group_guard_bot.model.dao;

import com.mehranghofrani.persian_group_guard_bot.model.entity.WarnedMessage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarnedMessageRepository extends CrudRepository<WarnedMessage, Long> {

    List<WarnedMessage> findByMessageIdAndChatId(Integer messageId, Long chatId);
}
