package com.skillbox.cryptobot.repositories;

import com.skillbox.cryptobot.entities.Subscription;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubscriptionRepository extends CrudRepository<Subscription, UUID> {
    Subscription findByTelegramId(Long TelegramId);
}
