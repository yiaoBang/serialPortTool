package com.yiaoBang.serialPortTool;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.yiaoBang.serialPortTool.listener.SerialCommDataListenerWithDelimiter;
import com.yiaoBang.serialPortTool.listener.SerialCommDataListenerWithPackSize;
import com.yiaoBang.serialPortTool.listener.SerialCommDataReceiveHandler;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("all")
public class SerialComm implements SerialCommDataReceiveHandler, AutoCloseable {

    /**
     * 命令缓冲区
     */
    private static final Map<String, byte[]> commandBuffer = new HashMap<>();
    /**
     * 串行端口
     */
    private SerialPort serialPort;
    /**
     * 串行端口名
     */
    private final String serialPortName;
    /**
     * 波特率
     */
    private final int baudRate;
    /**
     * 数据位
     */
    private final int dataBits;
    /**
     * 停止位
     */
    private final int stopBits;
    /**
     * 奇偶校验
     */
    private final int parity;
    /**
     * 流量控制
     */
    private final int flowControl;
    /**
     * 串口数据监听器
     */
    private SerialPortDataListener serialPortDataListener;
    /**
     * 接收到的串口数据
     */
    private volatile byte[] receivedSerialPortData = null;
    /**
     * 检查串口数据是否已经返回的时间间隔
     */
    private final int dataReceiveCheckTimeInterval;
    /**
     * 数据接收超时
     */
    private final int dataReceiveTimeOut;
    /**
     * 数据包大小(用于判断接收到的串口数据是否完整)
     */
    private volatile int packetSize = 0;
    /**
     * 数据分隔符(用于判断接收到的串口数据是否完整)
     */
    private volatile byte[] delimiter = new byte[0];
    /**
     * 串行通信数据接收处理程序(用于自定义处理)
     */
    private SerialCommDataReceiveHandler serialCommDataReceiveHandler;

    /**
     * 串行通信
     *
     * @param serialPortName               串口名称
     * @param baudRate                     波特率
     * @param dataBits                     数据位
     * @param stopBits                     停止位
     * @param parity                       平价
     * @param flowControl                  流控制
     * @param dataReceiveCheckTimeInterval 数据接收检查时间间隔
     * @param dataReceiveTimeOut           数据接收超时
     * @param packetSize                   数据包大小
     * @param serialCommDataReceiveHandler 串行通信数据接收处理程序
     */
    public SerialComm(String serialPortName,
                      int baudRate,
                      int dataBits,
                      int stopBits,
                      int parity,
                      int flowControl,
                      int dataReceiveCheckTimeInterval,
                      int dataReceiveTimeOut,
                      int packetSize,
                      SerialCommDataReceiveHandler serialCommDataReceiveHandler) {
        this.serialPortName = serialPortName;
        this.baudRate = baudRate;
        this.dataBits = dataBits;
        this.stopBits = stopBits;
        this.parity = parity;
        this.flowControl = flowControl;
        this.dataReceiveCheckTimeInterval = dataReceiveCheckTimeInterval < 0 ? 100 : dataReceiveCheckTimeInterval;
        this.dataReceiveTimeOut = dataReceiveTimeOut;
        this.packetSize = packetSize;
        this.serialCommDataReceiveHandler = serialCommDataReceiveHandler;
        this.serialPortDataListener = new SerialCommDataListenerWithPackSize(this.packetSize,
                this.serialCommDataReceiveHandler == null ? this : this.serialCommDataReceiveHandler);
    }

    /**
     * 串行通信
     *
     * @param serialPortName               串口名称
     * @param baudRate                     波特率
     * @param dataBits                     数据位
     * @param stopBits                     停止位
     * @param parity                       平价
     * @param flowControl                  流控制
     * @param dataReceiveCheckTimeInterval 数据接收检查时间间隔
     * @param dataReceiveTimeOut           数据接收超时
     * @param delimiter                    定界符
     * @param serialCommDataReceiveHandler 串行通信数据接收处理程序
     */
    public SerialComm(String serialPortName,
                      int baudRate,
                      int dataBits,
                      int stopBits,
                      int parity,
                      int flowControl,
                      int dataReceiveCheckTimeInterval,
                      int dataReceiveTimeOut,
                      byte[] delimiter,
                      SerialCommDataReceiveHandler serialCommDataReceiveHandler) {
        this.serialPortName = serialPortName;
        this.baudRate = baudRate;
        this.dataBits = dataBits;
        this.stopBits = stopBits;
        this.parity = parity;
        this.flowControl = flowControl;
        this.dataReceiveCheckTimeInterval = dataReceiveCheckTimeInterval < 0 ? 100 : dataReceiveCheckTimeInterval;
        this.dataReceiveTimeOut = dataReceiveTimeOut;
        this.delimiter = delimiter;
        this.serialCommDataReceiveHandler = serialCommDataReceiveHandler;
        this.serialPortDataListener = new SerialCommDataListenerWithDelimiter(this.delimiter,
                this.serialCommDataReceiveHandler == null ? this : this.serialCommDataReceiveHandler);
    }

    /**
     * 检查串口是否存在
     *
     * @throws SerialCommException 串行异常
     */
    private void init() throws SerialCommException {
        for (SerialPort commPort : SerialPort.getCommPorts()) {
            if (commPort.getSystemPortName().equals(serialPortName)) {
                serialPort = commPort;
                return;
            }
        }
        throw new SerialCommException("未找到串口:" + serialPortName);
    }

    /**
     * 打开串行端口
     *
     * @throws SerialCommException 串行异常
     */
    public final void openSerialPort() throws SerialCommException {
        //检查串口是否存在
        init();
        //防止重复打开
        close();
        serialPort.setComPortParameters(baudRate, dataBits, stopBits, parity);
        serialPort.setFlowControl(flowControl);
        serialPort.addDataListener(serialPortDataListener);
        if (!serialPort.openPort()) {
            throw new SerialCommException("串口:" + serialPortName + "打开失败(串口被占用或串口损坏)");
        }
    }

    /**
     * 写
     *
     * @param command 命令
     * @return {@code SerialComm}
     * @throws SerialCommException 串行异常
     */
    public final SerialComm write(String command) throws SerialCommException {
        byte[] bytes = commandBuffer.get(command);
        if (bytes == null) {
            byte[] bytes1 = command.getBytes(StandardCharsets.UTF_8);
            commandBuffer.put(command, bytes1);
            bytes = bytes1;
        }
        return write(bytes);
    }

    /**
     * 写
     *
     * @param bytes 字节
     * @return {@code SerialComm}
     * @throws SerialCommException 串行异常
     */
    public final SerialComm write(byte[] bytes) throws SerialCommException {
        receivedSerialPortData = null;
        if (serialPort == null || !serialPort.isOpen()) {
            throw serialPort == null ? new SerialCommException("串口未初始化") : new SerialCommException("串口未打开");
        }
        if (bytes.length == serialPort.writeBytes(bytes, bytes.length)) {
            return this;
        } else {
            throw new SerialCommException("串口消息写入失败");
        }
    }

    /**
     * 读取字符串
     *
     * @return {@code String}
     * @throws SerialCommException 串行异常
     */
    public final String readString() throws SerialCommException {
        sleep();
        if (receivedSerialPortData != null) {
            return getReceivedSerialPortDataWithMessage();
        }
        for (int i = 0; i < dataReceiveTimeOut - 1; i++) {
            if (receivedSerialPortData != null) {
                return getReceivedSerialPortDataWithMessage();
            }
            sleep();
        }
        throw new SerialCommException("串口消息超时");
    }

    /**
     * 读取byte[]
     *
     * @return {@code byte[]}
     * @throws SerialCommException 串行异常
     */
    public final byte[] readBytes() throws SerialCommException {
        sleep();
        if (receivedSerialPortData != null) {
            return receivedSerialPortData;
        }
        for (int i = 0; i < dataReceiveTimeOut - 1; i++) {
            if (receivedSerialPortData != null) {
                return receivedSerialPortData;
            }
            sleep();
        }
        throw new SerialCommException("串口消息超时");
    }

    private void sleep() {
        try {
            Thread.sleep(dataReceiveCheckTimeInterval);
        } catch (InterruptedException _) {

        }
    }


    /**
     * 更新数据包大小
     *
     * @param packetSize 新的数据包大小
     * @throws SerialCommException 串行异常
     */
    public final void updatePacketSize(int packetSize) throws SerialCommException {
        if (serialPort == null) {
            throw new SerialCommException("串口未初始化");
        }
        this.packetSize = packetSize;
        this.serialPortDataListener = new SerialCommDataListenerWithPackSize(this.packetSize,
                this.serialCommDataReceiveHandler
                        == null ? this : this.serialCommDataReceiveHandler);
        serialPort.removeDataListener();
        serialPort.addDataListener(this.serialPortDataListener);
    }

    /**
     * 更新分隔符
     *
     * @param delimiter 新的分隔符
     * @throws SerialCommException 串行异常
     */
    public final void updateDelimiter(byte[] delimiter) throws SerialCommException {
        if (serialPort == null) {
            throw new SerialCommException("串口未初始化");
        }
        this.delimiter = delimiter;
        this.serialPortDataListener = new SerialCommDataListenerWithDelimiter(this.delimiter, this.serialCommDataReceiveHandler
                == null ? this : this.serialCommDataReceiveHandler);
        serialPort.removeDataListener();
        serialPort.addDataListener(this.serialPortDataListener);
    }

    /**
     * 数据接收
     *
     * @param data 数据
     */
    @Override
    public void dataReceive(byte[] data) {
        if (receivedSerialPortData == null) {
            receivedSerialPortData = data;
        } else {
            byte[] bytes = mergeArrays(receivedSerialPortData, data);
            receivedSerialPortData = bytes;
        }
    }

    /**
     * 获取接收到串口数据
     *
     * @return {@code byte[] }
     */
    public final byte[] getReceivedSerialPortData() {
        return receivedSerialPortData;
    }

    /**
     * 获取收到串口数据并且转换为字符串
     *
     * @return {@code String }
     */
    public final String getReceivedSerialPortDataWithMessage() {
        return new String(receivedSerialPortData, StandardCharsets.UTF_8);
    }

    /**
     * 合并数组
     *
     * @param oldArray 旧数组
     * @param newArray 新建阵列
     * @return {@code byte[] }
     */
    public static byte[] mergeArrays(byte[] oldArray, byte[] newArray) {
        byte[] mergedArray = new byte[oldArray.length + newArray.length];
        System.arraycopy(oldArray, 0, mergedArray, 0, oldArray.length);
        System.arraycopy(newArray, 0, mergedArray, oldArray.length, newArray.length);
        return mergedArray;
    }

    @Override
    public void close() {
        if (serialPort != null) {
            serialPort.closePort();
        }
    }
}
