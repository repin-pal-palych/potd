package com.repin.potd.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class BotInitializer {

    private final PotdBot potdBot;

    public BotInitializer(PotdBot potdBot) throws TelegramApiException {
        this.potdBot = potdBot;

        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(potdBot);
    }
}
