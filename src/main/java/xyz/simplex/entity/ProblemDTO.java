package xyz.simplex.entity;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.concurrent.atomic.AtomicInteger;

@DataObject(generateConverter = true)
public class ProblemDTO {


    private Problem problem;

    public ProblemDTO(Problem problem) {
        this.problem = problem;

    }

    public Problem getProblem() {
        return problem;
    }

    public ProblemDTO setProblem(Problem problem) {
        this.problem = problem;
        return this;
    }

    public ProblemDTO(JsonObject obj) {
        ProblemDTOConverter.fromJson(obj, this);
    }

    public ProblemDTO(String jsonStr) {
        ProblemDTOConverter.fromJson(new JsonObject(jsonStr), this);
    }


    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        ProblemDTOConverter.toJson(this, json);
        return json;
    }



    private <T> T getOrElse(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }


}
