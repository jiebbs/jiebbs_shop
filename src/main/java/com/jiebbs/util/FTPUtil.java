package com.jiebbs.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;


/**
 * FTP服务器交互工具类
 * @author weijie
 * @version v1.0
 */
public class FTPUtil {

    private static Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");

    private static String ftpUsername = PropertiesUtil.getProperty("ftp.user");

    private static String ftpPassword = PropertiesUtil.getProperty("ftp.pass");

    private String ip;

    private int port;

    private String username;

    private String password;

    private static FTPClient ftpClient;

    private FTPUtil(String ip, int port, String username, String password) {
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 对外暴露的上传接口
     * @param fileList 多文件上传
     * @return 是否上传成功（成功：true,失败：false）
     */
    public static boolean fileUploads(List<File> fileList){
        //创建Ftp连接
        FTPUtil ftpUtil = new FTPUtil(ftpIp,21,ftpUsername,ftpPassword);
        logger.info("开始连接FTP服务器，并上传文件");
        boolean isSuccess =  ftpUtil.uploadFiles("img",fileList);
        logger.info("完成上传文件到FTP,上传结果:{}",isSuccess);
        return isSuccess;
    }

    /**
     * 创建私有方法处理上传逻辑
     * @param remotePath ftp远程文件夹地址
     * @param fileList 上传的文件
     * @return 上传文件是否成功
     */
    private boolean uploadFiles(String remotePath,List<File> fileList){
        boolean uploaded = true;
        FileInputStream fileInputStream = null;

        //连接FTP服务器
        if(connectFtpServer(this.ip,this.port,this.ftpUsername,this.ftpPassword)){
            try {
                //更改工作目录
                ftpClient.changeWorkingDirectory(remotePath);
                //设置缓冲区
                ftpClient.setBufferSize(1024);
                //设置编码
                ftpClient.setControlEncoding("UTF-8");
                //设置文件类型(二进制防止乱码)
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                //打开本地被动模式
                ftpClient.enterLocalPassiveMode();

                logger.info("远程地址:{}",remotePath);

                //遍历上传文件
                for(File file:fileList){
                    logger.info("开始将文件上传到Ftp服务器,文件名称:{}",file.getName());
                    //读取文件
                    fileInputStream = new FileInputStream(file);
                    //上传文件到FTP
                    ftpClient.storeFile(file.getName(),fileInputStream);
                    logger.info("完成文件上传到Ftp服务器,文件名称:{}",file.getName());
                }
            } catch (IOException e) {
                uploaded = false;
                logger.error("上传文件异常",e);
                e.printStackTrace();
            } finally {
                if(null!=fileInputStream){
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        logger.error("关闭文件输入流异常",e);
                        e.printStackTrace();
                    }
                }
                if(null!=ftpClient){
                    try {
                        ftpClient.disconnect();
                    } catch (IOException e) {
                        logger.error("关闭FTP服务器连接异常",e);
                        e.printStackTrace();
                    }
                }
            }
        }
        return uploaded;
    }

    /**
     * 初始化FTPClient
     * @param ip
     * @param port
     * @param username
     * @param password
     * @return
     */
    private static boolean connectFtpServer(String ip, int port, String username,String password){
        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            //连接FTP服务器
            ftpClient.connect(ip,port);
            isSuccess = ftpClient.login(username,password);
        } catch (IOException e) {
            logger.error("FTP服务器连接异常",e);
            e.printStackTrace();
        }
        return isSuccess;
    }

}
