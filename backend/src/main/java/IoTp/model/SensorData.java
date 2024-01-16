package IoTp.model;


import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;

import java.time.Instant;
import java.util.Objects;

@Measurement(name = "data")
public class SensorData {

    @Column(timestamp = true)
    private Instant time;

    @Column(tag = true)
    private String sensorId;

    @Column(tag = true)
    private String customerId;

    @Column
    private double value;

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SensorData{");
        sb.append("time=").append(time);
        sb.append(", sensorId='").append(sensorId).append('\'');
        sb.append(", customerId='").append(customerId).append('\'');
        sb.append(", value=").append(value);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SensorData that = (SensorData) o;
        return Double.compare(value, that.value) == 0 && Objects.equals(time, that.time) && Objects.equals(sensorId, that.sensorId) && Objects.equals(customerId, that.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, sensorId, customerId, value);
    }
}
