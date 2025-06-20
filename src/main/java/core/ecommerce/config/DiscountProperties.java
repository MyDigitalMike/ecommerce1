package core.ecommerce.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
@ConfigurationProperties(prefix = "discount")
@Getter
@Setter
@RequiredArgsConstructor
public class DiscountProperties {
    private LocalDateTime timeStart;
    private LocalDateTime timeEnd;
    private boolean randomEnabled;
}
