package xyz.simplex.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.api.validation.HTTPRequestValidationHandler;
import io.vertx.ext.web.api.validation.ValidationException;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.redis.RedisOptions;
import xyz.simplex.Constants;
import xyz.simplex.entity.*;
import xyz.simplex.service.RedisTaskService;
import xyz.simplex.service.TaskService;
import xyz.simplex.service.ZeroOneKnapsack;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;


public class RestVerticle extends AbstractVerticle {

    private static final String HTTP_HOST = "0.0.0.0";
    private static final String REDIS_HOST = "127.0.0.1";
    private static final int HTTP_PORT = 8082;
    private static final int REDIS_PORT = 6379;

    private static final Logger LOGGER = LoggerFactory.getLogger(RestVerticle.class);

    private static final String HOST = "0.0.0.0";
    private static final int PORT = 8082;

    private TaskService service;

    private void initData() {
        final String serviceType = config().getString("service.type", "redis");
        LOGGER.info("Service Type: " + serviceType);
        switch (serviceType) {

            case "redis":
            default:
                RedisOptions config = new RedisOptions()
                        .setHost(config().getString("redis.host", "redis"))
                        .setPort(config().getInteger("redis.port", 6379));
                service = new RedisTaskService(config);
        }

        service.initData().setHandler(res -> {
            if (res.failed()) {
                LOGGER.error("Persistence service is not running!");
                res.cause().printStackTrace();
            }
        });
    }

    @Override
    public void start(Future<Void> future) throws Exception {
        HTTPRequestValidationHandler validationHandler = HTTPRequestValidationHandler.create()
                .addJsonBodySchema("{" +
                        "  \"type\": \"object\"," +
                        "  \"properties\": {" +
                        "    \"problem\": {" +
                                          "\"type\": \"object\"," +
                                          "  \"properties\": {" +
                                                     "    \"capacity\": { \"type\": \"number\", \"minimum\": 0 }," +
                                                     "    \"weights\": { \"type\": \"array\" }," +
                                                     "    \"values\": { \"type\": \"array\" }" +
                        " } }}\n" +
                        "}"
                );
        Router router = Router.router(vertx);
        // CORS support
        Set<String> allowHeaders = new HashSet<>();
        allowHeaders.add("x-requested-with");
        allowHeaders.add("Access-Control-Allow-Origin");
        allowHeaders.add("origin");
        allowHeaders.add("Content-Type");
        allowHeaders.add("accept");
        Set<HttpMethod> allowMethods = new HashSet<>();
        allowMethods.add(HttpMethod.GET);
        allowMethods.add(HttpMethod.POST);
        allowMethods.add(HttpMethod.DELETE);
        allowMethods.add(HttpMethod.PATCH);

        router.route().handler(BodyHandler.create());
        router.route().handler(CorsHandler.create("*")
                .allowedHeaders(allowHeaders)
                .allowedMethods(allowMethods));

        // routes
        router.get(Constants.API_GET).handler(this::handleGetTodo);
        router.get(Constants.API_LIST_ALL).handler(this::handleGetAll);
        router.get(Constants.API_GET_SOLUTION).handler(this::handleGetSolution);
        router.post(Constants.API_SHUTDOWN).handler((this::handleShutdown));
        router.post(Constants.API_CREATE)
                .handler(validationHandler)
                .handler(this::handleCreateTodo)
                .failureHandler((routingContext) -> {
            Throwable failure = routingContext.failure();
            if (failure instanceof ValidationException) {
                // Something went wrong during validation!
                String validationErrorMessage = failure.getMessage();
                final JsonObject error = new JsonObject()
                        .put("timestamp", System.nanoTime())
                        .put("exception", failure.getClass().getName())
                        .put("exceptionMessage", failure.getMessage())
                        .put("path", routingContext.request().path());

                routingContext.response().putHeader(HttpHeaders.CONTENT_TYPE,"application/json; charset=utf-8");
                routingContext.response().end(error.encode());

            }
        });
        router.patch(Constants.API_UPDATE).handler(this::handleUpdateTodo);
        router.delete(Constants.API_DELETE).handler(this::handleDeleteOne);
        router.delete(Constants.API_DELETE_ALL).handler(this::handleDeleteAll);

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(PORT, HOST, result -> {
                    if (result.succeeded())
                        future.complete();
                    else
                        future.fail(result.cause());
                });

        initData();
    }

    /**
     * Wrap the result handler with failure handler (503 Service Unavailable)
     */
    private <T> Handler<AsyncResult<T>> resultHandler(RoutingContext context, Consumer<T> consumer) {
        return res -> {
            if (res.succeeded()) {
                consumer.accept(res.result());
            } else {
                serviceUnavailable(context);
            }
        };
    }

    private void handleCreateTodo(RoutingContext context) {
        try {

            final Task todo = wrapObject(new Task(), context);

            final String encoded = Json.encodePrettily(todo);
            ProblemDTO problemDTO = new ProblemDTO(context.getBodyAsString());


            service.insert(todo).setHandler(resultHandler(context, res -> {
                if (res) {


                        todo.getTimestamps().put("started",Instant.now().toEpochMilli());
                        todo.setStatus(TaskStatus.started);
                        service.update(todo.getTask(), todo)
                                .setHandler(resultHandler(context, resUpd -> {
                                    if (resUpd == null)
                                        notFound(context);
                                    else {
                                        vertx.executeBlocking(future -> {
                                            future.complete(new ZeroOneKnapsack(problemDTO.getProblem().getCapacity(),problemDTO.getProblem().getWeights(),problemDTO.getProblem().getValues()).calcSolution());
                                        }, resp -> {
                                            if (resp.succeeded()) {
                                              LOGGER.info(problemDTO.getProblem());
                                                LOGGER.info(resp.result());




                                                todo.setStatus(TaskStatus.completed);

                                                todo.getTimestamps().put("completed", Instant.now().toEpochMilli());

                                                Solution solution=new Solution((List<Integer>) resp.result(),todo.getTimestamps().getLong("completed")-todo.getTimestamps().getLong("started"));

                                                solution.setItems((List<Integer>) resp.result());

                                                SolutionDTO solutionDTO= new SolutionDTO(solution, todo.getTask(), problemDTO.getProblem());

                                                service.update(todo.getTask(), todo)
                                                        .setHandler(resultHandler(context, restUpd -> {
                                                            if (restUpd == null)
                                                                notFound(context);
                                                            else {
                                                                service.insertSolution(solutionDTO);
                                                            }
                                                        }));
                                            }
                                        });

                                    }
                                }));


                    context.response()
                            .setStatusCode(201)
                            .putHeader("content-type", "application/json")
                            .end(encoded);
                } else {
                    serviceUnavailable(context);
                }
            }));
        } catch (DecodeException e) {
            sendError(400, context.response());
        }
    }

    private void handleGetTodo(RoutingContext context) {
        String todoID = context.request().getParam("todoId");
        if (todoID == null) {
            sendError(400, context.response());
            return;
        }

        service.getCertain(todoID).setHandler(resultHandler(context, res -> {
            if (!res.isPresent())
                notFound(context);
            else {
                final String encoded = Json.encodePrettily(res.get());
                context.response()
                        .putHeader("content-type", "application/json")
                        .end(encoded);
            }
        }));
    }

    private void handleGetSolution(RoutingContext context) {
        String taskId = context.request().getParam("taskId");
        if (taskId == null) {
            sendError(400, context.response());
            return;
        }

        service.getCertainSolution(taskId).setHandler(resultHandler(context, res -> {
            if (!res.isPresent())
                notFound(context);
            else {
                final String encoded = Json.encodePrettily(res.get());
                context.response()
                        .putHeader("content-type", "application/json")
                        .end(encoded);
            }
        }));
    }

    private void handleGetAll(RoutingContext context) {
        service.getAll().setHandler(resultHandler(context, res -> {
            if (res == null) {
                serviceUnavailable(context);
            } else {
                final String encoded = Json.encodePrettily(res);
                context.response()
                        .putHeader("content-type", "application/json")
                        .end(encoded);
            }
        }));
    }

    private void handleUpdateTodo(RoutingContext context) {
        try {
            String todoID = context.request().getParam("todoId");
            final Task newTodo = new Task(context.getBodyAsString());
            // handle error
            if (todoID == null) {
                sendError(400, context.response());
                return;
            }
            service.update(todoID, newTodo)
                    .setHandler(resultHandler(context, res -> {
                        if (res == null)
                            notFound(context);
                        else {
                            final String encoded = Json.encodePrettily(res);
                            context.response()
                                    .putHeader("content-type", "application/json")
                                    .end(encoded);
                        }
                    }));
        } catch (DecodeException e) {
            badRequest(context);
        }
    }

    private Handler<AsyncResult<Boolean>> deleteResultHandler(RoutingContext context) {
        return res -> {
            if (res.succeeded()) {
                if (res.result()) {
                    context.response().setStatusCode(204).end();
                } else {
                    serviceUnavailable(context);
                }
            } else {
                serviceUnavailable(context);
            }
        };
    }

    private void handleDeleteOne(RoutingContext context) {
        String todoID = context.request().getParam("todoId");
        service.delete(todoID)
                .setHandler(deleteResultHandler(context));
    }

    private void handleDeleteAll(RoutingContext context) {
        service.deleteAll()
                .setHandler(deleteResultHandler(context));
    }

    private void handleShutdown(RoutingContext context) {


                context.response()
                .putHeader("content-type", "application/json")
                .end("Service Shutting down...");

        context.vertx().close(r->{
            System.exit(0);});


    }

    private void sendError(int statusCode, HttpServerResponse response) {
        response.setStatusCode(statusCode).end();
    }

    private void badRequest(RoutingContext context) {
        context.response().setStatusCode(400).end();
    }

    private void notFound(RoutingContext context) {
        context.response().setStatusCode(404).end();
    }

    private void serviceUnavailable(RoutingContext context) {
        context.response().setStatusCode(503).end();
    }

    private Task wrapObject(Task todo, RoutingContext context) {
        todo.setTask(null);
        todo.setStatus(TaskStatus.submitted);

        JsonObject obj = new JsonObject();

        obj.put("submitted", Instant.now().toEpochMilli());
//        obj.put("started", ObjectUtils.NULL);
//        obj.put("completed", ObjectUtils.NULL);
        todo.setTimestamps(obj);

        return todo;
    }




}
