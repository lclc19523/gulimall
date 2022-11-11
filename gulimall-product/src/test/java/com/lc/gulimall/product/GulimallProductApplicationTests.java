package com.lc.gulimall.product;

import com.aliyun.oss.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.additional.query.impl.QueryChainWrapper;
import com.lc.gulimall.product.entity.BrandEntity;
import com.lc.gulimall.product.service.BrandService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

/**
 * 1.引入oss-starter  spring-cloud-starter-alicloud-oss
 * 2.配置key，endpoint相关信息
 * 3.使用ossclient对象存储进行操作
 *
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallProductApplicationTests {


    @Autowired
    BrandService brandService;

    @Autowired
    OSSClient ossClient;

    @Test
    public void testUpload() throws FileNotFoundException {
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
//        String endpoint = "oss-cn-hangzhou.aliyuncs.com";
//        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
//        String accessKeyId = "LTAI5tDFMABwWEi9MhBnwwop";
//        String accessKeySecret = "BkCZv2WF5QrdmgTHD9FETH02JQOTzD";
        // 填写Bucket名称，例如examplebucket。
        String bucketName = "gulimall---lc";
        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
        String objectName = "刘聪-Java.pdf";
        // 填写本地文件的完整路径，例如D:\\localpath\\examplefile.txt。
        // 如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
        String filePath= "D:\\Desktop\\刘聪-Java.pdf";

//        // 创建OSSClient实例。
//        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);


        try {
            InputStream inputStream = new FileInputStream(filePath);
            // 创建PutObject请求。
            ossClient.putObject(bucketName, objectName, inputStream);
            System.out.println("上传完成");
        } catch (OSSException oe) {
            System.err.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.err.println("Error Message:" + oe.getErrorMessage());
            System.err.println("Error Code:" + oe.getErrorCode());
            System.err.println("Request ID:" + oe.getRequestId());
            System.err.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.err.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.err.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

    }


    @Test
    public void contextLoads() {

        BrandEntity brandEntity=new BrandEntity();


        List<BrandEntity> brand_id = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1l));
        brand_id.forEach(item->{
            System.out.println(item);
        });
    }

}
