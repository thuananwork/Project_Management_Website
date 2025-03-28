package com.manager.controller.admin;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "dyq9afi16", // Thay bằng cloud name của bạn
            "api_key", "558973558839412",       // Thay bằng API key của bạn
            "api_secret", "IxxzipA2USHGDuSGisOBhPyspEM" // Thay bằng API secret của bạn
        ));
    }
}
