package com.project.authentification.service;


import com.project.authentification.model.User;
import com.project.authentification.payload.request.UpdateUserRequest;

import java.util.List;

public interface IUserService {

    /*User updateExistUser(UpdateUserRequest user);

    User submitUserToDb(UserRequest user, User user1);

    void deleteUser(Long id);*/

    List<User> listUser();
    void deleteUser(Long id);

    void resetPassword(String email,String password);



    void updateUser(UpdateUserRequest updateUserRequest);

    User findUserByUsername(String username);
}
