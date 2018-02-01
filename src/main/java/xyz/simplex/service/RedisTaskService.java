package xyz.simplex.service;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;
import xyz.simplex.Constants;
import xyz.simplex.entity.Solution;
import xyz.simplex.entity.SolutionDTO;
import xyz.simplex.entity.Task;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RedisTaskService implements TaskService {

    private final Vertx vertx;
    private final RedisOptions config;
    private final RedisClient redis;

    public RedisTaskService() {
        this(new RedisOptions());
    }

    public RedisTaskService(RedisOptions config) {
        this.vertx = Vertx.vertx();
        this.config = config;
        this.redis = RedisClient.create(vertx, config);
    }

    @Override
    public Future<Boolean> initData() {
//        return insert(new Task(Math.abs(new java.util.Random().nextInt()),
//                "Something to do...", false, 1, "todo/ex"));
        deleteAll();
        return Future.future();
    }

    @Override
    public Future<Boolean> insert(Task todo) {
        System.out.println("entrato");
        Future<Boolean> result = Future.future();
        final String encoded = Json.encodePrettily(todo);
        redis.hset(Constants.REDIS_TASK_KEY, String.valueOf(todo.getTask()),
                encoded, res -> {
                    if (res.succeeded())
                        result.complete(true);
                    else
                        result.fail(res.cause());
                });
        return result;
    }

    @Override
    public Future<Boolean> insertSolution(SolutionDTO solutionDTO) {
        Future<Boolean> result = Future.future();
        final String encoded = Json.encodePrettily(solutionDTO);
        redis.hset(Constants.REDIS_SOLUTION_KEY, String.valueOf(solutionDTO.getTask()),
                encoded, res -> {
                    if (res.succeeded())
                        result.complete(true);
                    else
                        result.fail(res.cause());
                });
        return result;
    }
    @Override
    public Future<List<Task>> getAll() {
        Future<List<Task>> result = Future.future();
        redis.hvals(Constants.REDIS_TASK_KEY, res -> {
            if (res.succeeded()) {
                result.complete(res.result()
                        .stream()
                        .map(x -> new Task((String) x))
                        .collect(Collectors.toList()));
            } else
                result.fail(res.cause());
        });
        return result;
    }

    @Override
    public Future<Optional<Task>> getCertain(String todoID) {
        Future<Optional<Task>> result = Future.future();
        redis.hget(Constants.REDIS_TASK_KEY, todoID, res -> {
            if (res.succeeded()) {
                result.complete(Optional.ofNullable(
                        res.result() == null ? null : new Task(res.result())));
            } else
                result.fail(res.cause());
        });
        return result;
    }

    @Override
    public Future<Optional<SolutionDTO>> getCertainSolution(String todoID) {
        Future<Optional<SolutionDTO>> result = Future.future();
        redis.hget(Constants.REDIS_SOLUTION_KEY, todoID, res -> {
            if (res.succeeded()) {
                result.complete(Optional.ofNullable(
                        res.result() == null ? null : new SolutionDTO(res.result())));
            } else
                result.fail(res.cause());
        });
        return result;
    }

    @Override
    public Future<Task> update(String todoId, Task newTask) {
        return this.getCertain(todoId).compose(old -> {
            if (old.isPresent()) {
                Task fnTodo = old.get().merge(newTask);
                return this.insert(fnTodo)
                        .map(r -> r ? fnTodo : null);
            } else {
                return Future.succeededFuture();
            }
        });
    }

    @Override
    public Future<Boolean> delete(String todoId) {
        Future<Boolean> result = Future.future();
        redis.hdel(Constants.REDIS_TASK_KEY, todoId, res -> {
            if (res.succeeded())
                result.complete(true);
            else
                result.complete(false);
        });
        return result;
    }

    @Override
    public Future<Boolean> deleteAll() {
        Future<Boolean> result = Future.future();
        redis.del(Constants.REDIS_TASK_KEY, res -> {
            if (res.succeeded())
                result.complete(true);
            else
                result.complete(false);
        });
        return result;
    }
}
