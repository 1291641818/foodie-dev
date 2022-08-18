package com.personal.resource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author aXuan
 * @version V1.0
 * @title
 * @description
 * @date 2022-07-01 21:28
 */
@Component
@ConfigurationProperties(prefix = "file")
@PropertySource("classpath:file-upload-prod.properties")
public class FileUpload {

    private String imgUserFacePath;

    private String imgServerUrl;

    public String getImgUserFacePath() {
        return imgUserFacePath;
    }

    public void setImgUserFacePath(String imgUserFacePath) {
        this.imgUserFacePath = imgUserFacePath;
    }

    public String getImgServerUrl() {
        return imgServerUrl;
    }

    public void setImgServerUrl(String imgServerUrl) {
        this.imgServerUrl = imgServerUrl;
    }
}
