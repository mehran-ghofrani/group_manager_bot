package com.mehranghofrani.persian_group_guard_bot.controller.bot;

import com.mehranghofrani.persian_group_guard_bot.model.entity.WarnedUser;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.groupadministration.KickChatMember;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Set;

@Component
public class MemberAdditionController extends BaseBotController {

    @Override
    protected boolean isHandlable(Update update) {
        if (!update.hasMessage())
            return false;
        Message message = update.getMessage();
        if (message.getNewChatMembers() == null)
            return false;
        return true;
    }

    @Override
    protected void handle(Update update) {

        Set<Integer> badUsersIds = warnedUserService.getBadUsersIds();
        Message message = update.getMessage();
        for (User user : message.getNewChatMembers()) {
            if (badUsersIds.contains(user.getId())) {
                KickChatMember kickChatMember = new KickChatMember();
                kickChatMember.setUserId(user.getId());
                kickChatMember.setChatId(message.getChatId());
                kickChatMember.setUntilDate(0);
                try {
                    telegramBot.execute(kickChatMember);
                } catch (TelegramApiException e) {
                    System.out.println("app47: cannot kick user:");
                    e.printStackTrace();
                }
            }
            if (user.getBot()) {
                KickChatMember kickAdder = new KickChatMember();
                kickAdder.setUserId(message.getFrom().getId());
                kickAdder.setChatId(message.getChatId());
                kickAdder.setUntilDate(0);
                try {
                    telegramBot.execute(kickAdder);
                } catch (TelegramApiException e) {
                    System.out.println("app47: cannot kick user:");
                    e.printStackTrace();
                }
                KickChatMember kickBot = new KickChatMember();
                kickBot.setUserId(user.getId());
                kickBot.setChatId(message.getChatId());
                kickBot.setUntilDate(0);
                try {
                    telegramBot.execute(kickBot);
                } catch (TelegramApiException e) {
                    System.out.println("app47: cannot kick user:");
                    e.printStackTrace();
                }
                WarnedUser botAdder = warnedUserService.findByUserId(message.getFrom().getId());
                if (botAdder == null) {
                    botAdder = new WarnedUser();
                    botAdder.setWarnsCount(1000);
                    botAdder.setUserId(message.getFrom().getId());
                }
                warnedUserService.save(botAdder);
            }
        }
    }
}
