package com.moderathowinther.usermanagement.exception;

public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException(String msg){
        super(msg);
    }

}
