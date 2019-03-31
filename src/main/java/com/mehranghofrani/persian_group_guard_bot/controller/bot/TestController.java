package com.mehranghofrani.persian_group_guard_bot.controller.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class TestController extends BaseBotController {
    @Override
    protected boolean isHandlable(Update update) {
        Message message = update.getMessage();
        if (message == null)
            return false;
        String messageText = telegramBot.getTxtCap(message);
        if (messageText == null)
            return false;
        if (!messageText.equals("نگهبان: تست"))
            return false;
        return true;
    }

    @Override
    protected void handle(Update update) {
        telegramBot.replyCurrentMessage("نگهبان فعال است.");
    }
}
