package blossom.project.im.controller;

import blossom.project.im.MinIOConfig;
import blossom.project.im.MinIOUtils;
import blossom.project.im.exceptions.GraceException;
import blossom.project.im.grace.result.GraceJSONResult;
import blossom.project.im.grace.result.ResponseStatusEnum;
import blossom.project.im.utils.JcodecVideoUtil;
import blossom.project.im.utils.JsonUtils;
import blossom.project.im.utils.QrCodeUtils;
import blossom.project.im.vo.UsersVO;
import blossom.project.im.vo.VideoMsgVO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import blossom.project.im.api.feign.UserInfoMicroServiceFeign;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Auther 风间影月
 */
@RestController
@RequestMapping("file")
public class FileController {

    @PostMapping("uploadFace1")
    public GraceJSONResult uploadFace1(@RequestParam("file") MultipartFile file,
                                       String userId,
                                       HttpServletRequest request) throws Exception {

        // abc.123.456.png
        String filename = file.getOriginalFilename();   // 获得文件原始名称
        String suffixName = filename.substring(filename.lastIndexOf("."));  // 从最后一个.开始截取
        String newFileName = userId + suffixName;   // 文件的新名称

        // 设置文件存储路径，可以存放到任意的指定路径
        String rootPath = "/Volumes/lee/workspaces/temp" + File.separator; // 上传文件的存放位置

        String filePath = rootPath + File.separator + "face" + File.separator + newFileName;
        File newFile = new File(filePath);
        // 判断目标文件所在目录是否存在
        if (!newFile.getParentFile().exists()) {
            // 如果目标文件所在目录不存在，则创建父级目录
            newFile.getParentFile().mkdirs();
        }

        // 将内存中的数据写入磁盘
        file.transferTo(newFile);

        return GraceJSONResult.ok();
    }

    @Resource
    private MinIOConfig minIOConfig;
    @Resource
    private UserInfoMicroServiceFeign userInfoMicroServiceFeign;

    @PostMapping("uploadFace")
    public GraceJSONResult uploadFace(@RequestParam("file") MultipartFile file,
                                       String userId,
                                       HttpServletRequest request) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        String filename = file.getOriginalFilename();   // 获得文件原始名称
        if (StringUtils.isBlank(filename)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        filename = "face" + File.separator + userId + File.separator + filename;
        MinIOUtils.uploadFile(minIOConfig.getBucketName(),
                                filename,
                                file.getInputStream());

        String faceUrl = minIOConfig.getFileHost()
                + "/"
                + minIOConfig.getBucketName()
                + "/"
                + filename;

        /**
         * 微服务远程调用更新用户头像到数据库 OpenFeign
         * 如果前端没有保存按钮则可以这么做，如果有保存提交按钮，则在前端可以触发
         * 此处则不需要进行微服务调用，让前端触发保存提交到后台进行保存
         */
        GraceJSONResult jsonResult = userInfoMicroServiceFeign.updateFace(userId, faceUrl);
        Object data = jsonResult.getData();

        String json = JsonUtils.objectToJson(data);
        UsersVO usersVO = JsonUtils.jsonToPojo(json, UsersVO.class);

        return GraceJSONResult.ok(usersVO);
    }

    @PostMapping("generatorQrCode")
    public String generatorQrCode(String wechatNumber,
                                  String userId) throws Exception {

        // 构建map对象
        Map<String, String> map = new HashMap<>();
        map.put("wechatNumber", wechatNumber);
        map.put("userId", userId);

        // 把对象转换为json字符串，用于存储到二维码中
        String data = JsonUtils.objectToJson(map);

        // 生成二维码
        String qrCodePath = QrCodeUtils.generateQRCode(data);

        // 把二维码上传到minio中
        if (StringUtils.isNotBlank(qrCodePath)) {
            String uuid = UUID.randomUUID().toString();
            String objectName = "wechatNumber" + File.separator + userId + File.separator + uuid + ".png";
            String imageQrCodeUrl = MinIOUtils.uploadFile(minIOConfig.getBucketName(), objectName, qrCodePath, true);
            return imageQrCodeUrl;
        }

        return null;
    }

    @PostMapping("uploadFriendCircleBg")
    public GraceJSONResult uploadFriendCircleBg(@RequestParam("file") MultipartFile file,
                                      String userId) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        String filename = file.getOriginalFilename();   // 获得文件原始名称
        if (StringUtils.isBlank(filename)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        filename = "friendCircleBg"
                + File.separator + userId
                + File.separator + dealWithoutFilename(filename);

        String imageUrl = MinIOUtils.uploadFile(minIOConfig.getBucketName(),
                filename,
                file.getInputStream(),
                true);

        GraceJSONResult jsonResult = userInfoMicroServiceFeign
                                            .updateFriendCircleBg(userId, imageUrl);
        Object data = jsonResult.getData();

        String json = JsonUtils.objectToJson(data);
        UsersVO usersVO = JsonUtils.jsonToPojo(json, UsersVO.class);

        return GraceJSONResult.ok(usersVO);
    }

    @PostMapping("uploadChatBg")
    public GraceJSONResult uploadChatBg(@RequestParam("file") MultipartFile file,
                                        String userId) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        String filename = file.getOriginalFilename();   // 获得文件原始名称
        if (StringUtils.isBlank(filename)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        filename = "chatBg"
                + File.separator + userId
                + File.separator + dealWithoutFilename(filename);

        String imageUrl = MinIOUtils.uploadFile(minIOConfig.getBucketName(),
                filename,
                file.getInputStream(),
                true);

        GraceJSONResult jsonResult = userInfoMicroServiceFeign
                    .updateFriendCircleBg(userId, imageUrl);
        Object data = jsonResult.getData();

        String json = JsonUtils.objectToJson(data);
        UsersVO usersVO = JsonUtils.jsonToPojo(json, UsersVO.class);

        return GraceJSONResult.ok(usersVO);
    }

    @PostMapping("uploadFriendCircleImage")
    public GraceJSONResult uploadFriendCircleImage(@RequestParam("file") MultipartFile file,
                                        String userId) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        String filename = file.getOriginalFilename();   // 获得文件原始名称
        if (StringUtils.isBlank(filename)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        filename = "friendCircleImage"
                + File.separator + userId
                + File.separator + dealWithoutFilename(filename);

        String imageUrl = MinIOUtils.uploadFile(minIOConfig.getBucketName(),
                filename,
                file.getInputStream(),
                true);

        return GraceJSONResult.ok(imageUrl);
    }

    @PostMapping("uploadChatPhoto")
    public GraceJSONResult uploadChatPhoto(@RequestParam("file") MultipartFile file,
                                           String userId) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        String filename = file.getOriginalFilename();   // 获得文件原始名称
        if (StringUtils.isBlank(filename)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        filename = "chat"
                + File.separator + userId
                + File.separator + "photo"
                + File.separator + dealWithoutFilename(filename);

        String imageUrl = MinIOUtils.uploadFile(minIOConfig.getBucketName(),
                filename,
                file.getInputStream(),
                true);

        return GraceJSONResult.ok(imageUrl);
    }

    @PostMapping("uploadChatVideo")
    public GraceJSONResult uploadChatVideo(@RequestParam("file") MultipartFile file,
                                           String userId) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        String filename = file.getOriginalFilename();   // 获得文件原始名称
        if (StringUtils.isBlank(filename)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        filename = "chat"
                + File.separator + userId
                + File.separator + "video"
                + File.separator + dealWithoutFilename(filename);

        String videoUrl = MinIOUtils.uploadFile(minIOConfig.getBucketName(),
                filename,
                file.getInputStream(),
                true);

        // 帧，封面获取 = 视频截帧 截取第一帧
        String coverName = UUID.randomUUID().toString() + ".jpg";   // 视频封面的名称
        String coverPath = JcodecVideoUtil.videoFramesPath
                + File.separator + "videos"
                + File.separator + coverName;

        File coverFile = new File(coverPath);
        if (!coverFile.getParentFile().exists()) {
            coverFile.getParentFile().mkdirs();
        }

        JcodecVideoUtil.fetchFrame(file, coverFile);

        // 上传封面到minio
        String coverUrl = MinIOUtils.uploadFile(minIOConfig.getBucketName(),
                coverName,
                new FileInputStream(coverFile),
                true);

        VideoMsgVO videoMsgVO = new VideoMsgVO();
        videoMsgVO.setVideoPath(videoUrl);
        videoMsgVO.setCover(coverUrl);

        return GraceJSONResult.ok(videoMsgVO);
    }

    @PostMapping("uploadChatVoice")
    public GraceJSONResult uploadChatVoice(@RequestParam("file") MultipartFile file,
                                           String userId) throws Exception {
        String voiceUrl = uploadForChatFiles(file, userId, "voice");
        return GraceJSONResult.ok(voiceUrl);
    }

    private String uploadForChatFiles(MultipartFile file,
                                      String userId,
                                      String fileType) throws Exception {

        if (StringUtils.isBlank(userId)) {
            GraceException.display(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        String filename = file.getOriginalFilename();   // 获得文件原始名称
        if (StringUtils.isBlank(filename)) {
            GraceException.display(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        filename = "chat"
                + File.separator + userId
                + File.separator + fileType
                + File.separator + dealWithoutFilename(filename);

        String fileUrl = MinIOUtils.uploadFile(minIOConfig.getBucketName(),
                filename,
                file.getInputStream(),
                true);

        return fileUrl;
    }


    private String dealWithFilename(String filename) {
        String suffixName = filename.substring(filename.lastIndexOf("."));
        String fName = filename.substring(0, filename.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString();
        return fName + "-" + uuid + suffixName;
    }

    private String dealWithoutFilename(String filename) {
        String suffixName = filename.substring(filename.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString();
        return uuid + suffixName;
    }

}
