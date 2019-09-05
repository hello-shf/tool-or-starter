package com.my.fastdfs.service;

import cn.hutool.core.io.FileUtil;
import com.my.fastdfs.core.IotFastdfsConnectionPool;
import com.my.fastdfs.properties.IotFastdfsProperties;
import com.iot.iottool.core.base64.IotBase64Utils;
import org.csource.common.MyException;
import org.csource.fastdfs.StorageClient1;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


public class IotFastdfsTemplate {
    private IotFastdfsConnectionPool pool;
    @Autowired
    private IotFastdfsProperties iotFastdfsProperties;

    /**
     * 初始化连接客户端
     * @param pool
     */
    public IotFastdfsTemplate(IotFastdfsConnectionPool pool){
        this.pool=pool;
    }

    /**
     * 上传图片-base64
     *
     * @param base64
     * @param parentFilePath
     * @param fileName
     * @return
     * @throws IOException
     * @throws MyException
     */
    public String uploadFileByBase64(String base64, String parentFilePath, String fileName) throws IOException, MyException {
        int retryTimes = iotFastdfsProperties.getRetryTimes();
        String url = "";
        try {
            url = uploadBase64(base64, parentFilePath, fileName);
        } catch (MyException | IOException e) {
            for (int i = 0; i <= retryTimes; i++) {
                try {
                    url = uploadBase64(base64, parentFilePath, fileName);
                    if (url != null && !url.isEmpty()) {
                        break;
                    }
                } catch (IOException | MyException e1) {
                    if(i == retryTimes){
                        throw e;
                    }else{
                        continue;
                    }
                }
            }
        }
        return url;
    }
    public String uploadBase64(String base64,String parentFilePath,String fileName) throws IOException, MyException {
        StorageClient1 client =null;
        String url="";
        String fullPath=parentFilePath+ File.separator+fileName;
        try {
            client = pool.getStorageClinet(10);
            boolean flag = IotBase64Utils.Base64ToImage(base64, fullPath);
            if(flag){
                //开始上传fastdfs
                String[] strings = client.upload_file(fullPath, fileName.split("\\.")[1], null);
                if(strings!=null && strings.length>0){
                    url=strings[0]+"/"+strings[1];
                }
            }
        } catch (MyException | IOException e){
            throw e;
        }finally {
            pool.giveBack(client);
            File file = new File(fullPath);
            if (file.exists() && file.isFile()) {
                file.delete();
            }
        }
        return url;
    }
    /**
     * 上传本地图片
     * @param parentFilePath
     * @param fileName
     * @return
     */
    public String uploadFileByLocal(String parentFilePath,String fileName) throws MyException, IOException {
        StorageClient1 client =null;
        String url="";
        client = pool.getStorageClinet(10);
        String fullPath=parentFilePath+ File.separator+fileName;
        if(FileUtil.exist(fullPath)){
            //开始上传fastdfs
            try {
                url = client.upload_file1(fullPath, fileName.split("\\.")[1], null);
            } catch (MyException e) {
                throw e;//异常继续上抛
            } catch (IOException e) {
                throw e;
            } finally {
                pool.giveBack(client);
            }
        }else{
            throw new FileNotFoundException("路径不存在:"+fullPath);
        }
        return url;
    }
}
