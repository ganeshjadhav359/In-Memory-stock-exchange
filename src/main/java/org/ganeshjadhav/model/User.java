package org.ganeshjadhav.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.ganeshjadhav.utils.Common;

@Data
@Builder
@ToString
public class User {
    private String id;
    private String username;
    private String email;
    private String phone;

    public static User build(String username, String email, String phone){
        return User.builder()
                .id(Common.getUUID())
                .username(username)
                .email(email)
                .phone(phone)
                .build();
    }
}
