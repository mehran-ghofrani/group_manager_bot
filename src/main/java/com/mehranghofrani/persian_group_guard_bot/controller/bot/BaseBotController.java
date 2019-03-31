package com.mehranghofrani.persian_group_guard_bot.controller.bot;

import com.mehranghofrani.persian_group_guard_bot.Main;
import com.mehranghofrani.persian_group_guard_bot.controller.telegram.TelegramBot;
import com.mehranghofrani.persian_group_guard_bot.service.OpenChatService;
import com.mehranghofrani.persian_group_guard_bot.service.FreeUserService;
import com.mehranghofrani.persian_group_guard_bot.service.WarnedMessageService;
import com.mehranghofrani.persian_group_guard_bot.service.WarnedUserService;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;

public abstract class BaseBotController {

    @Resource
    WarnedMessageService warnedMessageService;

    @Resource
    WarnedUserService warnedUserService;

    @Resource
    OpenChatService openChatService;

    @Resource
    FreeUserService freeUserService;

    @Resource
    TelegramBot telegramBot;


    public void onUpdate(Update update) {
        Main.mainPrintStream.println("update recived");
        if (isHandlable(update))
            handle(update);
    }

    protected abstract boolean isHandlable(Update update);

    protected abstract void handle(Update update);






}
