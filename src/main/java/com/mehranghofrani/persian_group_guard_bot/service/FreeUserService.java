package com.mehranghofrani.persian_group_guard_bot.service;

import com.mehranghofrani.persian_group_guard_bot.model.entity.FreeUser;
import com.mehranghofrani.persian_group_guard_bot.model.repository.FreeUserRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class FreeUserService {
    @Resource
    FreeUserRepository freeUserRepository;

    public FreeUser find(Integer userId, Long chatId) {
        return freeUserRepository.findByUserIdAndChatId(userId, chatId);
    }

    public FreeUser save(FreeUser freeUser) {
        return freeUserRepository.save(freeUser);
    }
}
