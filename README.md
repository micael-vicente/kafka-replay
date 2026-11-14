# ⏯️ kafka-replay - Kafka Mock Engine

A Spring Boot mock engine for Kafka that listens on topics, matches incoming messages against declarative fixtures, and
publishes canned responses to destination topics. This is a development/testing aid that allows you to test event-driven
applications without hitting real services.

## 📖 What This Project Is

kafka-replay is a **mocking engine** for Apache Kafka that enables you to:

- Produce messages to Kafka topics
- Have the system match those messages against configured fixtures
- Receive canned downstream responses instead of hitting real services
- Test your event-driven applications in isolation

## 🏗️ Requirements

- Java 25
- Gradle (wrapper provided)
- Apache Kafka
- Spring Boot 4.1.2
- Jackson, json-path, Lombok

## 🛠️ How to Build

```bash
./gradlew build
```

## ▶️ How to Run

The application supports two persistence modes:

### Filesystem Persistence (Default - local profile)

```bash
./gradlew bootRun --args=\"--spring.profiles.active=local\"
```

This uses the FILESYSTEM persistence mode, which reads fixture files from `src/main/resources/repo/fixtures/`.

### In-Memory Persistence

To use in-memory persistence, set the property `mock.persistence.type` to `MEMORY`:

```bash
./gradlew bootRun --args="--mock.persistence.type=MEMORY"
```

## 📁 Fixture Files

Fixtures are defined as JSON files that describe how messages should be matched and what responses should be generated.

### Example Fixture

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

### Fixture Structure

- **origin**: The Kafka topic to listen on
- **destination**: The Kafka topic to send the mock response to
- **mappings**: How to map values from the input message to the output response
- **conditions**: When to apply this fixture (matching rules)
- **response**: Template for the response message with placeholders

### Mapping Types

- `CONSTANT`: Replace placeholder with a literal value
- `JSON_PATH`: Extract a value from the input message using JSONPath
- `UUID`: Generate a random UUID
- `CURR_TIMESTAMP`: Insert current timestamp
- `FOR_EACH`: Repeat a block for each element in an array

### Condition Types

- `EQUAL`: Match exactly
- `NOT_EQUAL`: Don't match exactly
- `ONE_OF`: Match any value from a set
- `NOT_ONE_OF`: Don't match any value from a set
- `GREATER_THAN_EQUAL_TO`: Greater than or equal to (note: currently broken)

## 🏗️ Architecture

```
MockerKafkaConsumer (@KafkaListener on ${mock.topics}, concurrency 3)
  └─ build Event { origin, message, envelopedMessage, fieldContainingMessage }
       │
EventRouter.route("BASE_HANDLER", event)          # hardcodes handler name
       │
BaseEventHandler.handleEvent(event)
  1. repo.getAllByOrigin(origin)  -> Collection<Fixture>
  2. for each fixture: handleFixture()
       a. message = unwrap envelope if enveloped (JsonPath read of fieldContainingMessage)
       b. envelope conditions must ALL match (Condition::isOnEnvelope) or skip
       c. inner conditions must ALL match, else skip (no response)
       d. apply mappings to response template -> producer.sendMessage(destination, response)
```

## 📚 Domain Model

### Fixture

- `origin`: Source topic
- `destination`: Destination topic
- `mappings`: How to transform the input message
- `conditions`: Matching rules for when to use this fixture
- `response`: Template for output message

### Condition

- `type`: Type of matching (EQUAL, NOT_EQUAL, etc.)
- `jsonPath`: JSONPath expression to extract value from message
- `value`: Single value to match against
- `values`: Set of values to match against (for ONE_OF, NOT_ONE_OF)
- `onEnvelope`: Whether to check the envelope or inner message

### FixtureMapping

- `type`: Mapping type (CONSTANT, JSON_PATH, etc.)
- `tag`: Placeholder name in response template
- `replacement`: Value to replace placeholder with
- `innerBlock`: Block to repeat for FOR_EACH mappings
- `innerMappings`: Mappings to apply inside the inner block

## 🧪 Testing

The project includes comprehensive tests covering:

- Base event handling
- Mapping strategies (constant, JSON path, UUID, timestamp, foreach)
- Condition matching (equals, not equals, one of, not one of, greater than equal)

## ⚠️ Known Issues / TODOs

- **GTEMatcher is broken**: ignores `condition.getValue()` and just checks the attribute is a non-empty past timestamp.
  Fix to actually compare ≥ the value.
- **NotOneOfMatcher looks like a bug**: it compares the attribute to a single `condition.getValue()` instead of against
  `condition.getValues()` like `OneOfMatcher` does. Verify intended semantics.
- **Inconsistent injection**: `BaseEventHandler` uses constructor injection; `For EachMapper` uses `@Setter` field
  injection — inconsistent. Prefer constructor injection everywhere.
- **Tests are shallow**: The riskiest code (mapping + condition evaluation) is the least covered.

## 📝 Conventions

- Package: `pt.mocktail.kafkareplay`
- Inject dependencies via constructors; classes are `@RequiredArgsConstructor`
- String-based Kafka (String key/value, StringDeserializer/Serializer)
- Keep new matchers/mappers as small `@Component`s implementing the interfaces and registered through the existing
  `*Strategy` registries — don't bypass them

## 📝 License

This project is licensed under the Apache 2.0 License - see the [LICENSE](LICENSE) file for details.

## 🤝 Contributing

Contributions are welcome! Please read the [AGENTS.md](AGENTS.md) file for development guidelines and contribution
information.