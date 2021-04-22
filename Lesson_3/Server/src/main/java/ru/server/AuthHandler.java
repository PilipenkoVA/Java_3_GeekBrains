package ru.server;

public interface AuthHandler {
    void start();
    String getNickByLoginPass(String login, String pass);
    void stop();
}
