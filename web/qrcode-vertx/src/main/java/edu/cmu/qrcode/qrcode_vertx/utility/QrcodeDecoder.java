package edu.cmu.qrcode.qrcode_vertx.utility;

public class QrcodeDecoder {
    public static String decodeByteToString(byte[] bytes){
        StringBuilder message=new StringBuilder();

        for(int i=1; i<bytes.length; i+=2){
            message.append((char)bytes[i]);
        }
        return message.toString();
    }
}
