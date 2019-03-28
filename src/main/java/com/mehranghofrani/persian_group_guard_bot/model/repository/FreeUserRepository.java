package com.mehranghofrani.persian_group_guard_bot.model.repository;

import com.mehranghofrani.persian_group_guard_bot.model.entity.FreeUser;
import org.springframework.data.repository.CrudRepository;

public interface FreeUserRepository extends CrudRepository<FreeUser, Integer> {
    FreeUser findByUserIdAndChatId(Integer userId, Long chatId);
}
