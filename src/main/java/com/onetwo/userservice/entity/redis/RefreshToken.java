package com.onetwo.userservice.entity.redis;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash(value = "refresh", timeToLive = 604800)
public class RefreshToken {

    @Id
    private Long uuid;
    @Indexed
    private String accessToken;
    private String refreshToken;

    private RefreshToken(Long uuid, String accessToken, String refreshToken) {
        this.uuid = uuid;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static RefreshToken createRefreshTokenEntity(Long uuid, String accessToken, String refreshToken) {
        return new RefreshToken(uuid, accessToken, refreshToken);
    }

    public void setReissueAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
