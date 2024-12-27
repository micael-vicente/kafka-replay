package pt.mocktail.kafkareplay.service;

import pt.mocktail.kafkareplay.persistence.FixtureMapping;
import pt.mocktail.kafkareplay.persistence.MappingType;

public interface Mapper {

    MappingType mappingType();

    String applyMapping(String originMessage, String responseTemplate, FixtureMapping mapping);
}
