package com.skillbox.cryptobot.utils;

import java.text.MessageFormat;

public class TextUtil {

    public final static String CURRENT_BITCOIN_PRICE = "Текущая цена биткоина {0} USD";
    public static String getCurrentBitcoinPrice(double price) {
        String readablePrice = toString(price);
        return MessageFormat.format(CURRENT_BITCOIN_PRICE, readablePrice);
    }

    public static String toString(double value) {
        return String.format("%.3f", value);
    }
}
