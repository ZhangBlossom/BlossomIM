package blossom.project.im.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Auther 风间影月
 */
@FeignClient(value = "file-service")
public interface FileMicroServiceFeign {

    @PostMapping("/file/generatorQrCode")
    public String generatorQrCode(@RequestParam("wechatNumber") String wechatNumber,
                                  @RequestParam("userId") String userId) throws Exception;

}
