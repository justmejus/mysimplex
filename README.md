# KNAPSACK #

A Knapsack Optimization Problem solution based on Vert.x impemented in Java language. 
The RESTful API uses Vert.x web toolkit.
The persistence layer uses Redis data grid.
The Knapsack problem instance is encoded in json format:
```json
# problem
{
    "problem": {
        "capacity": # non-negative integer
        "weights": # array of non-negative integers
        "values": # array of non-negative integers, as many as weights
    }
}
```

### Build and Run ###
Docker Engine and Docker Compose are supposed to be installed and available in PATH.

```bash
$ cd <root directory of the service>
$ gradlew build
$ gradlew composeUp
```
To kill docker containers:
```bash
$ docker-compose down
```
Two docker container will be running (one executes Redis, the second executes the java application).

### Usage ###

```
$ curl -XPOST -H 'Content-type: application/json' http://localhost:8082/knapsack/tasks \
   -d '{"problem": {"capacity": 60, "weights": [10, 20, 33], "values": [10, 3, 30]}}'
{"task": "nbd43jhb", "status": "submitted", "timestamps": {"submitted": 1505225308, "started": null, "completed": null}}


$ curl -XGET -H http://localhost:8082/knapsack/tasks/nbd43jhb
{"task": "nbd43jhb", "status": "submitted", "timestamps": {"submitted": 1505225308, "started": 1505225320, "completed": null}}


$ curl -XGET -H http://localhost:8082/knapsack/solutions/nbd43jhb
# http status code 404 Not Found


$ curl -XGET -H http://localhost:8082/knapsack/tasks/nbd43jhb
{"task": "nbd43jhb", "status": "submitted", "timestamps": {"submitted": 1505225308, "started": 1505225320, "completed": 1505225521}}


$ curl -XGET -H http://localhost:8082/knapsack/solutions/nbd43jhb
{"task": "nbd43jhb", "problem": {...}, "solution": {"items": [0, 2]}, "time": 201}


$ curl -XGET http://localhost:8082/knapsack/admin/tasks
{"tasks": {"submitted": [...], "started": [...], "completed": [...]}}


$ curl -XPOST http://localhost:8082/knapsack/admin/shutdown
Service shutting down...
```

