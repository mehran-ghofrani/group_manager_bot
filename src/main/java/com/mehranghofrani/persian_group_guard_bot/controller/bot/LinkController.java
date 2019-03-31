package com.mehranghofrani.persian_group_guard_bot.controller.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class LinkController extends BaseBotController {
    @Override
    protected boolean isHandlable(Update update) {
        if (!update.hasMessage())
            return false;
        Message message = update.getMessage();
        if (!message.hasText() && message.getCaption() == null)
            return false;
        return true;
    }

    @Override
    protected void handle(Update update) {
        Message message = update.getMessage();
        Pattern urlPattern = Pattern.compile(
                ".*[.].*[/]",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        Matcher matcher = urlPattern.matcher(telegramBot.getTxtCap(message));
        if (matcher.find())
            if (freeUserService.find(message.getFrom().getId(), message.getChatId()) == null)
                telegramBot.deleteMessage(message);
    }
}
