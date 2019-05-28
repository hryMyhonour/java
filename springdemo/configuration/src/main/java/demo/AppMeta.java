package demo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("app.meta")
@Data
public class AppMeta {
    private int version;
    private int subVersion;
    private List<String> testList;
}
