package com.skillbox.cryptobot.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillbox.cryptobot.dto.SubscriptionDTO;
import com.skillbox.cryptobot.entities.Subscription;
import com.skillbox.cryptobot.exceptions.NoSuchSubscriptionPriceException;
import com.skillbox.cryptobot.repositories.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subRepo;
    private final ObjectMapper objectMapper;

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

    public Double getSubscriptionPrice(Long telegramUserId) throws NoSuchSubscriptionPriceException{
        Subscription sub = subRepo.findByTelegramId(telegramUserId);
        if (sub == null || sub.getPrice() == null) {
            throw new NoSuchSubscriptionPriceException("Для " +telegramUserId+ " не существует цены подписки.");
        }
        return sub.getPrice();
    }

    @Transactional
    public List<SubscriptionDTO> getSubscriptionsByLessPrice(Double price) {
        return subRepo.findByPriceLessThanEqual(price)
                .stream()
                .map(s -> objectMapper.convertValue(s, SubscriptionDTO.class))
                .collect(Collectors.toList());
    }

}
