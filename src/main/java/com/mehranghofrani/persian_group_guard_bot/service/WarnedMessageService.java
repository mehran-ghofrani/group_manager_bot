package com.mehranghofrani.persian_group_guard_bot.service;

import com.mehranghofrani.persian_group_guard_bot.model.entity.WarnedMessage;
import com.mehranghofrani.persian_group_guard_bot.model.repository.WarnedMessageRepository;
import com.mehranghofrani.persian_group_guard_bot.model.repository.WarnedUserRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class WarnedMessageService {
    @Resource
    WarnedMessageRepository warnedMessageRepository;

    public List<WarnedMessage> findByMessageIdAndChatId(Integer warnedTelegramMessageId, long chatId) {
        return warnedMessageRepository.findByMessageIdAndChatId(warnedTelegramMessageId, chatId);
    }

    public WarnedMessage save(WarnedMessage warnedMessage) {
        return warnedMessageRepository.save(warnedMessage);
    }
}
