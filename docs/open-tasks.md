# Open Tasks

## 1. Testing
- **Condition matchers** – add table‑driven tests for each matcher (`Equals`, `NotEquals`, `OneOf`, `NotOneOf`, `GTE`).
- **Mapping strategies** – add tests that assert the exact output of each mapper, especially `ForEachMapper` (repeated blocks, index substitution).
- Verify edge cases (empty values, wrong types, missing fields, etc.).

## 2. Matcher Fixes
- **GTEMatcher** – implement proper ≥ comparison instead of the current placeholder logic.
- **NotOneOfMatcher** – update to iterate over `condition.getValues()` and negate the result.

## 3. Dependency Injection
- Replace `@Setter` on `ForEachMapper` with constructor injection (`@RequiredArgsConstructor`).
- Add `@Lazy` on the `MappingStrategy` field in `ForEachMapper` (or on the `Set<JsonMapper>` in `MappingStrategy`) to break the circular dependency.

## 4. Additional Matchers
- Implement the planned `GT`, `LT`, and `LTE` matchers following the existing pattern.

## 5. Event Router Refactor
- Create a registry of `EventHandler`s keyed by qualifier.
- Update `EventRouter` to look up handlers from this registry.
- Update `MockerKafkaConsumer` to request the correct handler based on profile/qualifier.

## 6. Documentation
- Add a runnable example fixture to `src/main/resources/repo/fixtures/`.
- Update README.md and AGENTS.md to reflect:
  - Gradle build/run commands.
  - Updated Spring Boot/Java versions.
  - Handler registry explanation.
  - Any new testing or configuration details.

## 7. Miscellaneous
- Ensure the `docs/open-tasks.md` file itself is kept up‑to‑date as tasks are added or completed.
- Review code for any remaining inconsistent injection patterns.
- Consider adding a CI workflow to run tests on every PR.
