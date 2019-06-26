package configclient.demo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "test", ignoreInvalidFields = true)
public class Config {
    private String testInfo;
}
