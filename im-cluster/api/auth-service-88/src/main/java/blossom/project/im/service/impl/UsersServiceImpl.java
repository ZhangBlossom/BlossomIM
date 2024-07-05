package blossom.project.im.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import blossom.project.im.api.feign.FileMicroServiceFeign;
import blossom.project.base.BaseInfoProperties;
import blossom.project.enums.Sex;
import blossom.project.im.mapper.UsersMapper;
import blossom.project.pojo.Users;
import blossom.project.im.service.UsersService;
import blossom.project.utils.DesensitizationUtil;
import blossom.project.utils.LocalDateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author 风间影月
 * @since 2024-03-27
 */
@Service
public class UsersServiceImpl extends BaseInfoProperties implements UsersService {

    @Resource
    private UsersMapper usersMapper;

    private static final String USER_FACE1 = "http://127.0.0.1:9000/itzixi/face/1749619640390205441/e9f2be46-56ba-454c-a7ee-3e290bad6a59.jpg";

    @Override
    public Users queryMobileIfExist(String mobile) {
        return usersMapper.selectOne(
                new QueryWrapper<Users>()
                        .eq("mobile", mobile)
        );
    }

    @Transactional
    @Override
    public Users createUsers(String mobile, String nickname) {

        Users user = new Users();

        user.setMobile(mobile);

        String uuid = UUID.randomUUID().toString();
        String uuidStr[] = uuid.split("-");
        String wechatNum = "wx" + uuidStr[0] + uuidStr[1];
        user.setWechatNum(wechatNum);

        String wechatNumUrl = getQrCodeUrl(wechatNum, TEMP_STRING);
        user.setWechatNumImg(wechatNumUrl);

        // 用户138****1234
        // DesensitizationUtil
        if (StringUtils.isBlank(nickname)) {
            user.setNickname("用户" + DesensitizationUtil.commonDisplay(mobile));
        }
        user.setRealName("");

        user.setSex(Sex.secret.type);
        user.setFace(USER_FACE1);
        user.setFriendCircleBg(USER_FACE1);
        user.setEmail("");

        user.setBirthday(LocalDateUtils
                .parseLocalDate("1980-01-01",
                                LocalDateUtils.DATE_PATTERN));

        user.setCountry("中国");
        user.setProvince("");
        user.setCity("");
        user.setDistrict("");

        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());

        usersMapper.insert(user);

        return user;
    }

    @Resource
    private FileMicroServiceFeign fileMicroServiceFeign;

    private String getQrCodeUrl(String wechatNumber, String userId) {
        try {
            return fileMicroServiceFeign.generatorQrCode(wechatNumber, userId);
        } catch (Exception e) {
            // throw new RuntimeException(e);
            return null;
        }
    }
}
