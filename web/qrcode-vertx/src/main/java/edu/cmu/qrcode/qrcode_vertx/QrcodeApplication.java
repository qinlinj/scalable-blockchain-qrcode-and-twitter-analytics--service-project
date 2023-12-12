package edu.cmu.qrcode.qrcode_vertx;

import edu.cmu.qrcode.qrcode_vertx.controller.QrcodeController;
import io.vertx.core.Vertx;

public class QrcodeApplication {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new QrcodeController());
  }
}
