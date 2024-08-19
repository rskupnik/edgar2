package com.github.rskupnik.edgar;

public interface UserMessageSender {
    void send(String message);
    void send(byte[] data);
}
