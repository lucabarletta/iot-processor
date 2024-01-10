package IoTp.model;


import java.time.Instant;
import java.util.Objects;

public class SensorDataDTO {
    public double value;
    public String sensorId;
    public String customerId;
    public Instant time;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SensorDataDTO that = (SensorDataDTO) o;
        return Double.compare(value, that.value) == 0 && Objects.equals(sensorId, that.sensorId) && Objects.equals(customerId, that.customerId) && Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, sensorId, customerId, time);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SensorDataDTO{");
        sb.append("value=").append(value);
        sb.append(", sensorId='").append(sensorId).append('\'');
        sb.append(", customerId='").append(customerId).append('\'');
        sb.append(", time=").append(time);
        sb.append('}');
        return sb.toString();
    }
}
