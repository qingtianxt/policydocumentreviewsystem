package stdu.wzw.test;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("jodconverter")
public class JodConverterProperties {
    private boolean enabled;
    private String officeHome;
    private String portNumbers = "2002";
    private String workingDir;
    private String templateProfileDir;
    private boolean killExistingProcess = true;
    private long processTimeout = 120000L;
    private long processRetryInterval = 250L;
    private long taskExecutionTimeout = 120000L;
    private int maxTasksPerProcess = 200;
    private long taskQueueTimeout = 30000L;

}
