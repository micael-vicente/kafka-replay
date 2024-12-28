package pt.mocktail.kafkareplay.service;

import pt.mocktail.kafkareplay.persistence.FixtureMapping;
import pt.mocktail.kafkareplay.persistence.MappingType;

public interface Mapper {

    /**
     * The qualifier of the mapper.
     *
     * @return the {@link MappingType} being mapped
     */
    MappingType mappingType();

    /**
     * Applies a mapping to the response template.
     *
     * @param originMessage the original message received from origin
     * @param responseTemplate the template of the response
     * @param mapping the details of the mapping
     * @return the template post tag being replaced by mapping
     */
    String applyMapping(String originMessage, String responseTemplate, FixtureMapping mapping);
}
