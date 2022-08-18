package com.personal.convertor;

import com.personal.pojo.Users;
import com.personal.pojo.vo.UsersVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author aXuan
 * @version V1.0
 * @title
 * @description
 * @date 2022-08-18 21:47
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UsersConvertor {
    UsersConvertor INSTANCE = Mappers.getMapper(UsersConvertor.class);

    /**
     * po转换为vo
     * @param users
     */
    UsersVO po2Vo(Users users);

    /**
     * poList转换为voList
     * @param usersList
     */
    List<UsersVO> poList2VoList(List<Users> usersList);
}
