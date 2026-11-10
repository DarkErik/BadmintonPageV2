package de.dondevelops.badminton_backend.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Converter
public class StringListConverter
        implements AttributeConverter<List<String>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<String> value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JacksonException e) {
            throw new IllegalArgumentException("Could not serialize roles", e);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String value) {
        try {
            return objectMapper.readValue(
                    value,
                    new TypeReference<List<String>>() {}
            );
        } catch (JacksonException e) {
            throw new IllegalArgumentException("Could not deserialize roles", e);
        }
    }
}