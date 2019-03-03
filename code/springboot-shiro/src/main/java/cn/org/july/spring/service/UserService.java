package cn.org.july.spring.service;

import cn.org.july.spring.dao.UserMapper;
import cn.org.july.spring.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User findUserByName(String userName) {
        return userMapper.findUserByName(userName);
    }

    public List<User> selectAll() {
        return userMapper.selectAll();
    }

}
