package com.mehranghofrani.persian_group_guard_bot.controller.bot;

import com.mehranghofrani.persian_group_guard_bot.model.entity.FreeUser;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.ChatMember;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
public class ExclusionController extends BaseBotController {

    @Override
    protected boolean isHandlable(Update update) {
        if (!update.hasMessage())
            return false;
        Message message = update.getMessage();
        Chat chat = message.getChat();
        String messageText = telegramBot.getTxtCap(message);
        if ((message.getText().equals("/exclude"))
                && (message.getReplyToMessage() != null)
                && (chat.isGroupChat() || chat.isSuperGroupChat()))
            return true;
        return false;
    }

    @Override
    protected void handle(Update update) {
        Message message = update.getMessage();
        GetChatAdministrators getChatAdministrators = new GetChatAdministrators();
        getChatAdministrators.setChatId(message.getChatId());
        List<ChatMember> administrators = null;
        try {
            administrators = telegramBot.execute(getChatAdministrators);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        boolean nonAdminFlag = true;
        for (ChatMember chatMember : administrators) {
            if (chatMember.getUser().getId().equals(message.getFrom().getId())) {
                nonAdminFlag = false;
                break;
            }
        }
        if (!nonAdminFlag) {
            if (freeUserService.find(message.getFrom().getId(), message.getChatId()) == null) {
                FreeUser freeUser = new FreeUser();
                freeUser.setChatId(message.getChatId());
                freeUser.setUserId(message.getReplyToMessage().getFrom().getId());
                freeUserService.save(freeUser);
            }
        }
    }

}
