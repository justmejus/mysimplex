package xyz.simplex.entity;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@DataObject(generateConverter = true)
public class SolutionDTO {

    private static final AtomicInteger acc = new AtomicInteger(0); // counter

    private Solution solution;
    private String task;

    public SolutionDTO(Solution solution, String task, Problem problem) {
        this.solution = solution;
        this.task = task;
        this.problem = problem;
    }

    public Solution getSolution() {
        return solution;
    }

    public SolutionDTO setSolution(Solution solution) {
        this.solution = solution;
        return this;
    }

    public String getTask() {
        return task;
    }

    public SolutionDTO setTask(String task) {
        this.task = task;
        return this;
    }

    public Problem getProblem() {
        return problem;
    }

    public SolutionDTO setProblem(Problem problem) {
        this.problem = problem;
        return this;
    }

    private Problem problem;




    public static AtomicInteger getAcc() {
        return acc;
    }

    public SolutionDTO(JsonObject obj) {
        SolutionDTOConverter.fromJson(obj, this);
    }

    public SolutionDTO(String jsonStr) {
        SolutionDTOConverter.fromJson(new JsonObject(jsonStr), this);
    }


    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        SolutionDTOConverter.toJson(this, json);
        return json;
    }



    private <T> T getOrElse(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }


}
