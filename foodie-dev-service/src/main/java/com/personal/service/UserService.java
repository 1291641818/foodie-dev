package com.personal.service;

import com.personal.pojo.Users;
import com.personal.pojo.bo.UserBO;
import org.apache.ibatis.annotations.Select;

/**
 * @author aXuan
 * @version V1.0
 * @title
 * @description
 * @date 2022-06-21 00:02
 *
 */
public interface UserService {
    boolean queryUsernameIsExist(String username);

    Users createUser(UserBO userBO);

    Users queryUserForLogin(String username, String password);
}
