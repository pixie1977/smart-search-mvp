package ru.common.data.dto;

import ru.common.data.dto.User;
import ru.common.data.dto.UserOperationCode;

import java.io.Serializable;

public class UserOperationMessage implements Serializable {
    private UserOperationCode code;
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserOperationCode getCode() {

        return code;
    }

    public void setCode(UserOperationCode code) {
        this.code = code;
    }
}
