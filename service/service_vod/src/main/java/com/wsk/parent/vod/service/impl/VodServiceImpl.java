package com.wsk.parent.vod.service.impl;

import com.qcloud.vod.VodUploadClient;
import com.qcloud.vod.model.VodUploadRequest;
import com.qcloud.vod.model.VodUploadResponse;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.vod.v20180717.VodClient;
import com.tencentcloudapi.vod.v20180717.models.DeleteMediaRequest;
import com.tencentcloudapi.vod.v20180717.models.DeleteMediaResponse;
import com.wsk.ggkt.model.vod.Video;
import com.wsk.parent.vod.service.VideoService;
import com.wsk.parent.vod.service.VodService;
import com.wsk.parent.vod.utils.ConstantPropertiesUtil;
import com.wsk.serviceutil.exception.GgktException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author:WuShangke
 * @create:2022/8/20-17:23
 */
@Service
public class VodServiceImpl implements VodService {
    @Autowired
    private VideoService videoService;

    @Value("${tencent.video.appid}")
    private String appId;

    //上传视频,参考官方文档 https://cloud.tencent.com/document/product/266/10276
    //官方文档只有写死的方法？文件路径写死了
    //TODO 有没有不写死的方法？
    @Override
    public String uploadVideo(InputStream inputStream, String originalFilename) {
        try {
            VodUploadClient client = new VodUploadClient(ConstantPropertiesUtil.ACCESS_KEY_ID,
                                                                  ConstantPropertiesUtil.ACCESS_KEY_SECRET);
            VodUploadRequest request = new VodUploadRequest();
            //视频本地地址
            request.setMediaFilePath("D:\\001.mp4");
            //指定任务流
            request.setProcedure("LongVideoPreset");
            //调用上传方法，传入接入点地域及上传请求。
            VodUploadResponse response = client.upload("ap-shanghai", request);
            //返回文件id保存到业务表，用于控制视频播放
            String fileId = response.getFileId();
            System.out.println("Upload FileId = {}"+response.getFileId());
            return fileId;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //删除视频
    @Override
    public void removeVideo(String id) {
        try{
            // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey
            Credential cred =
                    new Credential(ConstantPropertiesUtil.ACCESS_KEY_ID,
                            ConstantPropertiesUtil.ACCESS_KEY_SECRET);
            // 实例化要请求产品的client对象,clientProfile是可选的
            VodClient client = new VodClient(cred, "ap-shanghai");
            // 实例化一个请求对象,每个接口都会对应一个request对象
            DeleteMediaRequest req = new DeleteMediaRequest();
            req.setFileId(id);
            // 返回的resp是一个DeleteMediaResponse的实例，与请求对象对应
            DeleteMediaResponse resp = client.DeleteMedia(req);
            // 输出json格式的字符串回包
            System.out.println(DeleteMediaResponse.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }
    }

    //点播视频播放接口，获取播放凭证
    @Override
    public Map<String, Object> getPlayAuth(Long courseId, Long videoId) {
        //查询video
        Video video = videoService.getById(videoId);
        if(video==null){
            throw new GgktException(20001,"视频不存在");
        }
        //获取video中的腾讯云视频ID，并封装进map
        Map<String, Object> map = new HashMap<>();
        map.put("videoSourceId",video.getVideoSourceId());
        map.put("appId",appId);
        return map;
    }
}
