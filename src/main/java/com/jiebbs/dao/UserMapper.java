package com.jiebbs.dao;

import com.jiebbs.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUsername(@Param("username") String username);

    int checkEmail(@Param("email") String email);

    User checkLogin(@Param("username") String username,@Param("password") String password);

    String selectForgetQuestion(@Param("username") String username);

    int checkForgetAnswer(@Param("username")String username,@Param("question")String question,@Param("answer")String answer);

    int updatePasswordByUsername(@Param("username") String username,@Param("password") String password);
}