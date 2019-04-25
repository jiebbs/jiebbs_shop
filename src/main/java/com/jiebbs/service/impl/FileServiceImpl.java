package com.jiebbs.service.impl;

import com.google.common.collect.Lists;
import com.jiebbs.service.IFileService;
import com.jiebbs.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传服务模块接口实现类
 * @author weijie
 * @version v1.0 2019-04-25
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private static Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile file,String path){
        //获取源文件原始名称
        String fileName = file.getOriginalFilename();
        //获取文件后缀名(并去掉点号)
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
        //更改上传的文件名名称
        String uploadFileName = UUID.randomUUID()+"."+fileExtensionName;
        //使用logger记录日志
        logger.info("开始上传文件，上传文件名为:{},上传路径为:{}，新文件名为:{}",fileName,path,uploadFileName);

        //创建上传文件夹
        File uploadFileDir = new File(path);
        //判断文件路径是否存在
        if(!uploadFileDir.exists()){
            //设置新建文件夹可写
            uploadFileDir.setWritable(true);
            uploadFileDir.mkdirs();
        }

        File targetFile = new File(path,uploadFileName);

        try {
            //先将上传文件先到本地路径
            file.transferTo(targetFile);
            //再调用FTP上传工具类上传到FTP服务器
            FTPUtil.fileUploads(Lists.<File>newArrayList(targetFile));
            //删除upload文件夹中的临时文件
            targetFile.delete();
        } catch (IOException e) {
            logger.error("上传文件异常",e);
        }
        return targetFile.getName();
    }
}
