package test1;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class Main {
    public static void main(String[] args) {
        // Setting proxies
//        System.out.println("http has been set");
//        System.setProperty("http.proxyHost", "localhost");
//        System.setProperty("http.proxyPort", "8580");
//        System.out.println("https has been set");
//        System.setProperty("https.proxyHost", "localhost");
//        System.setProperty("https.proxyPort", "8580");
//        System.out.println("socks has been set");
//        System.setProperty("socksProxyHost", "localhost");
//        System.setProperty("socksProxyPort", "9150");

        // Initialize Api Context
        ApiContextInitializer.init();

        // Instantiate Telegram Bots API
        TelegramBotsApi botsApi = new TelegramBotsApi();
        System.out.println("bot created");
        // Register our bot
        try {
            botsApi.registerBot(new BotApi());
            System.out.println("bot registered");
        } catch (TelegramApiException e) {
            System.out.println("failed register");
            e.printStackTrace();
        }
    }
}
