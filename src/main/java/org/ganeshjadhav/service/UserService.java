package org.ganeshjadhav.service;

import org.ganeshjadhav.exception.NotFoundException;
import org.ganeshjadhav.model.User;
import org.ganeshjadhav.storage.impl.UserStoreImpl;

public class UserService {

    private final UserStoreImpl userStore;


    private final WalletService walletService;

    public UserService(UserStoreImpl userStore, WalletService walletService) {
        this.userStore = userStore;
        this.walletService = walletService;
    }

    public User create(String userName, String email, String phone){
        User user = User.build(userName, email, phone);
        User savedUser = userStore.put(user);
        walletService.create(user.getId(), "INR");
        return savedUser;
    }

    public User get(String userId){
        User user = userStore.get(userId);
        if(user == null){
            throw new NotFoundException("User does not exist");
        }
        return user;
    }
}
