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

    @Override
    public String getCommandIdentifier() {
        return "unsubscribe";
    }

    @Override
    public String getDescription() {
        return "Отменяет подписку пользователя";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        Long currentChatId = message.getChatId();
        SendMessage answer = new SendMessage();
        answer.setChatId(currentChatId);

        subscriptionService.createSubscription(currentChatId, null);

        try {
            answer.setText("Подписка отменена");
            absSender.execute(answer);
        } catch (TelegramApiException ex) {
            log.error("Ошибка возникла /unsubscribe методе", ex);
        }
    }
}