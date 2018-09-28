package test1;

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
        if (update.hasMessage() && update.getMessage().hasText()) { //start logic
            Message message = update.getMessage();
            String messageText = message.getText();
            if (messageText.startsWith("setlimit")) {
                setLimit(message);
            }
            if (messageText.equals("warn")) {
                warn(message);
            }
            checkLinks(message);
//            sendTextMessage("test47", message.getChatId());
        }
    }

    private void checkLinks(Message message) {
        Pattern urlPattern = Pattern.compile(
                "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                        + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                        + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        Matcher matcher = urlPattern.matcher(message.getText());
        if (matcher.find())
            deleteMessage(message);
    }

    private void setLimit(Message message) {

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
                int limit = Integer.valueOf(message.getText().substring(9));
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
                    sendTextMessage("from now, " + (int)Math.ceil(((double)limit * chatMemberCount) / 100) + " warnings(more than " + limit + "% of members count) are needed to delete a warned message.",
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

    private void warn(Message message) {
        Message warnedMessage = message.getReplyToMessage();
        long chatId = message.getChat().getId();
        Integer warnedMessageId = warnedMessage.getMessageId();
        int warnerId = message.getFrom().getId();
        String warnedMessageEntityId = String.valueOf(warnedMessage) + String.valueOf(warnedMessageId);
        WarnedMessageEntity warnedMessageEntity;
        List<Integer> warnerIds = null;
        if(warnedMessagesByChatMessageId.get(warnedMessageEntityId) == null) {
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

            if (warnerIds.size() >= (int)Math.ceil(((double)warnLimitByGroup.get(chatId) * chatMemberCount) / 100)) {
                deleteMessage(warnedMessage);
            }
        }
        deleteMessage(message);
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

    private void sendTextMessage(String text, Long chatId) {
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
        return "testPHPmghbot";
    }

    @Override
    public String getBotToken() {
        return Secrets.getToken();
    }

    public void onClosing() {
    }
}
