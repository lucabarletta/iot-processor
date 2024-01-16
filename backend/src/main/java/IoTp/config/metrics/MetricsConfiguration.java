package IoTp.config.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.google.common.collect.Lists;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import jakarta.annotation.PostConstruct;
import org.apache.camel.component.micrometer.MicrometerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class MetricsConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger("iot-metrics");
    private static final MetricRegistry metrics = new MetricRegistry();

    @PostConstruct
    public void init() {
        final Slf4jReporter reporter = Slf4jReporter.forRegistry(metrics)
                .outputTo(LOGGER)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(1, TimeUnit.MINUTES);
    }

    @Bean
    public MetricRegistry getMetricRegistry() {
        return metrics;
    }

    @Bean("compositeMeter")
    public MeterRegistry compositeMeterRegistry(PrometheusMeterRegistry prometheusMeterRegistry) {
        return new CompositeMeterRegistry(Clock.SYSTEM, Lists.newArrayList(prometheusMeterRegistry));
    }

    @Bean(MicrometerConstants.METRICS_REGISTRY_NAME)
    public MeterRegistry meterRegistry(@Qualifier("compositeMeter") MeterRegistry meterRegistry) {
        return meterRegistry;
    }
}
