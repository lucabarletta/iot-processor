package IoTp.services;

import IoTp.model.SensorData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.camel.tooling.model.Strings;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SensorDataMapper {

    public SensorDataMapper() {
    }

    public Optional<SensorData> parse(String json) {
        if (Strings.isNullOrEmpty(json)) {
            return Optional.empty();
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            return Optional.ofNullable(objectMapper.readValue(json, SensorData.class));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }
}
