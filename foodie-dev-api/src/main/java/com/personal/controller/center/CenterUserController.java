package com.personal.controller.center;

import com.personal.controller.BaseController;
import com.personal.pojo.Users;
import com.personal.pojo.bo.center.CenterUserBO;
import com.personal.pojo.vo.UsersVO;
import com.personal.resource.FileUpload;
import com.personal.service.center.CenterUserService;
import com.personal.utils.CookieUtils;
import com.personal.utils.DateUtil;
import com.personal.utils.JSONResult;
import com.personal.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.personal.utils.DateUtil.DATE_PATTERN;

@Api(value = "用户信息接口", tags = {"用户信息相关接口"})
@Slf4j
@RestController
@RequestMapping("userInfo")
public class CenterUserController extends BaseController {

    @Resource
    private CenterUserService centerUserService;

    @Resource
    FileUpload fileUpload;

    @ApiOperation(value = "用户头像修改", notes = "用户头像修改", httpMethod = "POST")
    @PostMapping("uploadFace")
    public JSONResult uploadFace(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "file", value = "用户头像", required = true)
            @RequestParam MultipartFile file,
            HttpServletRequest request, HttpServletResponse response) {

        //1.图片保存的路径
        //String fileSpace = IMG_USER_FACE_PATH;
        String fileSpace = fileUpload.getImgUserFacePath();
        //2.在路径上为每一个用户增加一个userId，用于区分不同用户上传
        String uploadPathPrefix = File.separator + userId;

        //3.判空
        if (file == null) {
            return JSONResult.errorMsg("文件不能为空");
        }

        FileOutputStream fileOutputStream = null;
        String newFileName = "";
        try {
            //4.文件上传
            String fileName = file.getOriginalFilename();
            if (StringUtils.isNotBlank(fileName)) {

                //获取文件名和后缀名 face.png -> ["face","png"]
                String[] fileNameArray = fileName.split("\\.");

                //获取文件的后缀名
                String suffix = fileNameArray[fileNameArray.length - 1];

                //限制文件上传格式,防止后门
                if (!suffix.equalsIgnoreCase("png") &&
                        !suffix.equalsIgnoreCase("jpg") &&
                        !suffix.equalsIgnoreCase("jpeg")) {
                    return JSONResult.errorMsg("图片格式不正确");
                }

                //文件名重组，覆盖式上传  如果是增量式上传的话，可以在userId后面加上当前的年月日时分秒
                newFileName = "face-" + userId + "." + suffix;

                //上传文件最终保存的位置
                String finalFacePath = fileSpace + uploadPathPrefix + File.separator + newFileName;
                //用于提供给web服务访问的web地址
                //uploadPathPrefix += "/" + newFileName;

                File outFile = new File(finalFacePath);
                if (outFile.getParentFile() != null) {
                    //创建父级目录
                    outFile.getParentFile().mkdirs();
                }

                //输出文件保存到指定目录
                fileOutputStream = new FileOutputStream(outFile);
                InputStream inputStream = file.getInputStream();
                IOUtils.copy(inputStream, fileOutputStream);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }

        //获得图片服务地址
        String imgServerUrl = fileUpload.getImgServerUrl();

        //由于浏览器可能存在缓存的情况,所以在这里我们要加上时间戳来保证更新后的图片及时刷新
        String finalUserFaceUrl = imgServerUrl + uploadPathPrefix + "/" + newFileName + "?t="
                + DateUtil.getCurrentDateString(DATE_PATTERN);
        //更新用户头像到数据库
        Users users = centerUserService.updateUserFace(userId, finalUserFaceUrl);

        UsersVO usersVO = convert2UsersVO(users);

        //刷新cookie
        CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(usersVO), true);
        return JSONResult.ok();
    }

    @ApiOperation(value = "修改用户信息", notes = "修改用户信息", httpMethod = "POST")
    @PostMapping("update")
    public JSONResult update(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @RequestBody @Valid CenterUserBO centerUserBO,
            BindingResult boundResult,
            HttpServletRequest request, HttpServletResponse response) {

        //1. 判断BindingResult是否保存错误的验证信息，如果有，则直接return
        if (boundResult.hasErrors()) {
            Map<String, String> errorsMap = getErrors(boundResult);
            return JSONResult.errorMap(errorsMap);
        }
        //2. 更新数据，返回更新的对象
        Users userResult = centerUserService.updateUserInfo(userId, centerUserBO);

        UsersVO usersVO = convert2UsersVO(userResult);
        //3. 将多余数据置空，并存入cookie
        CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(usersVO), true);

        return JSONResult.ok();
    }

    /**
     * 将错误信息返回为一个map
     *
     * @param boundResult
     * @return
     */
    private Map<String, String> getErrors(BindingResult boundResult) {
        List<FieldError> fieldErrors = boundResult.getFieldErrors();
        Map<String, String> map = new HashMap<>();
        fieldErrors.stream().forEach(e -> map.put(e.getField(), e.getDefaultMessage()));
        return map;
    }

    private Users setNullProperty(Users userResult) {
        userResult.setPassword(null);
        userResult.setMobile(null);
        userResult.setEmail(null);
        userResult.setCreatedTime(null);
        userResult.setUpdatedTime(null);
        userResult.setBirthday(null);
        return userResult;
    }

}
