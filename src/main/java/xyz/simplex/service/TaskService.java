package xyz.simplex.service;


import io.vertx.core.Future;
import xyz.simplex.entity.SolutionDTO;
import xyz.simplex.entity.Task;

import java.util.List;
import java.util.Optional;

public interface TaskService {

    Future<Boolean> initData(); // init the data (or table)

    Future<Boolean> insert(Task todo);

    Future<Boolean> insertSolution(SolutionDTO todo);

    Future<List<Task>> getAll();

    Future<Optional<Task>> getCertain(String todoID);

    Future<Optional<SolutionDTO>> getCertainSolution(String todoID);

    Future<Task> update(String todoId, Task newTodo);

    Future<Boolean> delete(String todoId);

    Future<Boolean> deleteAll();

}

