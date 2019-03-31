package com.mehranghofrani.persian_group_guard_bot.controller.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class ForwardController extends BaseBotController {

    @Override
    protected boolean isHandlable(Update update) {
        if (!update.hasMessage())
            return false;
        Message message = update.getMessage();
        if (message.getForwardFromChat() == null)
            return false;
        return true;


    }

    @Override
    protected void handle(Update update) {
        Message message = update.getMessage();
        if (freeUserService.find(message.getFrom().getId(), message.getChatId()) == null) {
            telegramBot.deleteMessage(message);
        }

    }
}
