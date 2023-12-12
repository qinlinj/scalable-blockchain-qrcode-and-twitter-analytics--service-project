package edu.cmu.qrcode.qrcode_vertx.utility;

import java.math.BigInteger;

public class HexToMatrix {
  private final static int Side=32;
  /**
   * Length of an integer in bit
   */
  private final static int I=32;

  public static boolean[][] convertToMatrix(String hexString){
    int side=Side;
    boolean[][] result=new boolean[side][side];
    // break hexString by 0x
    String[] hexes=hexString.split("0x");
    String bits="";
    int clock=0;
    for(int i=0;i<side;i++){
      for(int j=0; j<side; j++){
        if((i*side+j)%I==0){
          int index=(i*side+j)/I;
          String hex=hexes[index+1];
          BigInteger bigInteger=new BigInteger(hex,16);
          String binary=bigInteger.toString(2);
          bits="0".repeat(I-binary.length())+binary;
          clock=0;
        }
        result[i][j]=bits.charAt(clock)=='1';
        clock++;
      }
    }
    return result;
  }

}
