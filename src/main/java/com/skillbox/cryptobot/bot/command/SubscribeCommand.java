package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.service.CryptoCurrencyService;
import com.skillbox.cryptobot.service.DefaultSubscriptionService;
import com.skillbox.cryptobot.utils.TextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Обработка команды подписки на курс валюты
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SubscribeCommand implements IBotCommand {
    private final CryptoCurrencyService service;
    private final DefaultSubscriptionService subscriptionService;

    @Override
    public String getCommandIdentifier() {
        return "subscribe";
    }

    @Override
    public String getDescription() {
        return "Подписывает пользователя на стоимость биткоина";
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
            answer.setText("Возникла ошибка, укажите корректную цену.");
        } else {
            answer.setText("Новая подписка создана на стоимость " + price);
        }

        try {
            absSender.execute(answer);
        } catch (TelegramApiException ex) {
            log.error("Ошибка возникла /subscribe методе", ex);
        }

        //subscriptionService.createSubscription(currentChatId, );


        try {
            answer.setText("Текущая цена биткоина " + TextUtil.toString(service.getBitcoinPrice()) + " USD");
            absSender.execute(answer);
        } catch (Exception e) {
            log.error("Ошибка возникла /subscribe методе", e);
        }
    }
}