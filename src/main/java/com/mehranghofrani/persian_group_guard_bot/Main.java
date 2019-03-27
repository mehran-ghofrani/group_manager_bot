package com.mehranghofrani.persian_group_guard_bot;

import com.mehranghofrani.persian_group_guard_bot.controller.BotController;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

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
    BotController botController;
    static Socket socket;
    public static void main(String[] args) {
        //this line should be called early and from an static context in
        // situations which Spring instantiates the bot(don't know why)
        // Initialize Api Context
        ApiContextInitializer.init();
        SpringApplication.run(Main.class, args);

    }
    public void normalStart() {
        try {
            //checking if running in heroku servers or on my machine
            if (!System.getenv("connect9150").equals("false")) {
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
                socket.bind(inetSocketAddress);
//                            s.connect(new InetSocketAddress("google.com", 80));
            }
            mainPrintStream = System.out;
            PrintStream myPrintStream = new TelegramPrintStream(new TelegramOutputStream());
            System.setOut(myPrintStream);
            System.setErr(myPrintStream);


            // Instantiate Telegram Bots API
            final TelegramBotsApi botsApi = new TelegramBotsApi();
            System.out.println("bot created");
            // Register our bot
            try {
                botsApi.registerBot(botController);
            } catch (TelegramApiException e) {
                System.out.println("failed register");
                e.printStackTrace();
                throw new IOException();
            }
            botController.sendTextMessage("bot registered", 87654811L, null);
            //infinite message to detect how much does the bot survive
            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(600000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        botController.sendTextMessage("I'm still alive...", 87654811L, null);
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }//to prevent stopping bot on the server
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        normalStart();
    }
}
