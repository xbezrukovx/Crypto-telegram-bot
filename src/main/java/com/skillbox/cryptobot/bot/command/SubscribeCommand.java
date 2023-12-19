package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.service.impl.CryptoCurrencyService;
import com.skillbox.cryptobot.service.impl.SubscriptionService;
import com.skillbox.cryptobot.utils.TextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.MessageFormat;

/**
 * Обработка команды подписки на курс валюты
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SubscribeCommand implements IBotCommand {
    private CryptoCurrencyService service;
    private final SubscriptionService subscriptionService;
    public final static String COMMAND_IDENTIFIER = "subscribe";
    public final static String COMMAND_DESCRIPTION = "Подписывает пользователя на стоимость биткоина";
    private final static String MESSAGE_ERROR = "Возникла ошибка, укажите корректную цену.";
    private final static String MESSAGE_OK = "Новая подписка создана на стоимость {0}";

    @Override
    public String getCommandIdentifier() {
        return COMMAND_IDENTIFIER;
    }

    @Override
    public String getDescription() {
        return COMMAND_DESCRIPTION;
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        Long currentChatId = message.getChatId();
        SendMessage answer = new SendMessage();
        answer.setChatId(currentChatId);
        String text = message.getText();
        Double price = null;
        if (text != null && text.startsWith("/subscribe ")){
            try {
                price = Double.parseDouble(text.substring(11));
                subscriptionService.createSubscription(currentChatId, price);
            } catch (NumberFormatException e) {
                log.info("Ошибка при считывании цены");
            }
        }

        if (price == null) {
            answer.setText(MESSAGE_ERROR);
        } else {
            answer.setText(MessageFormat.format(MESSAGE_OK, price));
        }

        try {
            absSender.execute(answer);
        } catch (TelegramApiException ex) {
            log.error("Ошибка возникла /subscribe методе", ex);
        }

        //subscriptionService.createSubscription(currentChatId, );


        try {
            String answerText = TextUtil.getCurrentBitcoinPrice(service.getBitcoinPrice());
            answer.setText(answerText);
            absSender.execute(answer);
        } catch (Exception e) {
            log.error("Ошибка возникла /subscribe методе", e);
        }
    }

    @Autowired
    public void setService(CryptoCurrencyService service) {
        this.service = service;
    }
}