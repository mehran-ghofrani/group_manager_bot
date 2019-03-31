package com.mehranghofrani.persian_group_guard_bot.model.repository;

import com.mehranghofrani.persian_group_guard_bot.model.entity.OpenChat;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpenChatRepository extends CrudRepository<OpenChat, Integer> {
    OpenChat findByChatId(Long chatId);

}
