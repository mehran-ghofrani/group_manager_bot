package com.mehranghofrani.persian_group_guard_bot.controller.bot;

import com.mehranghofrani.persian_group_guard_bot.model.entity.WarnedUser;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@Component
public class UserInfoController extends BaseBotController {
    @Override
    protected boolean isHandlable(Update update) {
        if (!update.hasMessage())
            return false;
        Message message = update.getMessage();
        if (!message.getFrom().getId().equals(87654811))
            return false;
        if (!message.hasText())
            return false;
        if (!message.getText().equals("/userInfo"))
            return false;
        if (message.getReplyToMessage() == null)
            return false;
        return true;
    }

    @Override
    protected void handle(Update update) {
        Message receivedMessage = update.getMessage();
        Message repliedMessage = receivedMessage.getReplyToMessage();
        User sourceUser = repliedMessage.getForwardFrom();
        if (sourceUser == null || sourceUser.getId().equals(receivedMessage.getForwardFrom().getId())) {
            telegramBot.replyCurrentMessage("msg is not from users");
            return;
        }
        WarnedUser printingWarnedUser = warnedUserService.findByUserId(sourceUser.getId());
        String sendingMessageText = printingWarnedUser.toString();
        telegramBot.replyCurrentMessage(sendingMessageText);
    }
}
