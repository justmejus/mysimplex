package xyz.simplex.entity;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@DataObject(generateConverter = true)
public class Solution {

    private static final AtomicInteger acc = new AtomicInteger(0); // counter

    private List<Integer> items;
    private long time;

    public Solution(List<Integer> items, long time) {
        this.items = items;
        this.time = time;
    }

    public static AtomicInteger getAcc() {
        return acc;
    }

    public List<Integer> getItems() {
        return items;
    }

    public Solution setItems(List<Integer> items) {
        this.items = items;
        return this;
    }

    public long getTime() {
        return time;
    }

    public Solution setTime(long time) {
        this.time = time;
        return this;
    }

    public Solution(JsonObject obj) {
        SolutionConverter.fromJson(obj, this);
    }

    public Solution(String jsonStr) {
        SolutionConverter.fromJson(new JsonObject(jsonStr), this);
    }


    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        SolutionConverter.toJson(this, json);
        return json;
    }



    private <T> T getOrElse(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }


}
