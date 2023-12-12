package edu.cmu.qrcode.controller;

import edu.cmu.qrcode.utility.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QrcodeController {

    // for local test
    public static void main(String[] args){
//        QrcodeController controller=new QrcodeController();

        // encode
//        String data="CC Team is awesome!";
//        System.out.println(controller.getData(data,"encode"));

        // decode
//        String data="0x78c0a8720xab39ae230x6e35f9930x2c005df50x6dd081e40xa72e300xdcfbcf2e0xed398a5f0x4e04bc880xa1eeb3590x5ac1458d0xda9e78e90x571420290x3062d5140x894a17920x6bca9ee60x32732ca0x99650de50x562f5dae0xc07624550xd507f9510xd6faee8f0x73b68f200xc9c089bc0x3c08acf20xedfc8d460x7f8f405e0x38b574940x5ec41c7f0xa3b580d90xeccd17c10xdb59a6b6";
//        System.out.println(controller.getData(data,"decode"));
    }

    /**
     * logger for Blockchain controller.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(
            QrcodeController.class
    );

    /**
     * Handle Get request
     * @param data
     * @param type
     * @return
     */
    @GetMapping("/qrcode")
    public String getData(@RequestParam("data") String data, @RequestParam("type") String type){
        if(type.equals("encode")){
            boolean isV1=data.length()<=13;
            byte[] bytes= QrcodeEncoder.encodeStringToBits(data);
            boolean[][] originalQr= QrcodeWriterReader.byteToMatrix(isV1,bytes);
            boolean[][] encryptedQr= LogisticMapUtility.lmMatrix(originalQr);
            String result=MatrixToHex.convertToHex(encryptedQr);

            return result;
        }else if(type.equals("decode")){
            boolean[][] encryptedQr= HexToMatrix.convertToMatrix(data);
            boolean[][] largeQr=LogisticMapUtility.lmMatrix(encryptedQr);
            boolean[][] originalQr=Locator.locateQrcode(largeQr);
            byte[] encodedMsg= QrcodeWriterReader.readQrCode(originalQr);
            String originalMsg=QrcodeDecoder.decodeByteToString(encodedMsg);

            return originalMsg;
        }else{
            return "";
        }
    }

    @GetMapping("/")
    public String index() {
        return "Healthy Qr Code Service!";
    }
}