package IoTp.model;

import java.util.ArrayList;
import java.util.List;

public class SensorDataList {
    private List<SensorData> items = new ArrayList<>();

    public SensorDataList(List<SensorData> items) {
        this.items = items;
    }

    public SensorDataList() {
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

    public void reset() {
        items = new ArrayList<>();
    }
}
