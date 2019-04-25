package com.jiebbs.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传服务接口
 * @author  weijie
 * @version v1.0 2019-04-25
 */
public interface IFileService {

    String upload(MultipartFile file, String path);
}
