package blossom.project.im.service;

import com.baomidou.mybatisplus.extension.service.IService;
import blossom.project.pojo.Users;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author 风间影月
 * @since 2024-03-27
 */
public interface UsersService {

    /**
     * 判断用户是否存在，如果存在则返回用户信息，否则null
     * @param mobile
     * @return
     */
    public Users queryMobileIfExist(String mobile);

    /**
     * 创建用户信息，并且返回用户对象
     * @param mobile
     * @return
     */
    public Users createUsers(String mobile, String nickname);

}
