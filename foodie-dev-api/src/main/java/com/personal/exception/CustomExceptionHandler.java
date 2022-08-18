package com.personal.exception;

import com.personal.utils.JSONResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * @author aXuan
 * @version V1.0
 * @title
 * @description
 * @date 2022-07-02 07:50
 */
@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

    /**
     * 捕获上传文件大小超过500k的异常
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public JSONResult handleMaxUploadFile(MaxUploadSizeExceededException e){
        log.error(e.getMessage());
        return JSONResult.errorMsg("文件上传大小不能超过500k");
    }
}
