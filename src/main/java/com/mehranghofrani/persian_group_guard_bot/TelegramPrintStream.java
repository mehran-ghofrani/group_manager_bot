package com.mehranghofrani.persian_group_guard_bot;

import com.mehranghofrani.persian_group_guard_bot.controller.BotController;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class TelegramPrintStream extends PrintStream {
    public TelegramPrintStream(OutputStream out) {
        super(out);
    }
}
class TelegramOutputStream extends OutputStream {
    String currentLine;
    @Resource
    BotController botController;
    public void write(int b) throws IOException {
        if (currentLine == null)
            currentLine = "";
        String outputString = new String(new char[]{(char)b});
        if (botController != null) {
            currentLine = currentLine + outputString;
            if (currentLine.contains("\r")) {
             botController.sendTextMessage(currentLine, 87654811L, null);
                currentLine = "";
             }
        }
        else {
            Main.mainPrintStream.print(outputString);
        }

    }
}

