package blossom.project.im.service;


import blossom.project.im.Users;
import blossom.project.im.bo.ModifyUserBO;

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
     * 修改用户基本信息
     * @param userBO
     */
    public void modifyUserInfo(ModifyUserBO userBO);

    /**
     * 获得用户信息
     * @param userId
     * @return
     */
    public Users getById(String userId);

    /**
     * 根据微信号（账号）或者手机号精确匹配
     * @param queryString
     * @return
     */
    public Users getByWechatNumOrMobile(String queryString);

}
