package com.lc.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 1.整合Mybatis-Plus
 *     1).导入依赖
 *     <dependency>
 *             <groupId>com.baomidou</groupId>
 *             <artifactId>mybatis-plus-boot-starter</artifactId>
 *             <version>3.2.0</version>
 *         </dependency>
 *     2）。配置
 *       1.配置数据源：
 *         1）。导入数据库驱动
 *         2)。在application.yml中配置数据源相关信息
 *       2.配置Mybatis-Plus
 *          1).使用@mapperscan
 *          2).告诉Mybatis-plus sql映射配置文件的位置
 * 2.逻辑删除
 *   1).配置全局的逻辑删除规则(省略)
 *   2).配置逻辑删除的Bean(高版本省略)
 *   3).加上逻辑删除的注解@TableLogic
 *
 *
 *
 */

@MapperScan("com.lc.gulimall.product.dao")
@SpringBootApplication
@EnableDiscoveryClient
public class GulimallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }

}
