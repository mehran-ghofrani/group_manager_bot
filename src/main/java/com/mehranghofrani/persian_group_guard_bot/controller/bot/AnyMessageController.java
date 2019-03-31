package com.mehranghofrani.persian_group_guard_bot.controller.bot;

import com.mehranghofrani.persian_group_guard_bot.model.entity.OpenChat;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class AnyMessageController extends BaseBotController {
    @Override
    protected boolean isHandlable(Update update) {
        if (!update.hasMessage())
            return false;
        return true;
    }

    @Override
    protected void handle(Update update) {
        Message message = update.getMessage();
        if (openChatService.find(message.getChatId()) == null) {
            OpenChat savingOpenChat = new OpenChat();
            savingOpenChat.setChatId(message.getChatId());
            openChatService.save(savingOpenChat);
        }

    }
}
