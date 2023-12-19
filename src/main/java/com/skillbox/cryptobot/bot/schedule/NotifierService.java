package com.skillbox.cryptobot.bot.schedule;

import com.skillbox.cryptobot.service.impl.CryptoCurrencyService;
import com.skillbox.cryptobot.service.impl.SubscriptionService;
import com.skillbox.cryptobot.utils.TextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The service sends notifications to subscribers whose
 * desired bitcoin price is less than or equal
 * to the current value via Telegram that the desired
 * price has been reached, and they can purchase bitcoins.
 *
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NotifierService {
    private final SubscriptionService subscriptionService;
    private final CryptoCurrencyService currencyService;
    private final Map<Long, LocalDateTime> subscriptionNotify = new ConcurrentHashMap<>();
    private final TelegramLongPollingCommandBot bot;
    private final static String MESSAGE_NOTIFY = "Пора покупать, стоимость биткоина: {0} USD";
    @Value("${application.notifications.user-delay}")
    private Duration userdelay;

    /**
     * The method runs each time based on the specified cron parameter.
     * A notification is sent via Telegram to all subscribers whose
     * price is lower than or equal to the current Bitcoin price.
     * If the user has already received a notification within
     * the duration time parameter, then it is skipped.
     *
     */
    @Scheduled(cron = "${application.notifications.binance-check}")
    public void subscriberNotifier() {
        for (Map.Entry<Long, LocalDateTime> entry : subscriptionNotify.entrySet()) {
            Long telegramId = entry.getKey();
            LocalDateTime lastNotification = entry.getValue();
            Duration diff = Duration.between(lastNotification, LocalDateTime.now());
            if (diff.compareTo(userdelay) > 0) {
                subscriptionNotify.remove(telegramId);
            }
        }
        try {
            Double currentPrice = currencyService.getBitcoinPrice();
            subscriptionService
                    .getSubscriptionsByLessPrice(currentPrice)
                    .stream()
                    .filter(s -> s.getPrice() <= currentPrice)
                    .forEach(s -> sendNotification(s.getTelegramId(), currentPrice));
        } catch (IOException e) {
            log.error("Возникла ошибка нофикатора.", e);
        }
    }

    private void sendNotification(Long telegramId, Double price) {
        if (subscriptionNotify.containsKey(telegramId)) {
            log.info(telegramId + " уже получал уведомление, пропускаем.");
            return;
        }

        try {
            SendMessage answer = new SendMessage();
            answer.setChatId(telegramId);
            String text = MessageFormat.format(MESSAGE_NOTIFY, TextUtil.toString(price));
            answer.setText(text);
            bot.execute(answer);
            subscriptionNotify.put(telegramId, LocalDateTime.now());
            log.info(telegramId + " уведомлен по подписке.");
        } catch (TelegramApiException e) {
            log.error("Возникла ошибка при отправке уведомления.", e);
        }
    }
}
