package demo.mapper;

import demo.model.User;

public interface UserMapper {
    int insert(User user);

    int insertSelective(User user);

    User selectByUsername(String username);

    int deleteByPrimaryKey(Integer id);
}
