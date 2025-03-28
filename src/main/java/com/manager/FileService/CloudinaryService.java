package com.manager.FileService;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryService() {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dyq9afi16", // Thay bằng cloud name của bạn
                "api_key", "558973558839412",       // Thay bằng API key của bạn
                "api_secret", "IxxzipA2USHGDuSGisOBhPyspEM" // Thay bằng API secret của bạn
        ));
    }

    public String uploadFile(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
            "resource_type", "auto" // Hỗ trợ cả hình ảnh và tệp raw
        ));
        return uploadResult.get("url").toString();
    }
}
