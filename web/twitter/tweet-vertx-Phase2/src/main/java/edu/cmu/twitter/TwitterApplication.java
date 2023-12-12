package edu.cmu.twitter;

import edu.cmu.twitter.controller.TwitterController;
import io.vertx.core.Vertx;


public class TwitterApplication {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new TwitterController());
	}
}