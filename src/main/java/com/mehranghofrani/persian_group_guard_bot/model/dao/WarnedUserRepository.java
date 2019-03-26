package com.mehranghofrani.persian_group_guard_bot.model.dao;

import com.mehranghofrani.persian_group_guard_bot.model.entity.WarnedUser;
import org.springframework.data.repository.CrudRepository;

public interface WarnedUserRepository extends CrudRepository<WarnedUser, Long> {

    WarnedUser findByUserId(Long userId);
}
