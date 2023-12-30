package IoTp.controllers;

import IoTp.model.SensorData;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/sensordata")
public class SensorDataController {

    @Produce("direct:sensorDataInput")
    private final ProducerTemplate producerTemplate;

    public SensorDataController(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    @PostMapping("/send")
    public void sendMessage(@RequestBody SensorData data) {
        producerTemplate.sendBody(data);
    }
}
