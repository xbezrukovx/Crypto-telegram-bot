package com.skillbox.cryptobot.exceptions;

public class NoSuchSubscriptionPriceException extends RuntimeException{
    public NoSuchSubscriptionPriceException(String message) {
        super(message);
    }
}
