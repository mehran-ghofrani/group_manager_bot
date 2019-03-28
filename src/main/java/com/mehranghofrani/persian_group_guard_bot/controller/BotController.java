package com.mehranghofrani.persian_group_guard_bot.controller;

import com.mehranghofrani.persian_group_guard_bot.Main;
import com.mehranghofrani.persian_group_guard_bot.model.entity.FreeUser;
import com.mehranghofrani.persian_group_guard_bot.model.repository.AccessibleUserRepository;
import com.mehranghofrani.persian_group_guard_bot.model.repository.WarnedMessageRepository;
import com.mehranghofrani.persian_group_guard_bot.model.repository.WarnedUserRepository;
import com.mehranghofrani.persian_group_guard_bot.model.entity.AccessibleUser;
import com.mehranghofrani.persian_group_guard_bot.model.entity.WarnedMessage;
import com.mehranghofrani.persian_group_guard_bot.model.entity.WarnedUser;
import com.mehranghofrani.persian_group_guard_bot.service.AccessibleUserService;
import com.mehranghofrani.persian_group_guard_bot.service.FreeUserService;
import com.mehranghofrani.persian_group_guard_bot.service.WarnedMessageService;
import com.mehranghofrani.persian_group_guard_bot.service.WarnedUserService;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.api.methods.groupadministration.GetChatMemberCount;
import org.telegram.telegrambots.api.methods.groupadministration.KickChatMember;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.ChatMember;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class BotController extends TelegramLongPollingBot {

    @Resource
    WarnedMessageService warnedMessageService;

    @Resource
    WarnedUserService warnedUserService;

    @Resource
    AccessibleUserService accessibleUserService;

    @Resource
    FreeUserService freeUserService;



    public void onUpdateReceived(Update update) {
        Main.mainPrintStream.println("update recived");
        if (update.hasMessage()) {

            //start logic
            Message message = update.getMessage();
            try {
                answerPv(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                answerTest(message);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                warn(message);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                checkLinks(message);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                checkForward(message);
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                answerUnban(message);
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                answerUnbanCount(message);
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                checkJoinedMember(message);
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                excludeUser(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void excludeUser(Message message) {
        if (message.getText().contains("/exclude")) {
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
                if (freeUserService.find(message.getFrom().getId(), message.getChatId()) == null) {
                    FreeUser freeUser = new FreeUser();
                    freeUser.setChatId(message.getChatId());
                    freeUser.setUserId(message.getReplyToMessage().getFrom().getId());
                    freeUserService.save(freeUser);
                }
            }
        }
    }

    private void checkJoinedMember(Message message) throws TelegramApiException {
        if (message.getNewChatMembers() != null) {
            Set<Integer> badUsersIds = warnedUserService.getBadUsersIds();
            for (User user : message.getNewChatMembers()) {
                if (badUsersIds.contains(user.getId())) {
                    KickChatMember kickChatMember = new KickChatMember();
                    kickChatMember.setUserId(user.getId());
                    kickChatMember.setChatId(message.getChatId());
                    kickChatMember.setUntilDate(0);
                    execute(kickChatMember);
                }
                if (user.getBot()) {
                    KickChatMember kickAdder = new KickChatMember();
                    kickAdder.setUserId(message.getFrom().getId());
                    kickAdder.setChatId(message.getChatId());
                    kickAdder.setUntilDate(0);
                    execute(kickAdder);
                    KickChatMember kickBot = new KickChatMember();
                    kickBot.setUserId(user.getId());
                    kickBot.setChatId(message.getChatId());
                    kickBot.setUntilDate(0);
                    execute(kickBot);
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

    private void answerUnbanCount(Message message) {
        if (message.getText().contains("/unbanCount ") && message.getFrom().getId().equals(87654811)) {
            int userId = Integer.valueOf(getTxtCap(message).substring(12));
            WarnedUser warnedUser = warnedUserService.findByUserId(userId);
            sendTextMessage(warnedUser.getUnbanCount().toString()+warnedUser.getWarnsCount().toString(), 87654811L, null);
        }
    }

    private void answerUnban(Message message) {
        if (message.getText().contains("/unbanUser") && message.getFrom().getId().equals(87654811)) {
            int userId = Integer.valueOf(getTxtCap(message).substring(11));
            WarnedUser warnedUser = warnedUserService.findByUserId(userId);
            if (warnedUser != null) {
                warnedUser.setWarnsCount(0);
                warnedUser.setUnbanCount(warnedUser.getUnbanCount() + 1);
                warnedUserService.save(warnedUser);
            } else {
                sendTextMessage("user not found", 87654811L, null);
            }
        }
    }

    private void answerPv(Message message) {
        if (message.getChat().isUserChat())
            if (accessibleUserService.findByUserId(message.getFrom().getId()) == null) {
                AccessibleUser accessibleUser = new AccessibleUser();
                accessibleUser.setUserId(message.getFrom().getId());
                accessibleUserService.save(accessibleUser);
            }
    }

    private void checkForward(Message message) {
        if (message.getForwardFrom() != null && message.getForwardFromChat() != null && freeUserService.find(message.getFrom().getId(), message.getChatId()) != null) {
            deleteMessage(message);
        }
    }

    private void answerTest(Message message) {
        if (getTxtCap(message) != null && getTxtCap(message).equals("امتحان روبات")) {
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
        if (freeUserService.find(message.getFrom().getId(), message.getChatId()) != null)
            return;
        Pattern urlPattern = Pattern.compile(
                ".*[.].*[/]",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        Matcher matcher = urlPattern.matcher(getTxtCap(message));
        if (matcher.find())
            deleteMessage(message);
    }

//    private void setLimit(Message message) {
//        if (getTxtCap(message).startsWith("setlimit")) {
//            GetChatAdministrators getChatAdministrators = new GetChatAdministrators();
//            getChatAdministrators.setChatId(message.getChatId());
//            List<ChatMember> administrators = null;
//            try {
//                administrators = execute(getChatAdministrators);
//            } catch (TelegramApiException e) {
//                e.printStackTrace();
//            }
//            boolean nonAdminFlag = true;
//            for (ChatMember chatMember : administrators) {
//                if (chatMember.getUser().getId().equals(message.getFrom().getId())) {
//                    nonAdminFlag = false;
//                    break;
//                }
//            }
//            if (!nonAdminFlag) {
//                try {
//                    int limit = Integer.valueOf(getTxtCap(message).substring(9));
//                    if (limit >= 0 && limit <= 100) {
//                        warnLimitByGroup.put(message.getChatId(), limit);
//                        int chatMemberCount = 0;
//                        GetChatMemberCount getChatMemberCount = new GetChatMemberCount();
//                        getChatMemberCount.setChatId(message.getChatId());
//                        try {
//                            chatMemberCount = execute(getChatMemberCount);
//                        } catch (TelegramApiException e) {
//                            e.printStackTrace();
//                        }
//                        sendTextMessage("from now, " + (int) Math.ceil(((double) limit * chatMemberCount) / 100) + " warnings(more than " + limit + "% of members count) are needed to delete a warned message.",
//                                message.getChatId(), message.getMessageId());
//                    } else {
//                        sendTextMessage("invalid argument", message.getChatId(), message.getMessageId());
//                    }
//                } catch (Exception ex) {
//                    sendTextMessage("illegal argument...\nsetlimit [0-100]",
//                            message.getChatId(), message.getMessageId());
//                }
//            }
//        }
//    }

    private void warn(Message message) {
        if (getTxtCap(message) != null && getTxtCap(message).equals("warn") && (message.getReplyToMessage() != null)) {
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
                warnedMessageService.save(warnedMessage);
//                if (warnLimitByGroup.get(chatId) == null) {
//                    warnLimitByGroup.put(chatId, 25);
//                }
                int chatMemberCount = 0;
                GetChatMemberCount getChatMemberCount = new GetChatMemberCount();
                getChatMemberCount.setChatId(chatId);
                try {
                    chatMemberCount = execute(getChatMemberCount);
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
                    deleteMessage(repliedMessage);
                    String messageText = "این پیام که توسط شما به گروه زیر ارسال شده بود به دلیل اخطار های مکرر کاربران از گروه حذف شد:" + "\r\n"
                            + "نام گروه:" + "\r\n"
                            + message.getChat().getTitle() + "\r\n"
                            + "متن پیام:" + "\r\n"
                            + (message.getReplyToMessage().getText() != null ?  message.getReplyToMessage().getText() : "بدون متن") + "\r\n"
                            + "زمان پیام:" + "\r\n"
                            + new Date(message.getDate()).toString() + "\r\n"
                            + "در صورت تکرار اخطار ها از همه ی گروه هایی که این روبات در آن حضور دارد حذف میشوید.";
                    AccessibleUser accessibleUser = accessibleUserService.findByUserId(message.getReplyToMessage().getFrom().getId());
                    if (accessibleUser != null)
                        sendTextMessage(messageText, (long) accessibleUser.getUserId(), null);
                    warnedUser.setWarnsCount(warnedUser.getWarnsCount() + 1);
                    warnedUserService.save(warnedUser);
                }
                if (warnedUser.getWarnsCount() >= 1) {
                    KickChatMember kickChatMember = new KickChatMember();
                    kickChatMember.setChatId(message.getChatId());
                    kickChatMember.setUserId(message.getFrom().getId());
                    kickChatMember.setUntilDate(0);
                    try {
                        execute(kickChatMember);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
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
