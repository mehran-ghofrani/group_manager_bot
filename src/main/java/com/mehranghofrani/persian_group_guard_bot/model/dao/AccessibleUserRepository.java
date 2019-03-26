package com.mehranghofrani.persian_group_guard_bot.model.dao;

import com.mehranghofrani.persian_group_guard_bot.model.entity.AccessibleUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessibleUserRepository extends CrudRepository<AccessibleUser, Integer> {
    AccessibleUser findByUserId(Integer userId);

}
