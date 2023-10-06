package com.onetwo.userservice.controller.response;

import java.time.Instant;

public record UserDetailResponse(String userId,
                                 Instant birth,
                                 String nickname,
                                 String name,
                                 String email,
                                 String phoneNumber,
                                 boolean state) {
}
