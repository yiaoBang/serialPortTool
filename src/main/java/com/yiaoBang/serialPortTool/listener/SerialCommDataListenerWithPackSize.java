package com.yiaoBang.serialPortTool.listener;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortPacketListener;

public class SerialCommDataListenerWithPackSize implements SerialPortPacketListener {
    private final int packetSize;
    private final SerialCommDataReceiveHandler handler;

    public SerialCommDataListenerWithPackSize(int packetSize, SerialCommDataReceiveHandler handler) {
        this.packetSize = packetSize;
        this.handler = handler;
    }

    @Override
    public int getPacketSize() {
        return this.packetSize;
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
