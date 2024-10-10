// LoginResponse.java

package com.medigator.medigator.service;


import com.medigator.medigator.dto.MemberDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private boolean success;
    private String message;
    private MemberDTO memberDTO;

    public LoginResponse(boolean success, String message, MemberDTO memberDTO) {
        this.success = success;
        this.message = message;
        this.memberDTO = memberDTO;
    }
}

