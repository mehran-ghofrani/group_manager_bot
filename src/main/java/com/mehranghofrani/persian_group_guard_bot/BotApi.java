package com.mehranghofrani.persian_group_guard_bot;

import com.ghasemkiani.util.icu.PersianCalendar;
import com.ibm.icu.util.ULocale;
import com.mehranghofrani.persian_group_guard_bot.model.dao.AccessibleUserRepository;
import com.mehranghofrani.persian_group_guard_bot.model.dao.WarnedMessageRepository;
import com.mehranghofrani.persian_group_guard_bot.model.entity.AccessibleUser;
import com.mehranghofrani.persian_group_guard_bot.model.entity.WarnedMessage;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.api.methods.groupadministration.GetChatMemberCount;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.ChatMember;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import javax.annotation.Resource;
import javax.xml.crypto.Data;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Component
public class BotApi extends TelegramLongPollingBot {

    @Resource
    WarnedMessageRepository warnedMessageRepository;
    @Resource
    AccessibleUserRepository accessibleUserRepository;
//    HashMap<String, WarnedMessage> warnedMessagesByChatMessageId = new HashMap();
    HashMap<Long, Integer> warnLimitByGroup = new HashMap<Long, Integer>();

    public void onUpdateReceived(Update update) {
        Main.mainPrintStream.println("update recived");
        if (update.hasMessage()) {
            try {
                //start logic
                Message message = update.getMessage();
                answerPv(message);
                answerTest(message);
                setLimit(message);
                warn(message);
                checkLinks(message);
                checkForward(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void answerPv(Message message) {
        if (message.getChat().isUserChat())
            if (accessibleUserRepository.findByUserId(message.getFrom().getId()) == null) {
                AccessibleUser accessibleUser = new AccessibleUser();
                accessibleUser.setUserId(message.getFrom().getId());
                accessibleUserRepository.save(accessibleUser);
            }
    }

    private void checkForward(Message message) {
        if (message.getForwardFromChat() != null) {
            deleteMessage(message);
        }
    }

    private void answerTest(Message message) {
        if (getTxtCap(message).equals("امتحان روبات")) {
            sendTextMessage("روبات در حال کار", message.getChatId(), message.getMessageId());
        }
    }

    private String getTxtCap(Message message) {
        if (message.getText() != null) {
            return message.getText();
        }
        else {
            return message.getCaption();
        }
    }

    private void checkLinks(Message message) {
        Pattern urlPattern = Pattern.compile(
                ".*[.].*[/]",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        Matcher matcher = urlPattern.matcher(getTxtCap(message));
        if (matcher.find())
            deleteMessage(message);
    }

    private void setLimit(Message message) {
        if (getTxtCap(message).startsWith("setlimit")) {
            GetChatAdministrators getChatAdministrators = new GetChatAdministrators();
            getChatAdministrators.setChatId(message.getChatId());
            List<ChatMember> administrators = null;
            try {
                administrators = execute(getChatAdministrators);
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
                try {
                    int limit = Integer.valueOf(getTxtCap(message).substring(9));
                    if (limit >= 0 && limit <= 100) {
                        warnLimitByGroup.put(message.getChatId(), limit);
                        int chatMemberCount = 0;
                        GetChatMemberCount getChatMemberCount = new GetChatMemberCount();
                        getChatMemberCount.setChatId(message.getChatId());
                        try {
                            chatMemberCount = execute(getChatMemberCount);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                        sendTextMessage("from now, " + (int) Math.ceil(((double) limit * chatMemberCount) / 100) + " warnings(more than " + limit + "% of members count) are needed to delete a warned message.",
                                message.getChatId(), message.getMessageId());
                    } else {
                        sendTextMessage("invalid argument", message.getChatId(), message.getMessageId());
                    }
                } catch (Exception ex) {
                    sendTextMessage("illegal argument...\nsetlimit [0-100]",
                            message.getChatId(), message.getMessageId());
                }
            }
        }
    }

    private void warn(Message message) throws TelegramApiException {
        if (getTxtCap(message).equals("warn") && (message.getReplyToMessage() != null)) {
            Message repliedMessage = message.getReplyToMessage();
            long chatId = message.getChat().getId();
            int warnerId = message.getFrom().getId();
            Integer warnedTelegramMessageId = repliedMessage.getMessageId();
            WarnedMessage warnedMessage;
            List<Integer> warnerIds = null;
            List<WarnedMessage> warnedMessageList = warnedMessageRepository.findByMessageIdAndChatId(warnedTelegramMessageId, chatId);
            if (warnedMessageList .size() == 0) {
                warnedMessage = new WarnedMessage();
                warnedMessage.setChatId(chatId);
                warnedMessage.setMessageId(warnedTelegramMessageId);
                warnerIds = new LinkedList<Integer>();
                warnedMessage.setWarnerIds(warnerIds);
                warnedMessage = warnedMessageRepository.save(warnedMessage);
                warnedMessage = (WarnedMessage)Hibernate.unproxy(warnedMessage);
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
                warnedMessageRepository.save(warnedMessage);
                if (warnLimitByGroup.get(chatId) == null) {
                    warnLimitByGroup.put(chatId, 25);
                }
                int chatMemberCount = 0;
                GetChatMemberCount getChatMemberCount = new GetChatMemberCount();
                getChatMemberCount.setChatId(chatId);
                try {
                    chatMemberCount = execute(getChatMemberCount);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

                if (warnerIds.size() >= (int) Math.ceil(((double) warnLimitByGroup.get(chatId) * chatMemberCount) / 100)) {
                    deleteMessage(repliedMessage);
                    String messageText = "این پیام که توسط شما به گروه زیر ارسال شده بود به دلیل اخطار های مکرر کاربران از گروه حذف شد:" + "\r\n"
                            + "نام گروه:" + "\r\n"
                            + message.getChat().getTitle() + "\r\n"
                            + "متن پیام:" + "\r\n"
                            + message.getReplyToMessage().getText();
                    AccessibleUser warnedUser = accessibleUserRepository.findByUserId(message.getFrom().getId());
                    if (warnedUser != null)
                        sendTextMessage(messageText, (long) warnedUser.getUserId(), null);
                }
            }
            deleteMessage(message);
        }
    }

    private void deleteMessage(Message message) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(String.valueOf(message.getChatId()));
        deleteMessage.setMessageId(message.getMessageId());
        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendTextMessage(String text, Long chatId, Integer repliedMessageId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(text);
        sendMessage.setChatId(chatId);
        if (repliedMessageId != null)
            sendMessage.setReplyToMessageId(repliedMessageId);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public String getBotUsername() {
        return System.getenv("username");
    }

    @Override
    public String getBotToken() {
//        System.out.println("tokening");
        return System.getenv("token");
    }

    public void onClosing() {
    }
}
