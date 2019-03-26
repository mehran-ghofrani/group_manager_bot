package com.mehranghofrani.persian_group_guard_bot;

import org.springframework.beans.factory.annotation.Autowired;

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
    BotApi botApi;
    public void write(int b) throws IOException {
        if (currentLine == null)
            currentLine = "";
        String outputString = new String(new char[]{(char)b});
        if (botApi != null) {
            currentLine = currentLine + outputString;
            if (currentLine.contains("\r")) {
             botApi.sendTextMessage(currentLine, 87654811L, null);
                currentLine = "";
            }
        }
        else {
            Main.mainPrintStream.print(outputString);
        }

    }
}

