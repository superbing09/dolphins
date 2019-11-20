package com.shakethetree.configuration.mapper;

import com.shakethetree.configuration.dto.User;
import org.apache.ibatis.annotations.*;

import java.util.*;

/**
 * @author xubing
 * @description //TODO 设计说明
 * @date 19-10-26
 * @copyright 中网易企秀
 */
@Mapper
public interface UserMapper {


    @Results({
            @Result(property = "uid", column = "uid"),
            @Result(property = "openid", column = "openid"),
            @Result(property = "name", column = "name"),
            @Result(property = "td", column = "td")
    })
    @Select("select * from user")
    List<User> findUser();

    @Insert("insert into user(uid,openid,name) values(#{uid},#{openid}, #{name}) on duplicate key update name = #{name}")
    void insert(User user);

    @Delete("delete from user where openid=#{openid}")
    void delete(@Param("openid") String openid);

    @Update("update user set td = #{td}")
    void update(boolean td);

}
