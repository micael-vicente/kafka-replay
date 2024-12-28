package pt.mocktail.kafkareplay.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import pt.mocktail.kafkareplay.persistence.exception.FileDBInitException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Repository
@ConditionalOnProperty(prefix = "mock.persistence", name = "type", havingValue = "FILESYSTEM")
public class FileFixtureRepo implements FixtureRepository {

    private final Map<String, List<Fixture>> fixturesByOrigin;

    public FileFixtureRepo(@Value("${mock.persistence.folder}") String folder) {
        ObjectMapper mapper = new ObjectMapper();
        fixturesByOrigin = new HashMap<>();

        Path current = null;
        try (Stream<Path> paths = Files.walk(Paths.get(folder))) {
            for(Path path : paths.filter(Files::isRegularFile).toList()) {
                current = path;
                String fileContent = Files.readString(path);
                Fixture fixture = mapper.readValue(fileContent, Fixture.class);

                List<Fixture> fixtures = Optional.ofNullable(fixturesByOrigin.get(fixture.getOrigin()))
                    .orElse(new ArrayList<>());

                fixtures.add(fixture);
                fixturesByOrigin.put(fixture.getOrigin(), fixtures);
            }
        } catch (IOException e) {
            String message = Optional.ofNullable(current)
                    .map(path -> "Failed reading fixture. Filename: " + path.getFileName())
                    .orElse("Failed to read fixtures from filesystem");
            throw new FileDBInitException(message, e);
        }

        log.info("Using the filesystem as persistence mechanism");
    }

    @Override
    public Collection<Fixture> getAllByOrigin(String origin) {
        return fixturesByOrigin.get(origin);
    }
}
