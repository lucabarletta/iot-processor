import IoTp.model.SensorData;
import IoTp.services.SensorDataMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class SensorDataMapperTest {

    private SensorDataMapper sensorDataMapper;

    @BeforeEach
    public void setUp() {
        sensorDataMapper = new SensorDataMapper();
    }

    @Test
    public void whenParseValidJson_thenOptionalContainsSensorDataDTO() {
        String validJson = "{\n" +
                "  \"value\": 111.11,\n" +
                "  \"sensorId\": \"sensor1\",\n" +
                "  \"customerId\": \"customer1\",\n" +
                "  \"time\": \"2019-01-21T05:47:26.853Z\"\n" +
                "}\n";

        var result = sensorDataMapper.parse(validJson.trim());
        var expected = new SensorData();
        expected.setSensorId("sensor1");
        expected.setCustomerId("customer1");
        expected.setTime(Instant.parse("2019-01-21T05:47:26.853Z"));
        expected.setValue(111.11);
        assertEquals(Optional.of(expected), result);
    }

    @Test
    public void whenParseInvalidJson_thenOptionalIsEmpty() {
        String invalidJson = "invalid json";
        Optional<SensorData> result = sensorDataMapper.parse(invalidJson);
        assertFalse(result.isPresent());
    }

    @Test
    public void whenParseNullJson_thenOptionalIsEmpty() {
        String nullJson = null;
        Optional<SensorData> result = sensorDataMapper.parse(nullJson);
        assertFalse(result.isPresent());
    }

    @Test
    public void whenParseEmptyJson_thenOptionalIsEmpty() {
        String emptyJson = "";
        Optional<SensorData> result = sensorDataMapper.parse(emptyJson);
        assertFalse(result.isPresent());
    }
}
