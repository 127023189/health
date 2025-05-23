package com.itheima.utils;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.google.gson.Gson;

/**
 * 七牛云工具类
 */
public class QiniuUtils {
    public static String accessKey = "8fpKVtHlOERGpLFDPYbdGDz3t8xhvet6Hz6JT-1p";
    public static String secretKey = "LJfu3oFwSXqqb0ECXnJRNDNzJiiyIpALzIOgDsHn";
    public static String bucket = "yias";

    /**
     * 根据文件路径和文件名上传图片到七牛云
     * @param filePath
     * @param fileName
     */
    public static void upload2Qiniu(String filePath, String fileName) {
        // 构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.region2());
        UploadManager uploadManager = new UploadManager(cfg);
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        try {
            Response response = uploadManager.put(filePath, fileName, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet =
                    new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        } catch (QiniuException ex) {
            Response r = ex.response;
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
        }
    }

    /**
     * 根据文件字节数组上传图片到七牛云
     * @param bytes
     * @param fileName
     */
    public static void upload2Qiniu(byte[] bytes,String fileName){
        Configuration cfg = new Configuration(Region.region2());
        UploadManager uploadManager = new UploadManager(cfg);

        // 默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = fileName;
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        try{
            Response response = uploadManager.put(bytes, key, upToken);
            DefaultPutRet putRet =
                    new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key);
            System.out.println(putRet.hash);
        }catch (QiniuException ex){
            Response r = ex.response;
            System.err.println(r.toString());
            try{
                System.err.println(r.bodyString());
            }catch (QiniuException ex2){
                //ignore
            }
        }
    }

    /**
     * 删除文件
     * @param fileName
     */
    public static void deleteFileFromQiniu(String fileName){
        Configuration cfg = new Configuration(Region.region2());
        String key = fileName;
        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            bucketManager.delete(bucket, key);
        } catch (QiniuException ex) {
            //如果遇到异常，说明删除失败
            System.err.println(ex.code());
            System.err.println(ex.response.toString());
        }
    }
}
