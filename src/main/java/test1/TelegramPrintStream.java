package test1;

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
    public void write(int b) throws IOException {
        if (currentLine == null)
            currentLine = "";
        String outputString = new String(new char[]{(char)b});
        if (Main.botApi != null) {
            currentLine = currentLine + outputString;
            if (currentLine.contains("\r")) {
                Main.botApi.sendTextMessage(currentLine, 87654811L);
                currentLine = "";
            }
        }
        else {
            Main.mainPrintStream.print(outputString);
        }

    }
}

