package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.service.impl.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Обработка команды отмены подписки на курс валюты
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UnsubscribeCommand implements IBotCommand {
    private final SubscriptionService subscriptionService;
    public final static String COMMAND_IDENTIFIER = "subscribe";
    public final static String COMMAND_DESCRIPTION = "Подписывает пользователя на стоимость биткоина";
    private final static String MESSAGE_OK = "Подписка отменена";

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

        subscriptionService.createSubscription(currentChatId, null);

        try {
            answer.setText(MESSAGE_OK);
            absSender.execute(answer);
        } catch (TelegramApiException ex) {
            log.error("Ошибка возникла /unsubscribe методе", ex);
        }
    }
}