package demo;

import demo.mapper.UserMapper;
import demo.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@MapperScan("demo.mapper")
public class AppTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void testCURD(){
        User user = new User();
        user.setPassword("111");
        user.setUsername("SNOW");
        int id = userMapper.insert(user);
        assertNotEquals(0, id);

        User retrieve = userMapper.selectByUsername("SNOW");
        assertNotNull(retrieve);
        assertEquals(Integer.valueOf(id), retrieve.getId());

        int deleteR = userMapper.deleteByPrimaryKey(id);
        assertNotEquals(0, deleteR);

        User afterDelete = userMapper.selectByUsername("SNOW");
        assertNull(afterDelete);
    }
}
