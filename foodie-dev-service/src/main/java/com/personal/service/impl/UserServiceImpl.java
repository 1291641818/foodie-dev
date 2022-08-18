package com.personal.service.impl;

import com.personal.service.UserService;
import com.personal.enums.Sex;
import com.personal.mapper.UsersMapper;
import com.personal.pojo.Users;
import com.personal.pojo.bo.UserBO;
import com.personal.utils.DateUtil;
import com.personal.utils.MD5Utils;
import org.n3r.idworker.Sid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;

/**
 * @author aXuan
 * @version V1.0
 * @title
 * @description
 * @date 2022-06-21 00:03
 *
 */
@Service

public class UserServiceImpl implements UserService {
    private static final String USER_FACE = "file:///C:/Users/Shinelon/Desktop/new.jpg";

    @Resource
    UsersMapper usersMapper;

    @Resource
    private Sid sid;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean queryUsernameIsExist(String username) {
        Example userExample = new Example(Users.class);
        userExample.createCriteria().andEqualTo("username", username);
        Users result = usersMapper.selectOneByExample(userExample);
        return result == null ? false : true;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Users createUser(UserBO userBO) {
        Users user = new Users();
        user.setId(sid.nextShort());
        user.setUsername(userBO.getUsername());
        try {
            user.setPassword(MD5Utils.getMD5Str(userBO.getPassword()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        user.setNickname(userBO.getUsername());
        user.setFace(USER_FACE);
        user.setBirthday(DateUtil.stringToDate("1998-01-01"));
        user.setSex(Sex.SECRET.getCode());
        usersMapper.insertSelective(user);


        return user;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Users queryUserForLogin(String username, String password) {
        Example userExample = new Example(Users.class);
        userExample.createCriteria()
                .andEqualTo("username", username)
                .andEqualTo("password", password);
        Users result = usersMapper.selectOneByExample(userExample);
        return result;
    }
}
