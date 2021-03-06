package com.mehranghofrani.persian_group_guard_bot.service;

import com.mehranghofrani.persian_group_guard_bot.model.entity.OpenChat;
import com.mehranghofrani.persian_group_guard_bot.model.repository.OpenChatRepository;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Chat;

import javax.annotation.Resource;

@Service
public class OpenChatService {
    @Resource
    OpenChatRepository openChatRepository;

    public OpenChat find(Long id) {
        return openChatRepository.findByChatId(id);
    }

    public OpenChat save(OpenChat openChat) {
        return openChatRepository.save(openChat);
    }

    public Iterable<OpenChat> findAll() {
        return openChatRepository.findAll();
    }
}
