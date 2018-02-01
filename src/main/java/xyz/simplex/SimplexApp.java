package xyz.simplex;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisOptions;
import xyz.simplex.service.RedisTaskService;
import xyz.simplex.verticles.RestVerticle;

public class SimplexApp {

    public static Verticle RedisTodo() {

        return new RestVerticle();
    }


    public static void runTodo(Verticle todoVerticle) {
        Vertx vertx = Vertx.vertx();

        final int port = Integer.getInteger("http.port", 8082);
        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put("http.port", port)
                );

        vertx.deployVerticle(todoVerticle, options, res -> {
            if (res.succeeded())
                System.out.println("Todo service is running at " + port + " port...");
            else
                res.cause().printStackTrace();
        });
    }

    public static void main(String[] args) {
        runTodo(RedisTodo());
    }
}
