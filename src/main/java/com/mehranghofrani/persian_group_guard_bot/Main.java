package com.mehranghofrani.persian_group_guard_bot;

import com.mehranghofrani.persian_group_guard_bot.controller.telegram.TelegramBot;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;

@SpringBootApplication
@EnableAutoConfiguration
@EnableJpaRepositories("com.mehranghofrani.persian_group_guard_bot.model.repository")
public class Main implements ApplicationRunner {
    public static PrintStream mainPrintStream;
    @Resource
    TelegramBot telegramBot;

    static Socket socket;
    public static void main(String[] args) {
        //this line should be called early and from an static context in
        // situations which Spring instantiates the bot(don't know why)
        // Initialize Api Context
        ApiContextInitializer.init();
        SpringApplication.run(Main.class, args);

    }
    public void normalStart() {

        //checking if running in heroku servers or on my machine
        if (!System.getenv("ON_LOCAL_MACHINE").equals("false")) {
            // Setting proxies
//                System.out.println("https has been set");
//                System.setProperty("http.proxyHost", "localhost");
//                System.setProperty("http.proxyPort", "8580");
//                System.setProperty("https.proxyHost", "localhost");
//                System.setProperty("https.proxyPort", "8580");
            System.out.println("socks has been set");
            System.setProperty("socksProxyHost", "localhost");
            System.setProperty("socksProxyPort", "9150");
        } else {
//                binding a port to avoid heroku bind timeout, sometimes comment on local to make app faster
            socket = new Socket();
            int portNum = Integer.valueOf(System.getenv("PORT"));
            System.out.println(portNum);
            InetSocketAddress inetSocketAddress = new InetSocketAddress("0.0.0.0", portNum);
            try {
                socket.bind(inetSocketAddress);
            } catch (IOException e) {
                System.out.println("main: cannot bind the socket");
                e.printStackTrace();
            }
//                            s.connect(new InetSocketAddress("google.com", 80));
        }
        mainPrintStream = System.out;
        PrintStream myPrintStream = new TelegramPrintStream(new TelegramOutputStream());
        System.setOut(myPrintStream);
        System.setErr(myPrintStream);


        // Instantiate Telegram Bots API
        final TelegramBotsApi botsApi = new TelegramBotsApi();
        System.out.println("bot api created");
        // Register controllers
            try {
                botsApi.registerBot(telegramBot);
//                updateReceiver.sendTextMessage("bot registered", 87654811L, null);
            } catch (TelegramApiException e) {
                System.out.println("failed register this controller");
                e.printStackTrace();
            }

        //infinite message to detect how much does the bot survive
//            new Thread(new Runnable() {
//                public void run() {
//                    while (true) {
//                        try {
//                            Thread.sleep(600000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        BaseBotController.sendTextMessage("I'm still alive...", 87654811L, null);
//                    }
//                }
//            }).start();


    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        normalStart();
    }
}
