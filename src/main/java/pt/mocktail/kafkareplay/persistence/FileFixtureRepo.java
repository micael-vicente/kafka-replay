package pt.mocktail.kafkareplay.persistence;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Slf4j
@Repository
@ConditionalOnProperty(prefix = "mock", name = "persistence", havingValue = "FILESYSTEM")
public class FileFixtureRepo implements FixtureRepository {

    public FileFixtureRepo() {
        log.info("Using the filesystem as persistence mechanism");
    }

    @Override
    public Collection<Fixture> getAllByOrigin(String origin) {
        return List.of();
    }
}
