package io.gonative.android.utils;

import com.hoho.android.usbserial.driver.UsbSerialPort;

import java.util.Objects;

public class USBSerialDeviceDetails {
    UsbSerialPort usbSerialPort;
    String id;
    String serialNumber;
    String noOfChannels;
    int noOfScaleConnected = 2;

    public USBSerialDeviceDetails(UsbSerialPort usbSerialPort, String id, String serialNumber,String noOfChannels,int noOfScaleConnected) {
        this.usbSerialPort = usbSerialPort;
        this.id = id;
        this.serialNumber = serialNumber;
        this.noOfChannels = noOfChannels;
        this.noOfScaleConnected = noOfScaleConnected;
    }

    public USBSerialDeviceDetails(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getId() {
        return id;
    }

    public UsbSerialPort getUsbSerialPort() {
        return usbSerialPort;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsbSerialPort(UsbSerialPort usbSerialPort) {
        this.usbSerialPort = usbSerialPort;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getNoOfChannels() {
        return noOfChannels;
    }

    public void setNoOfChannels(String noOfChannels) {
       this.noOfChannels = noOfChannels;
    }

    public int getNoOfScaleConnected() {
        return noOfScaleConnected;
    }

    public void setNoOfScaleConnected(int noOfScaleConnected) {
        this.noOfScaleConnected = noOfScaleConnected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        USBSerialDeviceDetails that = (USBSerialDeviceDetails) o;
        return Objects.equals(serialNumber, that.serialNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serialNumber);
    }
}
