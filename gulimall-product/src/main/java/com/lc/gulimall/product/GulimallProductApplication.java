package com.lc.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
 *
 *
 *
 */

@MapperScan("com.lc.gulimall.product.dao")
@SpringBootApplication
public class GulimallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }

}
