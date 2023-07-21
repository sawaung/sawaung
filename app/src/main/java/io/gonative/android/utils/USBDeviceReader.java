package io.gonative.android.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.gonative.android.MainActivity;
import io.gonative.android.R;

public class USBDeviceReader extends Service {
    private static final String TAG = "USBDeviceReader";
    public static List<USBSerialDeviceDetails> usbSerialPortList = new ArrayList<>();
    private final String CHANNEL_ID = "MQuestPOSForegroundServiceChannel";
    public static boolean isGettingIDOfScale = false;
    public static UsbManager manager;
    public static int lastCommandType = 0; //1 for the sale //2 for the send ack received //3 sale response received.


    /**
     * This is for the USB scale communication
     */
    private void initUSBDeviceReader() {
        // Find all available drivers from attached devices.
        manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        if (availableDrivers.isEmpty()) {
            return;
        }
        // Open a connection to the first available driver.
        List<UsbSerialDriver> driver = new ArrayList<>();
        for (int i = 0; i < availableDrivers.size(); i++) {
            LogUtils.Print(TAG, "initUSBDeviceReader:- " + availableDrivers.get(i).getDevice().getManufacturerName());
            if (availableDrivers.get(i).getDevice() != null && availableDrivers.get(i).getDevice().getManufacturerName() != null && (availableDrivers.get(i).getDevice().getManufacturerName().equalsIgnoreCase("INGENICO"))) {
                driver.add(availableDrivers.get(i));
            }
        }
        if (driver.size() > 0) {
            for (int i = 0; i < driver.size(); i++) {
                if (!manager.hasPermission(driver.get(i).getDevice())) {
                    PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(Constants.INTENT_ACTION_GRANT_USB), 0);
                    manager.requestPermission(driver.get(i).getDevice(), usbPermissionIntent);
                }

                UsbDeviceConnection connection = manager.openDevice(driver.get(i).getDevice());
                if (connection == null) {
                    continue;
                }
                if (driver.get(i).getPorts().isEmpty())
                    continue;
                UsbSerialPort usbSerialPort = driver.get(i).getPorts().get(0);
                if (usbSerialPort == null)
                    continue;
                LogUtils.Print(TAG, "Product Name: " + usbSerialPort.getDevice().getProductName());
                LogUtils.Print(TAG, "Device Name: " + usbSerialPort.getDevice().getDeviceName());
                LogUtils.Print(TAG, "Manufacturer Name: " + usbSerialPort.getDevice().getManufacturerName());
                LogUtils.Print(TAG, "Device Id: " + usbSerialPort.getDevice().getDeviceId());
                LogUtils.Print(TAG, "SerialNumber: " + usbSerialPort.getDevice().getSerialNumber());
                LogUtils.Print(TAG, "Product Id: " + usbSerialPort.getDevice().getProductId());
                LogUtils.Print(TAG, "Vendor Id: " + usbSerialPort.getDevice().getVendorId());
                LogUtils.Print(TAG, "getPortNumber: " + usbSerialPort.getPortNumber());
                USBSerialDeviceDetails usbSerialDeviceDetails = new USBSerialDeviceDetails(usbSerialPort,null,usbSerialPort.getDevice().getSerialNumber(),null,Constants.NO_SCALE_CONNECTED);
                usbSerialPortList.add(usbSerialDeviceDetails);
                try {
                    usbSerialPort.open(connection);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    usbSerialPort.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                SerialInputOutputManager usbIoManager = new SerialInputOutputManager(usbSerialPort, new SerialInputOutputManager.Listener() {
                    @Override
                    public void onNewData(byte[] data) {
                       String dataString = HexCode.toHexString(data).trim();//.replace(" ", "");
                        LogUtils.Print(TAG, "dataString :- " + dataString);
                        sendDataViaBroadCast(dataString);
                    }

                    @Override
                    public void onRunError(Exception e) {
                        e.printStackTrace();
                    }
                });
                usbIoManager.setReadBufferSize(512);
                usbIoManager.start();
            }
        }
    }

    private void sendDataViaBroadCast(String dataStr) {
        Intent RTReturn = new Intent(Constants.BROADCAST_USB_DEVICE_DATA);
        RTReturn.putExtra(Constants.data, dataStr);
        sendBroadcast(RTReturn);
    }

    /**
     * Send command to GateMachine controller to get Exit count
     */
    public static void sendCommandToUSBDevice(String commandToSend) {
        try {
            LogUtils.Print(TAG,"sendCommandToUSBDevice commandToSend:- "+commandToSend);
            if (usbSerialPortList != null && usbSerialPortList.size() > 0) {
                        usbSerialPortList.get(0).getUsbSerialPort().write(HexCode.fromHexString(commandToSend), 1000);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initUSBDeviceReader();
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0, notificationIntent, 0
        );
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.app_name))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
        return START_NOT_STICKY;
    }

    private void requestUsbPermission(UsbDevice usbDevice) {
        PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(Constants.INTENT_ACTION_USB_PERMISSION), 0);
        manager.requestPermission(usbDevice, permissionIntent);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "MQuestSmartScale Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
