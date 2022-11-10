package com.lc.gulimall.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 *  1.如何使用nacos作为配置中心统一配置管理
 *    1）.引入依赖
 *    <dependency>
 *             <groupId>com.alibaba.cloud</groupId>
 *             <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
 *         </dependency>
 *    2).创建一个bootstrap.properties
 *              spring.application.name=gulimall-coupon
 *              spring.cloud.nacos.config.server-addr=127.0.0.1:8848
 *     3).需要给配置中心默认添加一个数据集 叫（Data id） gulimall-coupon.properties 默认规则，应用名.properties
 *     4).给应用名.properties 添加任何配置
 *     5).动态获取配置
         * @RefreshScope :添加到controller上，动态刷新并获取配置
 *
         * @value("${配置项的名}")：获取到配置
 *          如果配置配置中心和当前应用的配置文件中度配置了相同的项优先使用配置中心的
 *  2.细节
 *    1).命名空间:配置隔离
 *       默认是public(保留空间);默认新增的所有配置度在public空间
 *       1.开发，生产，测试:利用命名空间来做环境隔离
 *         注意：在bootstrap.properties中配置需要使用哪个命名空间下的配置
 *         spring.cloud.nacos.config.namespace=41523a14-36db-4010-a523-38adf5ceadf3
 *       2.每一个微服务之间互相隔离，每一个微服务间都创建自己的命名空间，只加载自己命名空间下的配置
 *
 *    2).配置集：所有的配置的集合
 *    3).配置集ID:类似于文件名
 *          Data ID:配置文件名
 *    4).配置分组：
 *      默认所有的配置集都属于:DEFAULT_GROUP
 *      1111,618,1212
 *  每个微服务创建自己的命名空间，使用配置分组区分环境，dev，test，prod
 *  3.同时加载多个配置集
 *   1).微服务任何配置信息，任何配置文件都可以放在配置中心中
 *   2).只需要在bootstrap.properties说明加载配置中心中哪些配置文件即可
 *   3).@Value,ConfigurationProperties..
 *   以前springboot任何方法从配置中心中获取值，都能使用，
 *   配置中心中有的优先使用配置中心中的
 *
 *
 *
 *
 */


@SpringBootApplication
@EnableDiscoveryClient
public class GulimallCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallCouponApplication.class, args);
    }

}
