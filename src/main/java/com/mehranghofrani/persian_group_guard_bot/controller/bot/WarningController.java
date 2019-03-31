package com.mehranghofrani.persian_group_guard_bot.controller.bot;

import com.mehranghofrani.persian_group_guard_bot.model.entity.OpenChat;
import com.mehranghofrani.persian_group_guard_bot.model.entity.WarnedMessage;
import com.mehranghofrani.persian_group_guard_bot.model.entity.WarnedUser;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMembersCount;
import org.telegram.telegrambots.meta.api.methods.groupadministration.KickChatMember;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Component
public class WarningController extends BaseBotController {



    @Override
    protected boolean isHandlable(Update update) {
        if (!update.hasMessage())
            return false;
        Message message = update.getMessage();
        Chat chat = message.getChat();
        String messageText = telegramBot.getTxtCap(message);
        if ((messageText.equals("/warn"))
                && (message.getReplyToMessage() == null)
                && (chat.isGroupChat() || chat.isSuperGroupChat()))
            return true;
        return false;
    }

    @Override
    protected void handle(Update update) {
        Message message = update.getMessage();
        Message repliedMessage = message.getReplyToMessage();
        long chatId = message.getChat().getId();
        int warnerId = message.getFrom().getId();
        Integer warnedTelegramMessageId = repliedMessage.getMessageId();
        WarnedMessage warnedMessage;
        List<Integer> warnerIds = null;
        List<WarnedMessage> warnedMessageList = warnedMessageService.findByMessageIdAndChatId(warnedTelegramMessageId, chatId);
        if (warnedMessageList .size() == 0) {
            warnedMessage = new WarnedMessage();
            warnedMessage.setChatId(chatId);
            warnedMessage.setMessageId(warnedTelegramMessageId);
            warnerIds = new LinkedList<Integer>();
            warnedMessage.setWarnerIds(warnerIds);
            warnedMessage = warnedMessageService.save(warnedMessage);
            warnedMessage = (WarnedMessage) Hibernate.unproxy(warnedMessage);
        } else {
            warnedMessage = warnedMessageList .get(0);
        }
//            warnedMessage = (WarnedMessage)Hibernate.unproxy(warnedMessage);

        warnerIds = warnedMessage.getWarnerIds();
        Boolean duplicatedWarnFlag = false;
        for (Integer WId : warnerIds) {
            if (WId.equals(warnerId)) {
                duplicatedWarnFlag = true;
                break;
            }
        }
        if (!duplicatedWarnFlag) {
            warnerIds.add(warnerId);
            warnedMessageService.save(warnedMessage);
//                if (warnLimitByGroup.get(chatId) == null) {
//                    warnLimitByGroup.put(chatId, 25);
//                }
            int chatMemberCount = 0;
            GetChatMembersCount getChatMemberCount = new GetChatMembersCount();
            getChatMemberCount.setChatId(chatId);
            try {
                chatMemberCount = telegramBot.execute(getChatMemberCount);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

            WarnedUser warnedUser = warnedUserService.findByUserId(message.getFrom().getId());
            if (warnedUser == null) {
                warnedUser = new WarnedUser();
                warnedUser.setUserId(message.getFrom().getId());
                warnedUser.setWarnsCount(0);
            }
            if (warnerIds.size() >= (int) Math.ceil((10d * chatMemberCount) / 100)) {
                telegramBot.deleteMessage(repliedMessage);
                String messageText = "این پیام که توسط شما به گروه زیر ارسال شده بود به دلیل اخطار های مکرر کاربران از گروه حذف شد:" + "\r\n"
                        + "نام گروه:" + "\r\n"
                        + message.getChat().getTitle() + "\r\n"
                        + "متن پیام:" + "\r\n"
                        + (message.getReplyToMessage().getText() != null ?  message.getReplyToMessage().getText() : "بدون متن") + "\r\n"
                        + "زمان پیام:" + "\r\n"
                        + new Date(message.getDate()).toString() + "\r\n"
                        + "در صورت تکرار اخطار ها از همه ی گروه هایی که این روبات در آن حضور دارد حذف میشوید.";
                OpenChat openChat = openChatService.findByUserId(message.getReplyToMessage().getFrom().getId());
                if (openChat != null)
                    telegramBot.sendTextMessage(messageText, (long) openChat.getChatId(), null);
                warnedUser.setWarnsCount(warnedUser.getWarnsCount() + 1);
                warnedUserService.save(warnedUser);
            }
            if (warnedUser.getWarnsCount() >= 1) {
                KickChatMember kickChatMember = new KickChatMember();
                kickChatMember.setChatId(message.getChatId());
                kickChatMember.setUserId(message.getFrom().getId());
                kickChatMember.setUntilDate(0);
                try {
                    telegramBot.execute(kickChatMember);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
        telegramBot.deleteMessage(message);
    }


}
