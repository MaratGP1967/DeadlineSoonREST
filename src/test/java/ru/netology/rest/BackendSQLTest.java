package ru.netology.rest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import ru.netology.help.DBHelper;
import ru.netology.help.DataHelper;

import static ru.netology.help.APIHelper.*;

public class BackendSQLTest {

    @AfterAll
    static void cleanUp() {
        DBHelper.cleanUpDB();
    }

    @Test
    void shouldWork() {
        var userLog = DataHelper.getAuthInfo();
        shouldSetLogPass(userLog);
        var userCode = DataHelper.getToAuthCode();
        shouldSetVerCode(userCode);
        var getCardBalance = DataHelper.checkBalance(10000, 10000);
        shouldGetBalance(getCardBalance.getCard_2(), getCardBalance.getCard_1());
        var fromTo = DataHelper.getToChangeBalance("5000");
        shouldChangeBalance(fromTo);
        getCardBalance = DataHelper.checkBalance(5000, 15000);
        shouldGetBalance(getCardBalance.getCard_2(), getCardBalance.getCard_1());
        fromTo = DataHelper.getToChangeBalance("-5000");
        shouldChangeBalance(fromTo);
        getCardBalance = DataHelper.checkBalance(10000, 10000);
        shouldGetBalance(getCardBalance.getCard_2(), getCardBalance.getCard_1());
    }
}
