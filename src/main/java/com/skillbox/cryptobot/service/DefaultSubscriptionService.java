package com.skillbox.cryptobot.service;

import com.skillbox.cryptobot.entities.Subscription;
import com.skillbox.cryptobot.repositories.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultSubscriptionService {
    private final SubscriptionRepository subRepo;

    @Transactional
    public void createNewUser(Long telegramUserId) {
        boolean canCreate = subRepo.findByTelegramId(telegramUserId) == null;
        if (!canCreate) {
            return;
        }
        Subscription newSub = new Subscription();
        newSub.setTelegramId(telegramUserId);
        log.info(telegramUserId + " был добавлен в базу подписок.");
    }

    @Transactional
    public void createSubscription(Long telegramUserId, Double price) {
        Subscription sub = subRepo.findByTelegramId(telegramUserId);
        if (sub == null) {
            sub = new Subscription();
            sub.setTelegramId(telegramUserId);
            log.info("Пользователь " + telegramUserId + " не был зарегистрирован. Добавляем пользователя в базу.");
        }
        sub.setPrice(price);
        subRepo.save(sub);
        log.info("Для пользователя: " + telegramUserId + " была установлена подписка на " + price);
    }

}
