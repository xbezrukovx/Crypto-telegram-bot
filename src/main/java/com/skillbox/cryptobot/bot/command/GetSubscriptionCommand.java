package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.exceptions.NoSuchSubscriptionPriceException;
import com.skillbox.cryptobot.service.impl.SubscriptionService;
import com.skillbox.cryptobot.utils.TextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.MessageFormat;

@Service
@Slf4j
@RequiredArgsConstructor
public class GetSubscriptionCommand implements IBotCommand {
    private final SubscriptionService subscriptionService;
    public final static String COMMAND_IDENTIFIER = "get_subscription";
    public final static String COMMAND_DESCRIPTION = "Возвращает текущую подписку";
    private final static String MESSAGE_OK = "Вы подписаны на стоимость биткоина {0} USD";
    private final static String MESSAGE_NO_SUBS = "Активные подписки отсутствуют";

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
        answer.setChatId(message.getChatId());
        try {
            Double price = subscriptionService.getSubscriptionPrice(currentChatId);
            String text = MessageFormat.format(MESSAGE_OK, TextUtil.toString(price));
            answer.setText(text);
        } catch (NoSuchSubscriptionPriceException e) {
            answer.setText(MESSAGE_NO_SUBS);
            log.info("Для пользователя " + currentChatId + " нет текущей цены подписки");
        }

        try {
            absSender.execute(answer);
        } catch (TelegramApiException e) {
            log.error("Ошибка возникла /get_subscription методе", e);
        }
    }
}