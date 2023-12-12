package edu.cmu.qrcode.utility;

public class LogisticMapUtility {
    /**
     * Length of a byte in bit
     */
    private final static int B=8;

    /**
     * Length of a side of V1
     */
    private final static int V1=21;

    /**
     * Length of a side of V2
     */
    private final static int V2=25;

    /**
     * Length of large QR code
     */
    private final static int LARGE=32;

    /**
     * start with x(0) = 0.1, r = 4.0
     */
    private final static double X0=0.1;
    /**
     * start with x(0) = 0.1, r = 4.0
     */
    private final static double R=4.0;

    /**
     * map x(n) to [0,255]
     */
    private final static int N=255;

    private final static int[] LMV1=initLM(V1);
    private final static int[] LMV2=initLM(V2);

    private final static int[] LMLarge=initLM(LARGE);

    /**
     * Generate logistic map with x(n+1) = rx(n)(1-x(n))
     * @param side
     * @return
     */
    private static int[] initLM(int side){
        int length=(side*side)/B+1;

        int[] logisticMap=new int[length];
        double curr=X0;

        for(int i=0; i<length; i++){
            logisticMap[i]=(int)Math.floor(curr*N);
            curr=R*curr*(1-curr);
        }

        return logisticMap;
    }

    public static boolean[][] lmMatrix(boolean[][] matrix){
        int side= matrix.length;
        boolean[][] result=new boolean[side][side];
        int[] lm=side==V1?LMV1:(side==V2?LMV2:LMLarge);

        byte x=(byte)lm[0];
        int clock=0;
        for(int i=0;i<side;i++){
            for(int j=0; j<side; j++){
                if(clock==B){
                    x=(byte)lm[(i*side+j)/B];
                    clock=0;
                }
                boolean xbit=((x>>clock)&1)==1;
                result[i][j]=matrix[i][j]^xbit;
                clock++;
            }
        }

        return result;
    }


}
