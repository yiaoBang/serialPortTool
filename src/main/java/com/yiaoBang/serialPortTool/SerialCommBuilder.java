package com.yiaoBang.serialPortTool;

import com.fazecast.jSerialComm.SerialPort;
import com.yiaoBang.serialPortTool.listener.SerialCommDataReceiveHandler;

import java.nio.charset.StandardCharsets;

public class SerialCommBuilder {
    private String serialPortName;
    private int baudRate = 9600;
    private int dataBits = 8;
    private int stopBits = SerialPort.ONE_STOP_BIT;
    private int parity = SerialPort.NO_PARITY;
    private int flowControl = SerialPort.FLOW_CONTROL_DISABLED;
    private int dataReceiveCheckTimeInterval = 200;
    private int dataReceiveTimeOut = 5;
    private int packetSize = 0;
    private byte[] delimiter = new byte[0];
    private SerialCommDataReceiveHandler serialCommDataReceiveHandler = null;

    public SerialCommBuilder(String serialPortName) {
        this.serialPortName = serialPortName;
    }

    public void serialPortName(String serialPortName) {
        this.serialPortName = serialPortName;
    }

    public void baudRate(int baudRate) {
        this.baudRate = baudRate;
    }

    public void dataBits(int dataBits) {
        this.dataBits = dataBits;
    }

    public void stopBits(int stopBits) {
        this.stopBits = stopBits;
    }

    public void parity(int parity) {
        this.parity = parity;
    }

    public void flowControl(int flowControl) {
        this.flowControl = flowControl;
    }

    public void dataReceiveCheckTimeInterval(int dataReceiveCheckTimeInterval) {
        this.dataReceiveCheckTimeInterval = dataReceiveCheckTimeInterval;
    }

    public void dataReceiveTimeOut(int dataReceiveTimeOut) {
        this.dataReceiveTimeOut = dataReceiveTimeOut;
    }

    public void packetSize(int packetSize) {
        this.packetSize = packetSize;
    }

    public void delimiter(byte[] delimiter) {
        this.delimiter = delimiter;
    }

    public void delimiter(String delimiter) {
        this.delimiter = delimiter.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 使用自定义的串行通信数据接收处理程序
     *
     * @param serialCommDataReceiveHandler 串行通信数据接收处理程序
     */
    public void serialCommDataReceiveHandler(SerialCommDataReceiveHandler serialCommDataReceiveHandler) {
        this.serialCommDataReceiveHandler = serialCommDataReceiveHandler;
    }

    /**
     * 使用分隔符构建
     *
     * @return {@code SerialComm }
     */
    public SerialComm buildWithDelimiter() {
        return new SerialComm(serialPortName, baudRate, dataBits, stopBits, parity, flowControl,
                dataReceiveCheckTimeInterval, dataReceiveTimeOut, delimiter, serialCommDataReceiveHandler);
    }

    /**
     * 使用数据包大小构建
     *
     * @return {@code SerialComm }
     */
    public SerialComm buildWithPacketSize() {
        return new SerialComm(serialPortName, baudRate, dataBits, stopBits, parity, flowControl,
                dataReceiveCheckTimeInterval, dataReceiveTimeOut, packetSize, serialCommDataReceiveHandler);
    }

    public SerialComm build() {
        return delimiter.length > 0 ? buildWithDelimiter() : buildWithPacketSize();
    }
}
