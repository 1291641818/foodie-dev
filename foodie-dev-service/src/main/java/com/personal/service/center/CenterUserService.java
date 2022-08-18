package com.personal.service.center;

import com.personal.pojo.Users;
import com.personal.pojo.bo.center.CenterUserBO;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author aXuan
 * @version V1.0
 * @title
 * @description
 * @date 2022-06-30 21:09
 */
public interface CenterUserService {
    /**
     * 根据用户id查询用户信息
     *
     * @param userId
     * @return
     */
    Users queryUserInfo(String userId);

    /**
     * 修改用户信息
     *
     * @param userId
     * @param centerUserBO
     */
    Users updateUserInfo(String userId, CenterUserBO centerUserBO);

    /**
     * 修改用户头像信息
     * @param userId
     * @param faceUrl
     * @return
     */
    Users updateUserFace(String userId, String faceUrl);
}
