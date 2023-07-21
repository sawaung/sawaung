package io.gonative.android;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import java.util.Locale;

import io.gonative.android.utils.POSCommandsUtils;
import io.gonative.android.utils.USBDeviceReader;

public class MyJavaScriptInterface {
    public static String invoice_no ="";
    public static String amount="";
    Context mContext;

    public MyJavaScriptInterface(Context context) {
        mContext = context;
    }

    @JavascriptInterface
    public void callAndroidFun(String message) {
        //Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        //android function


    }

    @JavascriptInterface
    public void saleTransaction(String invoiceNo,String amt) {
        invoice_no = invoiceNo;
        amount = amt;
        Toast.makeText(mContext,"inv_no " +invoiceNo + " amt " + amount, Toast.LENGTH_SHORT).show();
        try {
            if (invoiceNo.length() < 6) {
                invoiceNo = String.format(Locale.ENGLISH, "%06d", Integer.parseInt(invoiceNo));
            } else if (invoiceNo.length() > 6) {
                invoiceNo = invoiceNo.substring(0, 6);
            }
            String command = POSCommandsUtils.createCommandForPayment(Double.parseDouble(amount), invoiceNo);
            if (!command.isEmpty()) {
                USBDeviceReader.lastCommandType = 1;
                USBDeviceReader.sendCommandToUSBDevice(command);
            }
        }catch(Exception e){
            Toast.makeText(mContext,e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        Log.i("JavaScriptInterface","inv_no " +invoiceNo + " amt " + amount);
    }


    // Add more methods as needed
}
