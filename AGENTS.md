# AGENTS.md — kafka-replay

Working notes for anyone building or editing this repo. Read `README.md` for the
user-facing overview; this file is for contributors (including AI agents).

## What this project is

A Spring Boot **mock engine for Kafka**. It listens on a set of topics, matches
each incoming message against declarative **fixtures**, and — when a condition
matches — publishes a templated **mock response** to a destination topic. It is a
development/testing aid: you produce to a topic and get canned downstream
responses instead of hitting real services.

- Java 25 · Spring Boot 4.1.2 · spring-kafka · Jackson · json-path · Lombok
- Apache 2.0
- Currently **WIP** (single handler, no real "replay"/time-travel).

## Build & run

```bash
./gradlew build
./gradlew bootRun --args="--spring.profiles.active=local"   # uses FILESYSTEM persistence
```

Config lives in `src/main/resources/application.yml` and
`src/main/resources/application-local.yml` (prefix `mock.*`).

### Persistence toggle (`mock.persistence.type`)

Exactly one of these two `@ConditionalOnProperty` beans is active:

| type | bean | notes |
|------|------|-------|
| `MEMORY` | `NoDBFixtureRepo` | hardcoded `List<Fixture>` in code |
| `FILESYSTEM` | `FileFixtureRepo` | walks `mock.persistence.folder`, parses each `.json` as a `Fixture` |

## Architecture & request flow

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

`EventRouter` keys handlers by `EventHandler.getQualifier()`. The consumer always
routes to `"BASE_HANDLER"`.

## Domain model (`persistence/`)

- `Fixture` — one `origin` → `destination`, `conditions`, `mappings`, `response` template.
- `Condition { type, jsonPath, value, values, onEnvelope }`.
- `FixtureMapping { type, tag, replacement, innerBlock, innerMappings }`.
- `ConditionType`: `ONE_OF, NOT_EQUAL, EQUAL, NOT_ONE_OF, GREATER_THAN_EQUAL_TO`.
- `MappingType`: `CONSTANT, JSON_PATH, UUID, FOR_EACH, CURR_TIMESTAMP`.

## Condition matchers (`service/json/condition/`)

`MatcherStrategy` builds an `EnumMap<ConditionType, JsonConditionMatcher>` from a
`Set<JsonConditionMatcher>` (constructor-injected). All extend `JsonConditionMatcher`
which reads the attribute via `JsonPath.parse(json).read(jsonPath, String.class)`.
Implement `conditionType()` + `meetsCondition(Condition, String)`.

Matchers present: `Equals`, `NotEquals`, `OneOf`, `NotOneOf`, `GTE`.
Planned (not implemented): `GT, LT, LTE`.

## Mappers (`service/json/mapping/`)

`MappingStrategy` builds an `EnumMap<MappingType, JsonMapper>` from a
`Set<JsonMapper>` (constructor-injected). All implement `JsonMapper`
(extends `Mapper`), which has `mappingType()` +
`applyMapping(String originMessage, String responseTemplate, FixtureMapping)`.

Each mapper replaces the placeholder `{<tag>}` in `responseTemplate`:
- `CONSTANT` → literal `replacement`
- `JSON_PATH` → value read from `originMessage` via `replacement` as a JsonPath
- `UUID` → `UUID.randomUUID()`
- `CURR_TIMESTAMP` → `Instant.now()`
- `FOR_EACH` → repeats an `innerBlock` once per element of an array in
  `originMessage` (count via `replacement + ".length()"`), applying
  `innerMappings` with index substituted as `[i]`

## Fixture JSON format (`resources/repo/fixtures/*.json`)

```json
{
  "origin": "origin.topic",
  "destination": "destination.topic",
  "mappings": [ { "type": "CONSTANT", "tag": "greeting", "replacement": "Hello world!" } ],
  "conditions": [ { "type": "EQUAL", "jsonPath": "$.request", "value": "greet" } ],
  "response": "{\"greeting\": \"{greeting}\"}"
}
```

`example-fixture.json` is a **format example only** — nothing loads it by default
(`NoDBFixtureRepo` is hardcoded; `FileFixtureRepo` only under `FILESYSTEM`).

## Known issues / TODOs

- **`GTEMatcher` is broken**: ignores `condition.getValue()` and just checks the
  attribute is a non-empty past timestamp. Fix to actually compare ≥ the value.
- **`NotOneOfMatcher` looks like a bug**: it compares the attribute to a single
  `condition.getValue()` instead of against `condition.getValues()` like
  `OneOfMatcher` does. Verify intended semantics.
- **`BaseEventHandler` uses constructor injection; `For EachMapper` uses `@Setter`
  field injection** — inconsistent. Prefer constructor injection everywhere.
- **Tests are shallow**: `BaseEventHandlerTest` mocks every collaborator (asserts
  "no interactions" only). Mapper tests mostly assert non-empty output. The
  riskiest code (mapping + condition evaluation) is the least covered.

## Conventions

- Package: `pt.mocktail.kafkareplay`.
- Inject dependencies via constructors; classes are `@RequiredArgsConstructor`.
- String-based Kafka (String key/value, StringDeserializer/Serializer).
- Keep new matchers/mappers as small `@Component`s implementing the interfaces and
  registered through the existing `*Strategy` registries — don't bypass them.
