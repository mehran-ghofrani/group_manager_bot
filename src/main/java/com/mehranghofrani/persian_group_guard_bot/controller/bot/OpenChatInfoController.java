package com.mehranghofrani.persian_group_guard_bot.controller.bot;

import com.mehranghofrani.persian_group_guard_bot.model.entity.OpenChat;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public class OpenChatInfoController extends BaseBotController {
    @Override
    protected boolean isHandlable(Update update) {
        Message message = update.getMessage();
        if (message == null)
            return false;
        if (!message.getText().equals("نگهبان: چت ها"))
            return false;
        return true;
    }

    @Override
    protected void handle(Update update) {
        Iterable<OpenChat> openChats = openChatService.findAll();
        for (OpenChat openChat : openChats) {
            telegramBot.replyCurrentMessage(telegramBot.findChat(openChat.getChatId()).getTitle());
        }
    }
}
