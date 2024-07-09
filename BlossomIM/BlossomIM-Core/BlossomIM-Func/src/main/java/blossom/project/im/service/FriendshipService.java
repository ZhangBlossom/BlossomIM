package blossom.project.im.service;

import blossom.project.im.Friendship;
import blossom.project.im.enums.YesOrNo;
import blossom.project.im.vo.ContactsVO;

import java.util.List;

public interface FriendshipService {

    /**
     * 获得朋友关系
     * @param myId
     * @param friendId
     * @return
     */
    public Friendship getFriendship(String myId, String friendId);

    /**
     * 查询我的好友列表(通讯录)
     * @param myId
     * @return
     */
    public List<ContactsVO> queryMyFriends(String myId, boolean needBlack);

    /**
     * 修改我的好友的备注名
     * @param myId
     * @param friendId
     * @param friendRemark
     */
    public void updateFriendRemark(String myId,
                                   String friendId,
                                   String friendRemark);

    /**
     * 拉黑或者恢复好友
     * @param myId
     * @param friendId
     * @param yesOrNo
     */
    public void updateBlackList(String myId,
                                String friendId,
                                YesOrNo yesOrNo);

    /**
     * 删除好友(删除好友之间的两个记录)
     * @param myId
     * @param friendId
     */
    public void delete(String myId, String friendId);

    /**
     * 判断两个朋友之前的关系是否拉黑
     * @param friendId1st
     * @param friendId2nd
     */
    public boolean isBlackEachOther(String friendId1st, String friendId2nd);
}
