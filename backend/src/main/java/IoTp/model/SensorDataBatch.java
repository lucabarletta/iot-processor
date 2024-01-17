package IoTp.model;

import java.util.ArrayList;
import java.util.List;

public class SensorDataBatch {
    private List<SensorData> items = new ArrayList<>();

    public SensorDataBatch(List<SensorData> items) {
        this.items = items;
    }

    public SensorDataBatch() {
    }

    public void additem(SensorData data) {
        items.add(data);
    }

    public List<SensorData> getItems() {
        return items;
    }

    public int getSize() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void reset() {
        items = new ArrayList<>();
    }
}
