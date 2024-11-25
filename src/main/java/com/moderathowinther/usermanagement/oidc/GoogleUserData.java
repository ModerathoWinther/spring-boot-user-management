package com.moderathowinther.usermanagement.oidc;

import java.util.Map;

public class GoogleUserData {

    private final Map<String, Object> attributes;

    public GoogleUserData(Map<String, Object> attributes){
        this.attributes = attributes;
    }

    public String getId(){
        return (String) attributes.get("sub");
    }

    public String getName(){
        return (String) attributes.get("name");
    }

    public String getEmail(){
        return (String) attributes.get("email");
    }
}
