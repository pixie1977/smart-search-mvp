package ru.common.data.services;

import ru.common.data.dto.User;

import java.util.List;

public interface UserManagementService {
    User getUserData(String login);

    boolean saveUser(User user);

    List<User> getFriends(String login);

    List<User> getNotFriends(String login);

    int updateFriends(List<String> friendLogins, String userLogin);

    int removeFriend(String friendLogin, String login);
}
