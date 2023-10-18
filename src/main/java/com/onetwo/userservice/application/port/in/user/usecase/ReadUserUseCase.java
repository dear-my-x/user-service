package com.onetwo.userservice.application.port.in.user.usecase;

import com.onetwo.userservice.application.service.response.UserIdExistCheckDto;
import com.onetwo.userservice.application.service.response.UserResponseDto;

public interface ReadUserUseCase {

    UserIdExistCheckDto userIdExistCheck(String userId);

    UserResponseDto getUserDetailInfo(String userId);
}