package com.repin.potd.bot;

import com.repin.potd.service.FileIdService;
import com.repin.potd.service.PotdService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.Collections;

import static com.repin.potd.exception.ExceptionUtils.unchecked;
import static java.time.format.DateTimeFormatter.ISO_DATE;
import static org.slf4j.LoggerFactory.getLogger;

@Component
public class PotdBot extends TelegramLongPollingBot {

    private static final Logger LOG = getLogger(PotdBot.class);

    private static final String BOT_NAME = "Picture of the day bot";
    private static final String PICTURE_OF_A_DAY = "🚀 Картинка дня от НАСА";
    private static final String START = "/start";

    @Value("${BOT_TOKEN}")
    private String token;
    private PotdService potdService;
    private FileIdService fileIdService;

    public PotdBot(PotdService potdService, FileIdService fileIdService) {
        LOG.info("Init PotdBot...");
        this.potdService = potdService;
        this.fileIdService = fileIdService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case START -> sendStartMenu(chatId);
                case PICTURE_OF_A_DAY -> sendPicture(chatId);
                default -> sendUnknownCommand(chatId);
            }
        }
    }

    private void sendUnknownCommand(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Неизвестная команда, воспользуйтесь пожалуйста меню.");
        unchecked(() -> execute(message));
    }

    private void sendPicture(long chatId) {
        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId);
        photo.setCaption("NASA picture of a day!");

        final var dateString = ISO_DATE.format(LocalDate.now());
        final var maybeFileId = fileIdService.findFileId(dateString);
        maybeFileId.ifPresentOrElse(photoFileId -> {
            LOG.info("Sending file_id from cache, file_id: {}", photoFileId);
                photo.setPhoto(new InputFile(photoFileId));
                unchecked(() -> execute(photo));
            },
            () -> {
                final var picture = unchecked(() -> potdService.getPicture());
                photo.setPhoto(new InputFile(new ByteArrayInputStream(picture.image().toByteArray()), "pictureOfADay.jpg"));
                photo.setCaption(picture.title());
                final var response = unchecked(() -> execute(photo));
                final var photoFileId = response.getPhoto().getFirst().getFileId();
                fileIdService.saveFileId(dateString, photoFileId);
                LOG.info("First request for today, fileId: {}", photoFileId);
            });
    }

    private void sendStartMenu(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Добро пожаловать! Нажмите кнопку ниже, чтобы продолжить:");

        setKeyboard(message);

        unchecked(() -> execute(message));
    }

    private static void setKeyboard(SendMessage message) {
        final var keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);
        KeyboardRow row = new KeyboardRow();
        row.add(PICTURE_OF_A_DAY);
        final var keyboard = Collections.singletonList(row);
        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return token;
    }
}