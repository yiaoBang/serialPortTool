package com.yiaoBang.serialPortTool.listener;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListenerWithExceptions;

public class SerialCommDataListenerWithDelimiter implements SerialPortMessageListenerWithExceptions {
    private final byte[] delimiter;
    private final SerialCommDataReceiveHandler handler;

    public SerialCommDataListenerWithDelimiter(byte[] delimiter, SerialCommDataReceiveHandler handler) {
        this.delimiter = delimiter;
        this.handler = handler;
    }

    @Override
    public void catchException(Exception e) {
        throw new RuntimeException(e);
    }

    /**
     * 获取消息分隔符
     * 如果返回的是空数组将视作不启用
     *
     * @return {@code byte[] }
     */
    @Override
    public byte[] getMessageDelimiter() {
        return this.delimiter;
    }

    @Override
    public boolean delimiterIndicatesEndOfMessage() {
        //true表示结束符,false表示开始符号
        return true;
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_RECEIVED | SerialPort.LISTENING_EVENT_PORT_DISCONNECTED;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_RECEIVED) {
            this.handler.dataReceive(event.getReceivedData());
        } else {
            this.handler.serialPortDisconnected();
        }
    }
}
