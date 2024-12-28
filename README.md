# ⏯️ kafka-replay - WIP
Have you ever needed to manually produce to a kafka topic so you could test your
event-driven application?  
<br>
**kafka-replay** is here to help with mocking responses to messages you publish 
to a messaging system such as Apache kafka.   

## Requirements
- Maven (wrapper provided)
- Java 21
- Kafka
- Spring Boot 3.4.1

# 🏗️ How to build

> mvn clean install

# 🏃‍♂️‍➡️ How to run

As of now you can use 2 types of persistence, in-memory and filesystem.
To use either you need to provide the property `mock.persistence.type` which expects one
of the following values `MEMORY` or `FILESYSTEM`.

### Filesystem
When using `FILESYSTEM` the property `mock.persistence.folder` is also expected.
<br>
Inside this folder you should place your `Fixture` json objects which look like the following example.

<details>
  <summary>Example </summary>

  ```json
    {
      "origin": "origin.topic",
      "destination": "destination.topic",
      "mappings": [
        {
          "type": "CONSTANT",
          "tag": "greeting",
          "replacement": "Hello world!"
        }
      ],
      "conditions": [
        {
          "type": "EQUAL",
          "jsonPath": "$.request",
          "value": "greet"
        }
      ],
      "response": "{\"greeting\": \"{greeting}\"}"
    }
  ```
</details>

> mvn spring-boot:run -Dspring-boot.run.profiles=local

### In-Memory

Check `NoDBFixtureRepo` for an example on how to use.

# 🏁 WIP - Coming up...
- Documentation on existing `Condition` and `Mapping` objects
- Overall abstraction (messaging system, persistence, etc.)
- Improve Condition matchers such as `GTEMatcher` 