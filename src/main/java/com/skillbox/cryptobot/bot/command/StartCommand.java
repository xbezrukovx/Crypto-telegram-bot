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
 * Обработка команды начала работы с ботом
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class StartCommand implements IBotCommand {
    private final SubscriptionService subscriptionService;
    public final static String COMMAND_IDENTIFIER = "start";
    public final static String COMMAND_DESCRIPTION = "Запускает бота";
    private final static String MESSAGE_WELCOME =
            "Привет! Данный бот помогает отслеживать стоимость биткоина.\n" +
            "Поддерживаемые команды:\n" +
            " /get_price - получить стоимость биткоина\n" +
            " /get_subscription - Выводит информацию о подписке на цену\n" +
            " /subscribe {стоимость} - подписывает на стоимость биткоина\n" +
            " /unsubscribe - удаляет активную подписку";

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
        log.info(currentChatId + " запустил чат с ботом.");
        SendMessage answer = new SendMessage();
        answer.setChatId(currentChatId);
        subscriptionService.createNewUser(currentChatId);

        answer.setText(MESSAGE_WELCOME);
        try {
            absSender.execute(answer);
        } catch (TelegramApiException e) {
            log.error("Ошибка возникла /get_price методе", e);
        }
    }
}