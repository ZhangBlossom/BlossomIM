package blossom.project.im.tasks;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import blossom.project.utils.SMSUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @Auther 风间影月
 */
@Component
@Slf4j
public class SMSTask {

    @Resource
    private SMSUtils smsUtils;

    @Async
    public void sendSMSInTask(String mobile, String code) throws Exception {
        // smsUtils.sendSMS(mobile, code);
        log.info("异步任务中所发送的验证码为：{}", code);
    }

}
