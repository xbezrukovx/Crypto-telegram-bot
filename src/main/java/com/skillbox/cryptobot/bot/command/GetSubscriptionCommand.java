package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.exceptions.NoSuchSubscriptionPriceException;
import com.skillbox.cryptobot.service.impl.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@Slf4j
@RequiredArgsConstructor
public class GetSubscriptionCommand implements IBotCommand {
    private final SubscriptionService subscriptionService;

    @Override
    public String getCommandIdentifier() {
        return "get_subscription";
    }

    @Override
    public String getDescription() {
        return "Возвращает текущую подписку";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        Long currentChatId = message.getChatId();
        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());
        try {
            Double price = subscriptionService.getSubscriptionPrice(currentChatId);
            answer.setText("Вы подписаны на стоимость биткоина " + price + " USD");
        } catch (NoSuchSubscriptionPriceException e) {
            answer.setText("Активные подписки отсутствуют");
            log.info("Для пользователя " + currentChatId + " нет текущей цены подписки");
        }

        try {
            absSender.execute(answer);
        } catch (TelegramApiException e) {
            log.error("Ошибка возникла /get_subscription методе", e);
        }
    }
}