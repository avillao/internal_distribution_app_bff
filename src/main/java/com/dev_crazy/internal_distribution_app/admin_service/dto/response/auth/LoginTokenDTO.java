package com.dev_crazy.internal_distribution_app.admin_service.dto.response.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginTokenDTO {
    private String access_token;
    private int expires_in;
    private String token_type;
    private String refresh_token;
    private int refresh_expires_in;
}
