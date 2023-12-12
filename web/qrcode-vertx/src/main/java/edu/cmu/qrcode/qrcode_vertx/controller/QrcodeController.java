package edu.cmu.qrcode.qrcode_vertx.controller;
import edu.cmu.qrcode.qrcode_vertx.utility.Locator;
import edu.cmu.qrcode.qrcode_vertx.utility.LogisticMapUtility;
import edu.cmu.qrcode.qrcode_vertx.utility.MatrixToHex;
import edu.cmu.qrcode.qrcode_vertx.utility.QrcodeDecoder;
import edu.cmu.qrcode.qrcode_vertx.utility.QrcodeEncoder;
import edu.cmu.qrcode.qrcode_vertx.utility.QrcodeWriterReader;
import edu.cmu.qrcode.qrcode_vertx.utility.HexToMatrix;
import io.vertx.core.AbstractVerticle;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.handler.BodyHandler;

public class QrcodeController extends AbstractVerticle {

  @Override
  public void start() {
    Router router = Router.router(vertx);

    router.route().handler(BodyHandler.create());

    router.get("/").handler(this::index);
    router.get("/qrcode").handler(this::getData);

    HttpServer server = vertx.createHttpServer();
    server.requestHandler(router).listen(8080);
  }

  private void index(RoutingContext context) {
    HttpServerResponse response = context.response();
    response.putHeader("content-type", "text/plain").end("Healthy Qr Code Service!");
  }

  private void getData(RoutingContext context) {
    String data = context.request().getParam("data");
    String type = context.request().getParam("type");
    String result;

    if ("encode".equals(type)) {
      result = encodeData(data);
    } else if ("decode".equals(type)) {
      result = decodeData(data);
    } else {
      result = "";
    }

    context.response().putHeader("content-type", "text/plain").end(result);
  }

  private String encodeData(String data) {
    boolean isV1 = data.length() <= 13;
    byte[] bytes = QrcodeEncoder.encodeStringToBits(data);
    boolean[][] originalQr = QrcodeWriterReader.byteToMatrix(isV1, bytes);
    boolean[][] encryptedQr = LogisticMapUtility.lmMatrix(originalQr);
    return MatrixToHex.convertToHex(encryptedQr);
  }

  private String decodeData(String data) {
    boolean[][] encryptedQr = HexToMatrix.convertToMatrix(data);
    boolean[][] largeQr = LogisticMapUtility.lmMatrix(encryptedQr);
    boolean[][] originalQr = Locator.locateQrcode(largeQr);
    byte[] encodedMsg = QrcodeWriterReader.readQrCode(originalQr);
    return QrcodeDecoder.decodeByteToString(encodedMsg);
  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new QrcodeController());
  }
}

