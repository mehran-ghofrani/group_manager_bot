package com.mehranghofrani.persian_group_guard_bot.controller.telegram;

import com.mehranghofrani.persian_group_guard_bot.controller.bot.BaseBotController;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.Resource;
import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private Update currentProcessingUpdate;

    @Resource
    List<BaseBotController> botControllers;

    @Override
    public void onUpdateReceived(Update update) {
        currentProcessingUpdate = update;
        for (BaseBotController controller : botControllers) {
            controller.onUpdate(update);
        }
    }

    @Override
    public String getBotUsername() {
        return System.getenv("username");
    }

    @Override
    public String getBotToken() {
        return System.getenv("token");
    }


    public String getTxtCap(Message message) {
        if (message.getText() != null) {
            return message.getText();
        }
        else {
            return message.getCaption();
        }
    }



    public void deleteMessage(Message message) {
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

    public void replyCurrentMessage(String messageText) {
        Message messageForReply = currentProcessingUpdate.getMessage();
        sendTextMessage(messageText, messageForReply.getChatId(), messageForReply.getMessageId());
    }

    public Chat findChat(Long chatId) {
        GetChat getChat = new GetChat();
        getChat.setChatId(chatId);
        try {
            return execute(getChat);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return null;
        }
    }
}
