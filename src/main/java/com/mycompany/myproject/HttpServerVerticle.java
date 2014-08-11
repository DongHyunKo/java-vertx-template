package com.mycompany.myproject;


import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import java.util.concurrent.ConcurrentMap;

/**
 * Created by dhko on 2014. 8. 8..
 */
public class HttpServerVerticle extends Verticle {

    final static private String URL_PREFIX = "http://s.plaync.com/";
//    @Override
//    public void start() {
//        super.start();
//
//        vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {
//            public void handle(HttpServerRequest req) {
//                System.out.println("Got request: " + req.uri());
//                System.out.println("Headers are: ");
//                for (Map.Entry<String, String> entry : req.headers()) {
//                    System.out.println(entry.getKey() + ":" + entry.getValue());
//                }
//
//                ConcurrentMap<String, Integer> map = vertx.sharedData().getMap("demo.mymap");
//                Integer setCount = map.get("test");
//                if( setCount != null ) {
//                    setCount ++;
//                } else setCount = 0;
//
//                map.put("test", setCount);
//
//
//
//
//                req.response().headers().set("Content-Type", "text/html; charset=UTF-8");
//                req.response().end("setCount:" + setCount.toString() + "context:" + vertx.currentContext().toString()
//                                + "thread:" + Thread.currentThread().getId()
//                );
//            }
//        }).listen(8080);
//
//    }


    @Override
    public void start() {
        super.start();

        RouteMatcher rm = new RouteMatcher();

        rm.get("/details/:user/:id", new Handler<HttpServerRequest>() {
            public void handle(HttpServerRequest req) {
                req.response().end("User: " + req.params().get("user") + " ID: " + req.params().get("id"));
            }
        });

        rm.get("/short", new Handler<HttpServerRequest>() {
            public void handle(HttpServerRequest req) {

                String originUrl = req.params().get("url");
//                System.out.println(originUrl);

                ConcurrentMap<String, Integer> map = vertx.sharedData().getMap("url.map");
                Integer index = map.get("index");
                index = index==null ? 0 : index;
                index++;
                map.put("index", index);

                String shortUrl = BijectiveUtil.encode( index );
                JsonObject json = new JsonObject();
//                json.putString("orignal", orignalUrl );
                json.putString("url", URL_PREFIX + shortUrl);
                req.response().end(json.toString());
            }
        });

        rm.get("/origin", new Handler<HttpServerRequest>() {
            public void handle(HttpServerRequest req) {

                String shortUrl = req.params().get("url");
//                System.out.println(shortUrl);
                String encodedStr = shortUrl.replace(URL_PREFIX, "");

                Integer index = BijectiveUtil.decode(encodedStr);
                JsonObject json = new JsonObject();
//                json.putNumber("index", index);
                json.putString("url", "http://payment.plaync.com/abcde/url");
                req.response().end(json.toString());
            }
        });

        // Catch all - serve the index page
        rm.getWithRegEx(".*", new Handler<HttpServerRequest>() {
            public void handle(HttpServerRequest req) {
                req.response().sendFile("route_match/index.html");
            }
        });

        vertx.createHttpServer().requestHandler(rm).listen(33081);
    }
}
