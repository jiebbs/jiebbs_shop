package com.jiebbs.service;

import com.jiebbs.util.JsonUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-context.xml","classpath:spring-mvc.xml","classpath:spring-datasource.xml"})
public class FileServiceTest {

    @Resource(name = "iFileService")
    private IFileService iFileService;

    @Test
    public void filesUploadTest(){
        File file = new File("/Users/kenfor/Downloads/cat.jpg");
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("cat2.jpg","cat.jpg","multipart/form-data",fileInputStream);
            String resp = iFileService.upload(multipartFile,"/Users/kenfor/Downloads/upload/");
            JsonUtil.convert2JsonAndPrint(resp);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
