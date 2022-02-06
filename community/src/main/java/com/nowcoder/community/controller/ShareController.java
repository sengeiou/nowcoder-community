package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Event;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
public class ShareController implements CommunityConstant {
    @Autowired
    private EventProducer eventProducer;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${wk.image.storage}")
    private String wkImageStorage;

    @Value("${qiniu.bucket.url}")
    private String bucketUrl;

    @Value("${qiniu.bucket.name}")
    private String bucketName;

    @GetMapping("/share")
    @ResponseBody
    public String share(String url) {
        // 生成文件名
        String filename = CommunityUtil.generateUUID();
        // 异步生成长图
        Event event = new Event()
                .setTopic(TOPIC_SHARE)
                .setData("url", url)
                .setData("filename", filename)
                .setData("suffix", ".png");
        eventProducer.doEvent(event);
        Map<String, Object> map = new HashMap<>();
        map.put("shareUrl", bucketUrl + "/" + filename);
        return CommunityUtil.getJSONString(0, "生成成功", map);
    }

    // 获取长图
    @GetMapping("/share/image/{filename}")
    public void getShareImage(@PathVariable("filename") String filename, HttpServletResponse response) {
        if (StringUtils.isBlank(filename)) {
            throw new IllegalArgumentException("文件名不能为空！");
        }
        response.setContentType("image/png");
        File file = new File(wkImageStorage + "/" + filename + ".png");
        try (OutputStream os = response.getOutputStream();
             FileInputStream in = new FileInputStream(file);
        ) {
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = in.read(buf)) != -1) {
                os.write(buf, 0, len);
            }
        } catch (IOException e) {
            log.error("获取长图失败: " + e.getMessage());
        }
    }
}
