package test1;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Main {
    static PrintStream mainPrintStream;
    static BotApi botApi;
    static Socket socket;
    public static void main(String[] args) {
        try {
            // Setting proxies
            if (System.getenv("connect9150").equals("false")) {
                System.out.println("socks has been set");
                System.setProperty("socksProxyHost", "localhost");
                System.setProperty("socksProxyPort", "9150");
            }
            //binding a port to avoid heroku bind timeout
            socket = new Socket();
            int portNum = Integer.valueOf(System.getenv("PORT"));
            System.out.println(portNum);
            InetSocketAddress inetSocketAddress = new InetSocketAddress("0.0.0.0", portNum);
            socket.bind(inetSocketAddress);
//            s.connect(new InetSocketAddress("google.com", 80));
            mainPrintStream = System.out;
            PrintStream myPrintStream = new TelegramPrintStream(new TelegramOutputStream());
            System.setOut(myPrintStream);
            System.setErr(myPrintStream);
            // Initialize Api Context
            ApiContextInitializer.init();

            // Instantiate Telegram Bots API
            final TelegramBotsApi botsApi = new TelegramBotsApi();
            System.out.println("bot created");
            // Register our bot
            try {
                botApi = new BotApi();
                botsApi.registerBot(botApi);
                botApi.sendTextMessage("bot registered", 87654811L);
                //infinite message to detect how much does the bot survive
                new Thread(new Runnable() {
                    public void run() {
                        while (true) {
                            try {
                                Thread.sleep(600000/40);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            botApi.sendTextMessage("I'm still alive...", 87654811L);
                        }
                    }
                }).start();
            } catch (TelegramApiException e) {
                System.out.println("failed register");
                e.printStackTrace();
            }
        }catch(Exception e){e.printStackTrace();}//to prevent stopping bot on the server
    }
}
