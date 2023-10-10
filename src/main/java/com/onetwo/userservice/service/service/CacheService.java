package com.onetwo.userservice.service.service;

import com.onetwo.userservice.entity.redis.RefreshToken;

import java.util.Optional;

public interface CacheService {
    void saveRefreshToken(RefreshToken token);

    Optional<RefreshToken> findRefreshTokenById(Long uuid);

    Optional<RefreshToken> findRefreshTokenByAccessToken(String accessToken);
}
