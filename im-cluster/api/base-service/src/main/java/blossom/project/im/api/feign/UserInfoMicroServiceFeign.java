package blossom.project.im.api.feign;

import blossom.project.im.grace.result.GraceJSONResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Auther 风间影月
 */
@FeignClient(value = "main-service")
public interface UserInfoMicroServiceFeign {

    @PostMapping("/userinfo/updateFace")
    public GraceJSONResult updateFace(@RequestParam("userId") String userId,
                                      @RequestParam("face") String face);

    @PostMapping("/userinfo/updateFriendCircleBg")
    public GraceJSONResult updateFriendCircleBg(
            @RequestParam("userId") String userId,
            @RequestParam("friendCircleBg") String friendCircleBg);

    @PostMapping("/userinfo/updateChatBg")
    public GraceJSONResult updateChatBg(
            @RequestParam("userId") String userId,
            @RequestParam("chatBg") String chatBg);
}
