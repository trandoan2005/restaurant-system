package com.example.restaurant_system.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class MomoConfig {
    
    @Value("${momo.partner-code:MOMO}")
    private String partnerCode;
    
    @Value("${momo.access-key:F8BBA842ECF85}")
    private String accessKey;
    
    @Value("${momo.secret-key:K951B6PE1waDMi640xX08PD3vg6EkVlz}")
    private String secretKey;
    
    @Value("${momo.endpoint:https://test-payment.momo.vn}")
    private String endpoint;
    
    @Value("${momo.return-url:https://webhook.site/b3088a6a-2d17-4f8d-a383-71389a6c600b}")
    private String returnUrl;
    
    @Value("${momo.ipn-url:https://webhook.site/b3088a6a-2d17-4f8d-a383-71389a6c600b}")
    private String ipnUrl;
}