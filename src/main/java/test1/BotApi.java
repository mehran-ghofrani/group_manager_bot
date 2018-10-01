package test1;

import com.sun.org.apache.xml.internal.dtm.ref.ExtendedType;
import org.telegram.telegrambots.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.api.methods.groupadministration.GetChatMemberCount;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.ChatMember;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BotApi extends TelegramLongPollingBot {
    HashMap<String, WarnedMessageEntity> warnedMessagesByChatMessageId = new HashMap();
    HashMap<Long, Integer> warnLimitByGroup = new HashMap<Long, Integer>();

    public void onUpdateReceived(Update update) {
        System.out.println("update recived");
        if (update.hasMessage()) {
            //start logic
            Message message = update.getMessage();
            answerTest(message);
            setLimit(message);
            warn(message);
            checkLinks(message);
            checkForward(message);
        }
    }

    private void checkForward(Message message) {
        if (message.getForwardFromChat() != null) {
            deleteMessage(message);
        }
    }

    private void answerTest(Message message) {
        if (getTxtCap(message).equals("testbot")) {
            sendTextMessage("working", message.getChatId());
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
                                message.getChatId());
                    } else {
                        sendTextMessage("invalid argument", message.getChatId());
                    }
                } catch (Exception ex) {
                    sendTextMessage("illegal argument...\nsetlimit [0-100]",
                            message.getChatId());
                }
            }
        }
    }

    private void warn(Message message) {
        if (getTxtCap(message).equals("warn") && (message.getReplyToMessage() != null)) {
            Message warnedMessage = message.getReplyToMessage();
            long chatId = message.getChat().getId();
            Integer warnedMessageId = warnedMessage.getMessageId();
            int warnerId = message.getFrom().getId();
            String warnedMessageEntityId = String.valueOf(warnedMessage) + String.valueOf(warnedMessageId);
            WarnedMessageEntity warnedMessageEntity;
            List<Integer> warnerIds = null;
            if (warnedMessagesByChatMessageId.get(warnedMessageEntityId) == null) {
                warnedMessageEntity = new WarnedMessageEntity();
                warnedMessageEntity.setChatId(chatId);
                warnedMessageEntity.setMessageId(warnedMessageId);
                warnedMessagesByChatMessageId.put(warnedMessageEntityId, warnedMessageEntity);
                warnerIds = warnedMessageEntity.getWarnerIds();
            } else {
                warnedMessageEntity = warnedMessagesByChatMessageId.get(warnedMessageEntityId);
                warnerIds = warnedMessageEntity.getWarnerIds();
            }
            Boolean duplicatedWarnFlag = false;
            for (Integer WId : warnerIds) {
                if (WId.equals(warnerId)) {
                    duplicatedWarnFlag = true;
                    break;
                }
            }
            if (!duplicatedWarnFlag) {
                warnerIds.add(warnerId);
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
                    deleteMessage(warnedMessage);
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

    public void sendTextMessage(String text, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(text);
        sendMessage.setChatId(chatId);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public String getBotUsername() {
        return "PersianGroupGuard_Bot";
    }

    @Override
    public String getBotToken() {
//        System.out.println("tokening");
        return System.getenv("token");
    }

    public void onClosing() {
    }
}
