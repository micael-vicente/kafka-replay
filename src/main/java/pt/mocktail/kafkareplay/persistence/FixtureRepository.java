package pt.mocktail.kafkareplay.persistence;

import java.util.Collection;

public interface FixtureRepository {
    Collection<Fixture>  getAllByOrigin(String origin);
}
