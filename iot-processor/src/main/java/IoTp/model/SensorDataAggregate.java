package IoTp.model;


import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;

import java.time.Instant;
import java.util.Objects;

@Measurement(name = "aggregate")
public class SensorDataAggregate {

    @Column(timestamp = true)
    private Instant time;

    @Column(tag = true)
    private String sensorId;

    @Column
    private double min;

    @Column
    private double max;

    @Column
    private double mean;

    @Column
    private double standardDeviation;

    @Column
    private double median;

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

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMean() {
        return mean;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }

    public void setStandardDeviation(double standardDeviation) {
        this.standardDeviation = standardDeviation;
    }

    public double getMedian() {
        return median;
    }

    public void setMedian(double median) {
        this.median = median;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AggregatedSensorData{");
        sb.append("time=").append(time);
        sb.append(", sensorId='").append(sensorId).append('\'');
        sb.append(", min=").append(min);
        sb.append(", max=").append(max);
        sb.append(", mean=").append(mean);
        sb.append(", standardDeviation=").append(standardDeviation);
        sb.append(", median=").append(median);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SensorDataAggregate that = (SensorDataAggregate) o;
        return Double.compare(min, that.min) == 0 && Double.compare(max, that.max) == 0 && Double.compare(mean, that.mean) == 0 && Double.compare(standardDeviation, that.standardDeviation) == 0 && Double.compare(median, that.median) == 0 && Objects.equals(time, that.time) && Objects.equals(sensorId, that.sensorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, sensorId, min, max, mean, standardDeviation, median);
    }
}
