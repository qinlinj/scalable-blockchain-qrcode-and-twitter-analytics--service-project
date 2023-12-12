package edu.cmu.qrcode.qrcode_vertx.utility;

import java.util.Arrays;

/**
 *
 */
public class QrcodeWriterReader {

    /**
     * Length of a side of V1
     */
    private final static int V1=21;

    /**
     * Length of a side of V2
     */
    private final static int V2=25;

    /**
     * Length of a byte in bit
     */
    private final static int B=8;

    private final static byte[] FILLER=new byte[]{(byte) Integer.parseInt("11101100", 2),(byte) Integer.parseInt("00010001", 2)};

    /**
     * This method will convert byte array to Version 1 (21*21) and Version 2 (25*25)
     * Version 1 maximum length: byte[28]
     * Version 2 maximum length: byte[46]
     * @param bytes
     * @return
     */
    public static boolean[][] byteToMatrix(boolean isV1, byte[] bytes){
        int side=isV1?V1:V2;

        boolean[][] matrix=new boolean[side][side];
        // The area that has been written
        boolean[][] written=new boolean[side][side];

        // Set up special patterns
        initMatrix(isV1,matrix, written);

        // Write bytes to matrix using zigzag
        // Start from bottom right
        int row=side-1;
        int col=side-1;
        boolean goUp=true;
        boolean goRight=false;

        int byteIndex=0;
        int bitIndex=0;
        int fillerByte=0;
        int fillerBit=0;

        // Break if it is the end
        while(true){
            // Write bytes into matrix
            if(byteIndex<bytes.length){
                boolean bit=((bytes[byteIndex]>>(B-bitIndex-1))&1)==1;
                matrix[row][col]=bit;
                written[row][col]=true;
                // Update bit position
                bitIndex++;
                // reset bit and increment byte
                if(bitIndex==B){
                    bitIndex=0;
                    byteIndex++;
                }
                // Continue writing the sequence
            }else{
                boolean bit=((FILLER[fillerByte]>>(B-fillerBit-1))&1)==1;
                matrix[row][col]=bit;
                written[row][col]=true;
                fillerBit++;
                if(fillerBit==B){
                    fillerBit=0;
                    fillerByte++;
                    fillerByte=(fillerByte)%FILLER.length;
                }
            }
            // Break if it reaches the end
            if(row==side-9 && col==0) break;
            // update row and col && goUp and goRight
            // go up
            if(goUp){
                    if(goRight){
                        col++;
                        row--;
                        // check if it is valid
                        if(col<side && row>=0){
                            // check if it meets the boundary of patterns
                            // if yes, change direction/bypass
                            // check which pattern it meets
                            if(written[row][col]) {
                                // if it is PDP
                                if (row == 7) {
                                    col -= 2;
                                    row++;
                                    goUp = false;
                                    // skip the timing pattern at matrix[][7]
                                    if (written[row][col]) {
                                        col--;
                                    }
                                    // for other patterns (timing or alignment)
                                    // move up to skip
                                }else if(row==side-9){
                                    row--;
                                    col-=2;
                                    goUp=true;
                                }else{
                                    while( row>=0 && written[row][col]){
                                        row--;
                                    }
                                }
                            }
                            goRight=false;
                            // meet the boundary, change direction
                        }else{
                            row++;
                            col-=2;
                            // check if it meets timing pattern
                            if(written[row][col]){
                                col--;
                            }
                            goRight=false;
                            goUp=false;
                        }
                        // go Left
                    }else{
                        col--;
                        goRight=true;
                    }
                    // go down
                }else{
                    if(goRight){
                        col++;
                        row++;
                        if(row<side){
                            if(written[row][col]){
                                // change direction when hitting the bottom left pattern
                                if(row==side-8){
                                    row--;
                                    col-=2;
                                    goUp=true;
                                }else{
                                    // skip the alignment pattern and timing pattern
                                    while(row<side&&written[row][col]){
                                        row++;
                                    }
                                }
                            }
                            // hit the boundary, change direction
                        }else{
                            row--;
                            col-=2;
                            goUp=true;
                            // check if it hits the bottom left pattern to go up
                            while(row>=0 && written[row][col]){
                                row--;
                            }
                        }
                        goRight=false;
                    }else{
                        col--;
                        goRight=true;
                    }
                }
        }


        // Debug: Print matrix
//        for(int i=0; i<side; i++){
//            for(int j=0; j<side; j++){
//                int bit=matrix[i][j]?1:0;
//                if(written[i][j]){
//                    System.out.print(bit+" ");
//                }else{
//                    System.out.print("- ");
//                }
//            }
//            System.out.println();
//        }
//        System.out.println("---------------------------------");
//        for(int i=0; i<side; i++){
//            for(int j=0; j<side; j++){
//                int bit=written[i][j]?1:0;
//                System.out.print(bit+" ");
//            }
//            System.out.println();
//        }

        return matrix;
    }

    /**
     * Init the matrix with special patterns
     * @param isV1 whether the matrix is V1
     * @param matrix qrcode
     * @param written written map
     */
    private static void initMatrix(boolean isV1, boolean[][] matrix, boolean[][] written){
        int side=isV1?V1:V2;

        // Initialize the matrix with all true
        for(int i=0; i<side; i++){
            Arrays.fill(written[i],false);
        }

        // Mark Position Detection Pattern (7*7+white space around)
        // Upper left
        paintPDP(0,0,matrix, written);
        for(int i=0; i<8;i++){
            matrix[i][7]=false;
            matrix[7][i]=false;
            written[i][7]=true;
            written[7][i]=true;
            matrix[i][8]=false;
            written[i][8]=true;
        }
        // Bottom left
        paintPDP(side-7,0,matrix, written);
        for(int i=0; i<8;i++){
            matrix[side-1-i][7]=false;
            matrix[side-8][i]=false;
            written[side-1-i][7]=true;
            written[side-8][i]=true;
            matrix[side-1-i][8]=false;
            written[side-1-i][8]=true;
        }
        // Upper right
        paintPDP(0,side-7,matrix, written);
        for(int i=0; i<8;i++){
            matrix[i][side-8]=false;
            matrix[7][side-1-i]=false;
            written[i][side-8]=true;
            written[7][side-1-i]=true;
        }

        // Mark Alignment Pattern
        if(!isV1){
            int row=16, col=16; // start position at (16,16)
            for(int i=row; i<row+5; i++){
                for(int j=col; j<col+5; j++) {
                    matrix[i][j] = true;
                    written[i][j] = true;
                }
            }
            for(int i=0; i<2; i++){
                matrix[row+1][col+1+i]=false;
                matrix[row+1+i][col+3]=false;
                matrix[row+3][col+3-i]=false;
                matrix[row+3-i][col+1]=false;
            }
            for(int i=0; i<5;i++){
                matrix[row+i][col-1]=false;
                written[row+i][col-1]=true;
            }
        }

        // Mark Timing Pattern
        boolean isBlack=true;
        int length=side-2*8;
        for(int i=0; i<length; i++){
            matrix[6][8+i]=isBlack;
            written[6][8+i]=true;
            matrix[8+i][6]=isBlack;
            written[8+i][6]=true;
            isBlack=!isBlack;
        }
    }

    /**
     * Draw Position Detection Pattern at the given row and col
     * @param row row of upper left corner
     * @param col col of upper left corner
     * @param matrix
     * @param written
     */
    private static void paintPDP(int row, int col, boolean[][] matrix, boolean[][] written){
        int size=7; // size of PDP
        for(int i=row; i<row+size; i++){
            for(int j=col; j<col+size;j++){
                matrix[i][j]=true;
                written[i][j]=true;
            }
        }
        for(int i=0; i<size-3;i++){
            matrix[row+1][col+1+i]=false;
            matrix[row+1+i][col+size-2]=false;
            matrix[row+size-2][col+size-2-i]=false;
            matrix[row+size-2-i][col+1]=false;
        }
    }

    public static byte[] readQrCode(boolean[][] matrix){
        boolean isV1=matrix.length==V1;
        int side=isV1?V1:V2;

        boolean[][] written=new boolean[side][side];
        initMatrix(isV1,new boolean[side][side],written);

        // Write bytes to matrix using zigzag
        // Start from bottom right
        int row=side-1;
        int col=side-1;
        boolean goUp=true;
        boolean goRight=false;

        int byteIndex=0;
        int bitIndex=0;
        int fillerByte=0;
        int fillerBit=0;

        StringBuilder binary=new StringBuilder();

        // Break if it is the end
        while(true) {
            // Read bits into matrix
            binary.append(matrix[row][col] ? 1 : 0);
            // Break if it reaches the end
            if (row == side - 9 && col == 0) break;
            // update row and col && goUp and goRight
            // go up
            if (goUp) {
                if (goRight) {
                    col++;
                    row--;
                    // check if it is valid
                    if (col < side && row >= 0) {
                        // check if it meets the boundary of patterns
                        // if yes, change direction/bypass
                        // check which pattern it meets
                        if (written[row][col]) {
                            // if it is PDP
                            if (row == 7) {
                                col -= 2;
                                row++;
                                goUp = false;
                                // skip the timing pattern at matrix[][7]
                                if (written[row][col]) {
                                    col--;
                                }
                                // for other patterns (timing or alignment)
                                // move up to skip
                            } else if (row == side - 9) {
                                row--;
                                col -= 2;
                                goUp = true;
                            } else {
                                while (row >= 0 && written[row][col]) {
                                    row--;
                                }
                            }
                        }
                        goRight = false;
                        // meet the boundary, change direction
                    } else {
                        row++;
                        col -= 2;
                        // check if it meets timing pattern
                        if (written[row][col]) {
                            col--;
                        }
                        goRight = false;
                        goUp = false;
                    }
                    // go Left
                } else {
                    col--;
                    goRight = true;
                }
                // go down
            } else {
                if (goRight) {
                    col++;
                    row++;
                    if (row < side) {
                        if (written[row][col]) {
                            // change direction when hitting the bottom left pattern
                            if (row == side - 8) {
                                row--;
                                col -= 2;
                                goUp = true;
                            } else {
                                // skip the alignment pattern and timing pattern
                                while (row < side && written[row][col]) {
                                    row++;
                                }
                            }
                        }
                        // hit the boundary, change direction
                    } else {
                        row--;
                        col -= 2;
                        goUp = true;
                        // check if it hits the bottom left pattern to go up
                        while (row >= 0 && written[row][col]) {
                            row--;
                        }
                    }
                    goRight = false;
                } else {
                    col--;
                    goRight = true;
                }
            }
        }
        // convert binary string to byte array
        String binaryStr=binary.toString();
        int payload=Integer.parseInt(binaryStr.substring(0, B),2);
        byte[] bytes=new byte[payload*2+1];

        bytes[0]=(byte)(payload& 0xFF);

        for(int i=1;i<payload*2+1;i++){
            String byteStr="";
            if(i*B+B<binaryStr.length()){
                byteStr=binaryStr.substring(i*B, i*B+B);
            }else{
                if(i*B<binaryStr.length()){
                    byteStr=binaryStr.substring(i*B);
                }else{
                    byteStr="0";
                }
            }
            bytes[i]=(byte)(Integer.parseInt(byteStr,2)&0xFF);
        }
        return bytes;
    }


}
