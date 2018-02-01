package xyz.simplex.entity;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

@DataObject(generateConverter = true)
public class Task {

    private static final AtomicInteger acc = new AtomicInteger(100000); // counter


    private String task;
    private TaskStatus status;
    private JsonObject timestamps;

    public Task() {
    }

    public Task(String task, TaskStatus status, JsonObject timestamps) {
        this.task = task;
        this.status = status;
        this.timestamps = timestamps;
    }

    public Task(JsonObject obj) {
        TaskConverter.fromJson(obj, this);
    }

    public Task(String jsonStr) {
        TaskConverter.fromJson(new JsonObject(jsonStr), this);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        TaskConverter.toJson(this, json);
        return json;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public Task setStatus(TaskStatus status) {
        this.status = status;
        return this;
    }

    public JsonObject getTimestamps() {
        return timestamps;
    }

    public Task setTimestamps(JsonObject timestamps) {
        this.timestamps = timestamps;
        return this;
    }

    public void setTask(String task) {

        this.task = getOrElse(task, Integer.toHexString(acc.incrementAndGet()));
    }

    public static int getIncId() {
        return acc.get();
    }

    public static void setIncIdWith(int n) {
        acc.set(n);
    }

    public String getTask() {
        return task;
    }



    private <T> T getOrElse(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }

    public Task merge(Task todo) {
        return new Task(task,
                getOrElse(todo.status, status),
                getOrElse(todo.timestamps, timestamps)
                );
    }
}
