package com.mehranghofrani.persian_group_guard_bot.controller.bot;

import com.mehranghofrani.persian_group_guard_bot.model.entity.WarnedUser;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class UnbanController extends BaseBotController {
    @Override
    protected boolean isHandlable(Update update) {
        if (!update.hasMessage())
            return false;
        Message message = update.getMessage();
        if (!message.getText().equals("/unbanUser"))
            return false;
        if (!message.getFrom().getId().equals(87654811))
            return false;
        return true;
    }

    @Override
    protected void handle(Update update) {
        Message message = update.getMessage();
        int userId = Integer.valueOf(telegramBot.getTxtCap(message).substring(11));
        WarnedUser warnedUser = warnedUserService.findByUserId(userId);
        if (warnedUser != null) {
            warnedUser.setWarnsCount(0);
            warnedUser.setUnbanCount(warnedUser.getUnbanCount() + 1);
            warnedUserService.save(warnedUser);
        } else {
            telegramBot.sendTextMessage("user not found", 87654811L, null);
        }
    }
}
