package com.example.tgbotexchangeratess.service;

import com.example.tgbotexchangeratess.config.BotConfig;
import com.example.tgbotexchangeratess.database.MessagesRepository;
import com.example.tgbotexchangeratess.model.CurrencyModel;
import lombok.AllArgsConstructor;
import org.json.JSONException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.text.ParseException;

@Component
@AllArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig botConfig;

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        MessagesRepository messagesRepository = new MessagesRepository();
        CurrencyModel currencyModel = new CurrencyModel();
        String currency = "";

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    messagesRepository.createMessages(messageText);
                    break;
                default:
                    try {
                        currency = CurrencyService.getCurrencyRate(messageText, currencyModel);
                        messagesRepository.createMessages(messageText);

                    } catch (IOException | ParseException | JSONException | ArrayIndexOutOfBoundsException e) {
                        sendMessage(chatId, "We have not found such a currency." + "\n" +
                                "Enter the alphabetic currency codes to be converted in the format:" + "\n" +
                                "\"amount, currency code from, currency code to\"." + "\n" +
                                "For example: \"3 usd kzt\".");
                        messagesRepository.createMessages(messageText);
                    }
                    sendMessage(chatId, currency);
            }
        }
    }

    private void startCommandReceived(Long chatId, String name) {
        String answer = "Hi, " + name + ", nice to meet you!" + "\n" +
                "Enter the alphabetic currency codes to be converted in the format:" + "\n" +
                "\"amount, currency code from, currency code to\"." + "\n" +
                "For example: \"3 usd kzt\".";
        sendMessage(chatId, answer);
    }

    private void sendMessage(Long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
        }
    }
}

