package xyz.simplex.entity;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@DataObject(generateConverter = true)
public class Problem {

    private static final AtomicInteger acc = new AtomicInteger(0); // counter

    protected List<Integer> weights  = new ArrayList<Integer>();
    protected List<Integer> values  = new ArrayList<Integer>();

    protected int capacity        = 0;

    public Problem(List<Integer> weights, List<Integer> values, int capacity) {
        this.weights = weights;
        this.values = values;
        this.capacity = capacity;
    }

    public Problem() {
    }

    public List<Integer> getWeights() {
        return weights;
    }

    public Problem setWeights(List<Integer> weights) {
        this.weights = weights;
        return this;
    }

    public List<Integer> getValues() {
        return values;
    }

    public Problem setValues(List<Integer> values) {
        this.values = values;
        return this;
    }

    public int getCapacity() {
        return capacity;
    }

    public Problem setCapacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public Problem(JsonObject obj) {
        ProblemConverter.fromJson(obj, this);
    }

    public Problem(String jsonStr) {
        ProblemConverter.fromJson(new JsonObject(jsonStr), this);
    }


    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        ProblemConverter.toJson(this, json);
        return json;
    }


    private <T> T getOrElse(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }

    public Problem merge(Problem knapsack) {
        return new Problem(

                getOrElse(knapsack.weights, weights),
                getOrElse(knapsack.values, values),
                getOrElse(knapsack.capacity, capacity)
                );
    }

    @Override
    public String toString() {
        return "Problem{" +
                "weights=" + weights +
                ", values=" + values +
                ", capacity=" + capacity +
                '}';
    }
}
