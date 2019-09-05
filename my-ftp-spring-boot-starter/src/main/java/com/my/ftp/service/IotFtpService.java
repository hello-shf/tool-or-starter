package com.my.ftp.service;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.pool2.ObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Data
public class IotFtpService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * ftpClient连接池初始化标志
     */
    private boolean hasInit = false;
    /**
     * ftpClient连接池
     */
    private ObjectPool<FTPClient> ftpClientPool;

    /**
     * 上传文件
     * @param pathname ftp服务保存地址
     * @param fileName 上传到ftp的文件名
     *  @param originfilename 待上传文件的名称（绝对地址） *
     * @return
     */
    public boolean uploadFile( String pathname, String fileName,String originfilename){
        boolean flag = false;
        InputStream inputStream = null;
        FTPClient ftpClient = getFtpClient();
        try{
            log.info("开始上传文件");
            inputStream = new FileInputStream(new File(originfilename));
            ftpClient.setFileType(ftpClient.BINARY_FILE_TYPE);
            CreateDirecroty(pathname,ftpClient);
            ftpClient.makeDirectory(pathname);
            ftpClient.changeWorkingDirectory(pathname);
            ftpClient.storeFile(fileName, inputStream);
            inputStream.close();
            flag = true;
            log.info("上传文件成功");
        }catch (Exception e) {
            log.error("上传文件失败");
            e.printStackTrace();
        }finally{
            releaseFtpClient(ftpClient);
        }
        return flag;
    }


    /**
     * 上传文件
     * @param pathname ftp服务保存地址
     * @param fileName 上传到ftp的文件名
     * @param inputStream 输入文件流
     * @return
     */
    public boolean uploadFile( String pathname, String fileName,InputStream inputStream){
        boolean flag = false;
        FTPClient ftpClient = getFtpClient();
        try{
            log.info("开始上传文件");
            ftpClient.setFileType(ftpClient.BINARY_FILE_TYPE);
            CreateDirecroty(pathname,ftpClient);
            ftpClient.makeDirectory(pathname);
            ftpClient.changeWorkingDirectory(pathname);
            ftpClient.storeFile(fileName, inputStream);
            inputStream.close();
            flag = true;
            log.info("上传文件成功");
        }catch (Exception e) {
            log.error("上传文件失败");
            e.printStackTrace();
        }finally{
            releaseFtpClient(ftpClient);
        }
        return flag;
    }

    /** * 下载文件 *
     * @param pathname FTP服务器文件目录 *
     * @param filename 文件名称 *
     * @param localpath 下载后的文件路径 *
     * @return */
    public  boolean downloadFile(String pathname, String filename, String localpath){
        boolean flag = false;
        OutputStream os=null;
        FTPClient ftpClient = getFtpClient();
        try {
            log.info("开始下载文件");
            //切换FTP目录
            ftpClient.changeWorkingDirectory(pathname);
            FTPFile[] ftpFiles = ftpClient.listFiles();
            for(FTPFile file : ftpFiles){
                if(filename.equalsIgnoreCase(file.getName())){
                    File localFile = new File(localpath + "/" + file.getName());
                    os = new FileOutputStream(localFile);
                    ftpClient.retrieveFile(file.getName(), os);
                    os.close();
                }
            }
            flag = true;
            log.info("下载文件成功");
        } catch (Exception e) {
            log.error("下载文件失败");
            e.printStackTrace();
        } finally{
            releaseFtpClient(ftpClient);
            if(null != os){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    /** * 删除文件 *
     * @param pathname FTP服务器保存目录 *
     * @param filename 要删除的文件名称 *
     * @return */
    public boolean deleteFile(String pathname, String filename){
        boolean flag = false;
        FTPClient ftpClient = getFtpClient();
        try {
            log.info("开始删除文件");
            //切换FTP目录
            ftpClient.changeWorkingDirectory(pathname);
            ftpClient.dele(filename);
            ftpClient.logout();
            flag = true;
            log.info("删除文件成功");
        } catch (Exception e) {
            log.error("删除文件失败");
            e.printStackTrace();
        } finally {
            releaseFtpClient(ftpClient);
        }
        return flag;
    }

    /**
     * 按行读取FTP文件
     *
     * @param remoteFilePath 文件路径（path+fileName）
     * @return
     * @throws IOException
     */
    public List<String> readFileByLine(String remoteFilePath) throws IOException {
        FTPClient ftpClient = getFtpClient();
        try (InputStream in = ftpClient.retrieveFileStream(encodingPath(remoteFilePath));
             BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            return br.lines().map(line -> StrUtil.trimToEmpty(line))
                    .filter(line -> StrUtil.isNotEmpty(line)).collect(Collectors.toList());
        } finally {
            ftpClient.completePendingCommand();
            releaseFtpClient(ftpClient);
        }
    }

    /**
     * 获取指定路径下FTP文件
     *
     * @param remotePath 路径
     * @return FTPFile数组
     * @throws IOException
     */
    public FTPFile[] retrieveFTPFiles(String remotePath) throws IOException {
        FTPClient ftpClient = getFtpClient();
        try {
            return ftpClient.listFiles(encodingPath(remotePath + "/"),
                    file -> file != null && file.getSize() > 0);
        } finally {
            releaseFtpClient(ftpClient);
        }
    }

    /**
     * 获取指定路径下FTP文件名称
     *
     * @param remotePath 路径
     * @return ftp文件名称列表
     * @throws IOException
     */
    public List<String> retrieveFileNames(String remotePath) throws IOException {
        FTPFile[] ftpFiles = retrieveFTPFiles(remotePath);
        if (ArrayUtil.isEmpty(ftpFiles)) {
            return new ArrayList<>();
        }
        return Arrays.stream(ftpFiles).filter(Objects::nonNull)
                .map(FTPFile::getName).collect(Collectors.toList());
    }

    /**
     * 编码文件路径
     */
    private static String encodingPath(String path) throws UnsupportedEncodingException {
        // FTP协议里面，规定文件名编码为iso-8859-1，所以目录名或文件名需要转码
        return new String(path.replaceAll("//", "/").getBytes("GBK"), "iso-8859-1");
    }

    /**
     * 获取ftpClient
     *
     * @return
     */
    private FTPClient getFtpClient() {
        checkFtpClientPoolAvailable();
        FTPClient ftpClient = null;
        Exception ex = null;
        // 获取连接最多尝试3次
        for (int i = 0; i < 3; i++) {
            try {
                ftpClient = ftpClientPool.borrowObject();
                ftpClient.enterLocalPassiveMode();//被动模式
                ftpClient.changeWorkingDirectory("/");
                break;
            } catch (Exception e) {
                ex = e;
            }
        }
        if (ftpClient == null) {
            throw new RuntimeException("Could not get a ftpClient from the pool", ex);
        }
        return ftpClient;
    }

    /**
     * 释放ftpClient
     */
    private void releaseFtpClient(FTPClient ftpClient) {
        if (ftpClient == null) {
            return;
        }

        try {
            ftpClientPool.returnObject(ftpClient);
        } catch (Exception e) {
            log.error("Could not return the ftpClient to the pool", e);
            // destoryFtpClient
            if (ftpClient.isAvailable()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException io) {
                }
            }
        }
    }

    /**
     * 检查ftpClientPool是否可用
     */
    private void checkFtpClientPoolAvailable() {
        Assert.state(hasInit, "FTP未启用或连接失败！");
    }


    /**
     * 创建多层目录文件，如果有ftp服务器已存在该文件，则不创建，如果无，则创建
     * @param remote
     * @param ftpClient
     * @return
     * @throws IOException
     */
    public boolean CreateDirecroty(String remote,FTPClient ftpClient) throws IOException {
        boolean success = true;
        String directory = remote + "/";
        // 如果远程目录不存在，则递归创建远程服务器目录
        if (!directory.equalsIgnoreCase("/") && !changeWorkingDirectory(new String(directory),ftpClient)) {
            int start = 0;
            int end = 0;
            if (directory.startsWith("/")) {
                start = 1;
            } else {
                start = 0;
            }
            end = directory.indexOf("/", start);
            String path = "";
            String paths = "";
            while (true) {
                String subDirectory = new String(remote.substring(start, end).getBytes("GBK"), "iso-8859-1");
                path = path + "/" + subDirectory;
                if (!existFile(path,ftpClient)) {
                    if (makeDirectory(subDirectory,ftpClient)) {
                        changeWorkingDirectory(subDirectory,ftpClient);
                    } else {
                        System.out.println("创建目录[" + subDirectory + "]失败");
                        changeWorkingDirectory(subDirectory,ftpClient);
                    }
                } else {
                    changeWorkingDirectory(subDirectory,ftpClient);
                }

                paths = paths + "/" + subDirectory;
                start = end + 1;
                end = directory.indexOf("/", start);
                // 检查所有目录是否创建完毕
                if (end <= start) {
                    break;
                }
            }
        }
        return success;
    }

    /**
     * 改变目录路径
     * @param directory
     * @param ftpClient
     * @return
     */
    public boolean changeWorkingDirectory(String directory,FTPClient ftpClient) {
        boolean flag = true;
        try {
            flag = ftpClient.changeWorkingDirectory(directory);
            if (flag) {
                System.out.println("进入文件夹" + directory + " 成功！");

            } else {
                System.out.println("进入文件夹" + directory + " 失败！开始创建文件夹");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return flag;
    }

    //判断ftp服务器文件是否存在
    public boolean existFile(String path,FTPClient ftpClient) throws IOException {
        boolean flag = false;
        FTPFile[] ftpFileArr = ftpClient.listFiles(path);
        if (ftpFileArr.length > 0) {
            flag = true;
        }
        return flag;
    }
    //创建目录
    public boolean makeDirectory(String dir,FTPClient ftpClient) {
        boolean flag = true;
        try {
            flag = ftpClient.makeDirectory(dir);
            if (flag) {
                log.info("创建文件夹" + dir + " 成功！");

            } else {
                log.info("创建文件夹" + dir + " 失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
}