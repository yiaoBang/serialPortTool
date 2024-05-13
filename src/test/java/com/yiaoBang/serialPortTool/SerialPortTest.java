package com.yiaoBang.serialPortTool;

public class SerialPortTest {
    public static void main(String[] args) {
        try (SerialComm com3 = new SerialCommBuilder("COM3").build()) {
            com3.openSerialPort();
        } catch (SerialCommException e) {
            throw new RuntimeException(e);
        }
    }
}
