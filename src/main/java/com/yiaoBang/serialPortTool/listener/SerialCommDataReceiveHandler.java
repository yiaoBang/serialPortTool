package com.yiaoBang.serialPortTool.listener;



public interface SerialCommDataReceiveHandler {
    void dataReceive(byte[] data);
    void serialPortDisconnected();
}
