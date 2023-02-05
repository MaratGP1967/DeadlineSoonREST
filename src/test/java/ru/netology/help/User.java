package ru.netology.help;

public class User {
    private String login;
    private String code;

    public User(String login, String code) {
        this.login = login;
        this.code = code;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
