package cn.org.july.shiro;

import cn.org.july.spring.ShiroApplication;
import cn.org.july.spring.entity.User;
import cn.org.july.spring.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ShiroApplication.class)
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void testFindUserByName() {
        User user = userService.findUserByName("WHJ");
        System.out.println(user.getUserName());
    }

    @Test
    public void testAll() {
        List<User> list = userService.selectAll();
        System.out.println(list.size());
    }

}
