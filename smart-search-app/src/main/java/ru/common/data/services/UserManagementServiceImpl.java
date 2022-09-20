package ru.common.data.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.common.data.dto.User;
import ru.common.data.repo.UserRepository;


import java.util.List;

@Service()
public class UserManagementServiceImpl implements UserManagementService {

    private final UserRepository repository;

    @Autowired
    public UserManagementServiceImpl(UserRepository repository) {
        this.repository = repository;
    }


    public User getUserData(String login) {
        User user = this.repository.findByLogin(login);
        return user;
    }

    public boolean saveUser(User user) {
        User userFromDB = null;

        try {
            userFromDB = repository.findByLogin(user.getLogin());
        } catch (Exception e) {
            System.out.println("User " + user.getLogin() + " not found");
        }

        if (userFromDB != null) {
            return false;
        }

        user.setId(User.idGenerator());
        user.setRoles("ROLE_USER");
        user.setPassword(User.PASSWORD_ENCODER.encode(user.getPassword()));
        repository.save(user);
        return true;
    }

    @Override
    public List<User> getFriends(String login) {
        List<User> users = this.repository.findFriendsByLogin(login);
        return users;
    }

    @Override
    public List<User> getNotFriends(String login) {
        List<User> users = this.repository.findNotFriendsByLogin(login);
        return users;
    }

    @Override
    public int updateFriends(List<String> friendLogins, String userLogin) {
        return this.repository.updateFriends(friendLogins, userLogin);
    }

    @Override
    public int removeFriend(String friendLogin, String login) {
        return this.repository.removeFriend(friendLogin, login);
    }
}
