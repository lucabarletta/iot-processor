package IoTp.controller;

import IoTp.model.SensorData;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/sensordata")
public class SensorDataController {

    @Produce("direct:sensorDataInput")
    private final ProducerTemplate producerTemplate;

    public SensorDataController(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    @PostMapping("/send")
    public void sendMessage(@RequestBody List<SensorData> data) {
        data.forEach(producerTemplate::sendBody);
    }
}
