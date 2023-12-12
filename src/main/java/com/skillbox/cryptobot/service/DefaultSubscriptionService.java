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
        subRepo.save(newSub);
        log.info(telegramUserId + " был добавлен в базу подписок.");
    }

}
