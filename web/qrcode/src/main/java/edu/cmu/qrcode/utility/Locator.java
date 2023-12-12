package edu.cmu.qrcode.utility;

import java.util.Arrays;

public class Locator {

    /**
     * Length of a side of V1
     */
    private final static int V1=21;

    /**
     * Length of a side of V2
     */
    private final static int V2=25;
    private static int PDPSIDE=7;
    private static boolean[][] PDP=initPDP();

    private static boolean[][] initPDP(){
        boolean[][] result=new boolean[PDPSIDE][PDPSIDE];
        for(int i=0; i<Locator.PDPSIDE; i++){
            for(int j=0; j<Locator.PDPSIDE;j++){
                result[i][j]=true;
            }
        }
        for(int i=0; i<Locator.PDPSIDE-3;i++){
            result[1][1+i]=false;
            result[1+i][Locator.PDPSIDE-2]=false;
            result[Locator.PDPSIDE-2][Locator.PDPSIDE-2-i]=false;
            result[Locator.PDPSIDE-2-i][1]=false;
        }
        return result;
    }

    private static boolean isPDP(int row, int col, boolean[][] matrix) {
        if (row + PDPSIDE >= matrix.length || col + PDPSIDE >= matrix[0].length) {
            return false;
        }
        for (int i = 0; i < PDPSIDE; i++) {
            for (int j = 0; j < PDPSIDE; j++) {
                if (PDP[i][j] != matrix[row + i][col + j]) return false;
            }
        }
        return true;
    }

    private static void rotateMatrix(int rotation, boolean[][] matrix){
        int side=matrix.length;

        switch (rotation){
            case 0:
                break;
            case 90:
                // rotate horizontally
                for(int i=0; i<side;i++){
                    for(int j=0; j<=side/2; j++){
                        boolean temp=matrix[i][j];
                        matrix[i][j]=matrix[i][side-j-1];
                        matrix[i][side-j-1]=temp;
                    }
                }
                // rotate diagonal
                for(int i=0; i<side;i++){
                    for(int j=i+1;j<side;j++){
                        boolean temp=matrix[i][j];
                        matrix[i][j]=matrix[j][i];
                        matrix[j][i]=temp;
                    }
                }
                break;
            case 180:
                // rotate horizontally
                for(int i=0; i<side;i++){
                    for(int j=0; j<=side/2; j++){
                        boolean temp=matrix[i][j];
                        matrix[i][j]=matrix[i][side-j-1];
                        matrix[i][side-j-1]=temp;
                    }
                }
                // rotate vertically
                for(int i=0; i<=side/2;i++){
                    for(int j=0; j<side; j++){
                        boolean temp=matrix[i][j];
                        matrix[i][j]=matrix[side-i-1][j];
                        matrix[side-i-1][j]=temp;
                    }
                }
                break;
            case 270:
                // rotate vertically
                for(int i=0; i<=side/2;i++){
                    for(int j=0; j<side; j++){
                        boolean temp=matrix[i][j];
                        matrix[i][j]=matrix[side-i-1][j];
                        matrix[side-i-1][j]=temp;
                    }
                }
                // rotate diagonal
                for(int i=0; i<side;i++){
                    for(int j=i+1;j<side;j++){
                        boolean temp=matrix[i][j];
                        matrix[i][j]=matrix[j][i];
                        matrix[j][i]=temp;
                    }
                }
                break;
            default: break;
        }
    }

    public static boolean[][] locateQrcode(boolean[][] largeQrcode){
        // find the first PDP
        // -> get the other pdp
        // -> read the qrcode

        // denote the rotation of the qrcode
        // only 0,90,180,270 are allowed
        int rotation=0;
        // denote the qrcode version
        boolean isV1=false;
        boolean found=false;
        int side= largeQrcode.length;
        int row=0;
        int col=0;
        outerloop:
        for(int i=0; i<side-PDPSIDE; i++){
            for(int j=0; j<side-PDPSIDE; j++){
                if(isPDP(i,j,largeQrcode)){
                    row=i;
                    col=j;
                    // check if it is 180
                    if(j>=V1-PDPSIDE) {
                        rotation=180;
                        if(isPDP(i+V1-PDPSIDE,j,largeQrcode)) {
                            isV1=true;
                            col=j-(V1-PDPSIDE);
                        }else{
                            isV1=false;
                            row=j-(V2-PDPSIDE);
                        }
                    } else{
                        // assume the hidden qrcode is version 1
                        if(isPDP(i+V1-PDPSIDE,j,largeQrcode)){
                            isV1=true;
                            // find the rotation
                            if(isPDP(i,j+V1-PDPSIDE,largeQrcode)){
                                rotation=0;
                            }else{
                                rotation=270;
                            }
                        }else if(isPDP(i+V2-PDPSIDE,j,largeQrcode)){
                            isV1=false;
                            if(isPDP(i,j+V2-PDPSIDE,largeQrcode)){
                                rotation=0;
                            }else{
                                rotation=270;
                            }
                        }else{
                            rotation=90;
                            // continue to find the version
                            if(isPDP(i,j+V1-PDPSIDE,largeQrcode)){
                                isV1=true;
                            }else{
                                isV1=false;
                            }
                        }
                    }
                    break outerloop;
                }
            }
        }

        int qrside=isV1?V1:V2;
        boolean[][] result=new boolean[qrside][qrside];
        // copy the area down
        for(int i=0;i<qrside;i++){
            for(int j=0; j<qrside; j++){
                result[i][j]=largeQrcode[row+i][col+j];
            }
        }
        // rotate to get the final result
        rotateMatrix(rotation,result);

        return result;
    }

    public static void main(String[] args){
        String data="0x2b23d6830x15a0de0d0x744784010x29e880700xfe1adf5c0xb96061290x1127b67c0x311690430xc63153140xf6e00650x92d3960b0xf59a79070x704e73d40x977fd8090xf516e98a0x3e0c19f10xac626d040x6a3e58650xca85aa3e0x6266b640x842ddcb40x4e7c879c0x85dd21240x3afae3dc0xe07908a70x664685970xb38246f70x511908330x40a111ee0xc12c8fd10x82984c520x4ddee6f6";
        boolean[][] largeqr=HexToMatrix.convertToMatrix(data);
        boolean[][] decoded=LogisticMapUtility.lmMatrix(largeqr);
        boolean[][] hiddenqr=Locator.locateQrcode(decoded);
        String qrString=MatrixToHex.convertToHex(hiddenqr);
        System.out.println(qrString);
    }

}
