package blossom.project.im.service.impl;

import blossom.project.im.Users;
import blossom.project.im.base.BaseInfoProperties;
import blossom.project.im.bo.ModifyUserBO;
import blossom.project.im.exceptions.GraceException;
import blossom.project.im.grace.result.ResponseStatusEnum;
import blossom.project.im.mapper.UsersMapper;
import blossom.project.im.service.UsersService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import blossom.project.im.api.feign.FileMicroServiceFeign;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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

    @Transactional
    @Override
    public void modifyUserInfo(ModifyUserBO userBO) {

        Users pendingUser = new Users();

        String wechatNum = userBO.getWechatNum();
        String userId = userBO.getUserId();

        if (StringUtils.isBlank(userId)) {
            GraceException.display(ResponseStatusEnum.USER_INFO_UPDATED_ERROR);
        }

        if (StringUtils.isNotBlank(wechatNum)) {
            String isExist = redis.get(REDIS_USER_ALREADY_UPDATE_WECHAT_NUM + ":" + userId);
            if (StringUtils.isNotBlank(isExist)) {
                GraceException.display(ResponseStatusEnum.WECHAT_NUM_ALREADY_MODIFIED_ERROR);
            } else {
                // 修改微信二维码
                String wechatNumUrl = getQrCodeUrl(wechatNum, userId);
                pendingUser.setWechatNumImg(wechatNumUrl);
            }
        }

        pendingUser.setId(userId);
        pendingUser.setUpdatedTime(LocalDateTime.now());

        BeanUtils.copyProperties(userBO, pendingUser);

        usersMapper.updateById(pendingUser);

        // 如果用户修改微信号，则只能修改一次，放入redis中进行判断
        if (StringUtils.isNotBlank(wechatNum)) {
            redis.setByDays(REDIS_USER_ALREADY_UPDATE_WECHAT_NUM + ":" + userId,
                            userId,
                            365);
        }
    }

    @Override
    public Users getById(String userId) {
        return usersMapper.selectById(userId);
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

    @Override
    public Users getByWechatNumOrMobile(String queryString) {

        QueryWrapper queryWrapper = new QueryWrapper<Users>()
                .eq("wechat_num", queryString)
                .or()
                .eq("mobile", queryString);

        Users friend = usersMapper.selectOne(queryWrapper);

        return friend;
    }
}
