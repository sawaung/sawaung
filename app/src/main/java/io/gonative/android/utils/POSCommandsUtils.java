package io.gonative.android.utils;

public class POSCommandsUtils {

    private static byte charToByte(char character){
        return (byte) character;
    }

    private static char decimalToASCII(int number){
        char channelId = '0';
        if(number <= 9){
            channelId =  String.valueOf(number).charAt(0);
        }else{
            channelId = (char) ('A' + number - 10);
        }
        return channelId;
    }

    private static String byteToHexString(byte[] payload) {
        if (payload == null) return "<empty>";
        StringBuilder stringBuilder = new StringBuilder(payload.length);
        for (byte byteChar : payload)
            stringBuilder.append(String.format("%02X ", byteChar));
        return stringBuilder.toString();
    }

    public static String createCommandForPayment(double amount,String invoiceNumber){
        try {
            byte START = (byte) 0x02;
            byte LENGTH_START = 0x00; //lengthOfTheCommand
            byte LENGTH = 0x46; //lengthOfTheCommand
            byte th1 = charToByte('6');
            byte th2 = charToByte('0');

            byte th3 = charToByte('0');
            byte th4 = charToByte('0');
            byte th5 = charToByte('0');
            byte th6 = charToByte('0');

            byte th7 = charToByte('0');
            byte th8 = charToByte('0');
            byte th9 = charToByte('0');
            byte th10 = charToByte('0');

            byte ph1 = charToByte('1');
            byte ph2 = charToByte('0'); // request = 0, response = 1

            byte ph3 = charToByte('2');
            byte ph4 = charToByte('0');

            byte ph5 = charToByte('0');
            byte ph6 = charToByte('0');

            byte ph7 = charToByte('0');

            byte ph8 = (byte) 0x1c;

            byte sr1 = charToByte('4');
            byte sr2 = charToByte('0');

            byte sr3 = (byte) 0x00;
            byte sr4 = (byte) 0x12;

            LogUtils.Print("PaymentGatewayCommand", " Orignal amount :" + amount);
            int amt = (int) (amount * 100);
            LogUtils.Print("PaymentGatewayCommand", " Amount * 100 . :" + amt);
            String data = String.format("%12s", amt).replace(' ', '0');
            LogUtils.Print("PaymentGatewayCommand", " Amount 12 char . :" + data);

            byte sr5 = charToByte(data.charAt(0));
            byte sr6 = charToByte(data.charAt(1));
            byte sr7 = charToByte(data.charAt(2));
            byte sr8 = charToByte(data.charAt(3));
            byte sr9 = charToByte(data.charAt(4));
            byte sr10 = charToByte(data.charAt(5));
            byte sr11 = charToByte(data.charAt(6));
            byte sr12 = charToByte(data.charAt(7));
            byte sr13 = charToByte(data.charAt(8));
            byte sr14 = charToByte(data.charAt(9));
            byte sr15 = charToByte(data.charAt(10));
            byte sr16 = charToByte(data.charAt(11));
            byte sr17 = (byte) 0x1c;

            byte vra1 = charToByte('6');
            byte vra2 = charToByte('5');

            byte vra3 = (byte) 0x00;
            byte vra4 = (byte) 0x06;
            byte vra5 = charToByte(invoiceNumber.charAt(0));
            byte vra6 = charToByte(invoiceNumber.charAt(1));
            byte vra7 = charToByte(invoiceNumber.charAt(2));
            byte vra8 = charToByte(invoiceNumber.charAt(3));
            byte vra9 = charToByte(invoiceNumber.charAt(4));
            byte vra10 = charToByte(invoiceNumber.charAt(5));

            byte vra11 = (byte) 0x1c;

            byte END = (byte) 0x03;

            byte XORCheckSum = (byte) (LENGTH_START ^ LENGTH ^ th1 ^ th2 ^ th3 ^ th4 ^ th5 ^ th6 ^ th7 ^ th8 ^ th9 ^ th10 ^ ph1 ^ ph2 ^ ph3 ^ ph4 ^ ph5 ^ ph6 ^ ph7 ^ ph8 ^ sr1 ^ sr2 ^ sr3 ^ sr4 ^ sr5 ^ sr6 ^ sr7 ^ sr8 ^ sr9 ^ sr10 ^ sr11 ^ sr12 ^ sr13 ^ sr14 ^ sr15 ^ sr16 ^ sr17 ^ vra1 ^ vra2 ^ vra3 ^ vra4 ^ vra5 ^ vra6 ^ vra7 ^ vra8 ^ vra9 ^ vra10 ^ vra11 ^ END);

            byte[] command = {START, LENGTH_START, LENGTH, th1, th2, th3, th4, th5, th6, th7, th8, th9, th10, ph1, ph2, ph3, ph4, ph5, ph6, ph7, ph8, sr1, sr2, sr3, sr4, sr5, sr6, sr7, sr8, sr9, sr10, sr11, sr12, sr13, sr14, sr15, sr16, sr17, vra1, vra2, vra3, vra4, vra5, vra6, vra7, vra8, vra9, vra10, vra11, END, XORCheckSum};

            String hexCommand = byteToHexString(command);
            LogUtils.Print("PaymentGatewayCommand", "COMMAND ->" + hexCommand);

            return hexCommand;
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public static boolean isTransactionSuccess(String response){
        try {
            String data = response.replace(" ", "");
            LogUtils.Print("PaymentGatewayCommand", "Response Orignal command : " + data);
            String data2ByteAfterSecondSepration = data.substring(132, 136);
            LogUtils.Print("PaymentGatewayCommand", "Success 2 byte : " + data2ByteAfterSecondSepration);
            String isSuccess = HexCode.hexToASCII(data2ByteAfterSecondSepration);
            LogUtils.Print("PaymentGatewayCommand", "success data : " + isSuccess);

            String data2ByteAfterLengthToCheckMessage = data.substring(50, 130);
            LogUtils.Print("PaymentGatewayCommand", "success data : " + data2ByteAfterLengthToCheckMessage);
            String successMessage = HexCode.hexToASCII(data2ByteAfterLengthToCheckMessage);
            LogUtils.Print("PaymentGatewayCommand", "success data text : " + successMessage);
            return isSuccess.equals("01") && successMessage.toLowerCase().contains("approval");
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }
}
