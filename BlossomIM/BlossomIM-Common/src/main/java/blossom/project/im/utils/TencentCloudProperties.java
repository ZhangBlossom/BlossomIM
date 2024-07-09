package blossom.project.im.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @Auther ZhangBlossom
 */
@Component
@Data
@PropertySource("classpath:tencentCloud.properties")
@ConfigurationProperties(prefix = "tencent.cloud")
public class TencentCloudProperties {

    private String SecretId;
    private String SecretKey;

}
