package IoTp.services;

import IoTp.model.SensorData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.camel.tooling.model.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SensorDataMapper {
    private static final Logger Log = LoggerFactory.getLogger(SensorDataMapper.class);

    public SensorDataMapper() {
    }

    public Optional<SensorData> parse(String json) {
        if (Strings.isNullOrEmpty(json)) {
            return Optional.empty();
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule()); // Register the module for Java 8 date/time
            return Optional.ofNullable(objectMapper.readValue(json, SensorData.class));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }
}
