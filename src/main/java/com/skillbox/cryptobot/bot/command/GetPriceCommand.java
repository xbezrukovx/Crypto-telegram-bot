package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.service.impl.CryptoCurrencyService;
import com.skillbox.cryptobot.utils.TextUtil;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

/**
 * Обработка команды получения текущей стоимости валюты
 */
@Service
@Slf4j
@NoArgsConstructor
public class GetPriceCommand implements IBotCommand {

    private CryptoCurrencyService service;
    public final static String COMMAND_IDENTIFIER = "get_price";
    public final static String COMMAND_DESCRIPTION = "Возвращает цену биткоина в USD";

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
        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());
        try {
            String answerText = TextUtil.getCurrentBitcoinPrice(service.getBitcoinPrice());
            answer.setText(answerText);
            absSender.execute(answer);
        } catch (Exception e) {
            log.error("Ошибка возникла /get_price методе", e);
        }
    }

    @Autowired
    public void setService(CryptoCurrencyService service) {
        this.service = service;
    }
}