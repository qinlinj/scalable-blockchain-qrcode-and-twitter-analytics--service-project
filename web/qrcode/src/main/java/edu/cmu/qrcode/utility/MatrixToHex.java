package edu.cmu.qrcode.utility;

import java.math.BigInteger;

public class MatrixToHex {
    public static String convertToHex(boolean[][] matrix){

        StringBuilder bits=new StringBuilder();
        StringBuilder hex=new StringBuilder();

        for(int i=0;i<matrix.length;i++){
            for(int j=0; j<matrix[0].length; j++){
                if(bits.length()==32){
                    BigInteger decimal=new BigInteger(bits.toString(),2);
                    hex.append("0x").append(decimal.toString(16));
                    bits=new StringBuilder();
                }
                bits.append(matrix[i][j]?1:0);
            }
        }
        if(bits.length()!=0){
            BigInteger decimal=new BigInteger("0".repeat(32-bits.length())+bits.toString(),2);
            hex.append("0x").append(decimal.toString(16));
        }
        return hex.toString();
    }
}
