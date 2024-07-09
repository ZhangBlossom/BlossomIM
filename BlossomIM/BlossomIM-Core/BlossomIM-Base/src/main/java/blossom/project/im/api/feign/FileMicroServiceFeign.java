package blossom.project.im.api.feign;


/**
 * @Auther ZhangBlossom
 */
@FeignClient(value = "file-service")
public interface FileMicroServiceFeign {

    @PostMapping("/file/generatorQrCode")
    public String generatorQrCode(@RequestParam("wechatNumber") String wechatNumber,
                                  @RequestParam("userId") String userId) throws Exception;

}
