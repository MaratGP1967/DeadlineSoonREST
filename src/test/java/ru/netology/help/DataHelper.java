package ru.netology.help;

import lombok.Value;

public class DataHelper {
    private DataHelper() {
    }

    @Value
    public static class AuthInfo {
        private String login;
        private String password;
    }

    public static AuthInfo getAuthInfo() {
        return new AuthInfo("vasya", "qwerty123");
    }

    @Value
    public static class AuthCode {
        private String login;
        private String code;
    }

    public static AuthCode getToAuthCode() {
        return new AuthCode(getAuthInfo().login, DBHelper.getAuthCode(getAuthInfo().login));
    }

    @Value
    public static class CardNumber {
        private String from;
        private String to;
        private String amount;
    }

    public static CardNumber getToChangeBalance(String sum) {
        return new CardNumber("5559 0000 0000 0002", "5559 0000 0000 0001", sum);
    }

    @Value
    public static class CardBalance {
        private int card_2;
        private int card_1;
    }

    public static CardBalance checkBalance(int card_2, int card_1) {
        return new CardBalance(card_2, card_1);
    }
}
