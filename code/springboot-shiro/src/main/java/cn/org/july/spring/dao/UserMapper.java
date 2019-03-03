package cn.org.july.spring.dao;

import cn.org.july.spring.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM USER WHERE USERNAME = #{userName}")
    User findUserByName(@Param("userName") String userName);

    @Select("SELECT * FROM USER")
    List<User> selectAll();

}
