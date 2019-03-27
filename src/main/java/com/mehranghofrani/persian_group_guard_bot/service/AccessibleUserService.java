package com.mehranghofrani.persian_group_guard_bot.service;

import com.mehranghofrani.persian_group_guard_bot.model.entity.AccessibleUser;
import com.mehranghofrani.persian_group_guard_bot.model.entity.WarnedUser;
import com.mehranghofrani.persian_group_guard_bot.model.repository.AccessibleUserRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AccessibleUserService {
    @Resource
    AccessibleUserRepository accessibleUserRepository;

    public AccessibleUser findByUserId(Integer id) {
        return accessibleUserRepository.findByUserId(id);
    }

    public AccessibleUser save(AccessibleUser accessibleUser) {
        return accessibleUserRepository.save(accessibleUser);
    }
}
