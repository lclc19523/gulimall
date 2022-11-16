package com.lc.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

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
 * 3.JSR303
 *   1).给Bean添加校验注解 javax.validation.constraints ,并定义自己的message提示
 *   2).开启校验功能@Valid
 *     效果:校验错误以后会有默认的响应、
 *   3).给校验的bean后紧跟一个BindgResult,就可以获取到校验的结果
 *   4).分组校验（多场景的复杂校验）
 *      1).@NotBlank(message = "品牌必须提交",groups = {AddGroup.class,UpdateGroup.class})
 *          给注解标注什么情况下需要进行校验
 *      2).@Validated({AddGroup.class})
 *      3).默认没有指定分组校验的注解@notblank，在分组校验情况下不生效，只会在@Validated下生效（不校验分组）
 *   5).自定义校验功能
 *      1).编写一个自定义的校验注解
 *      2).编写一个自定义的校验器
 *      3).关联自定义的校验器和自定义的校验注解
 *          @Documented
 *          @Constraint(validatedBy = {ListValueConstraintValidator.class,可以指定多个不同的校验器，适配不同类型的校验})
 *          @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
 *          @Retention(RUNTIME)
 * 4.统一异常处理
 * @ControllerAdvice
 *  1).编写异常处理类,使用@ControolerAdvice
 *  2).使用@ExceptionHandler标注方法可以处理的异常
 *
 *
 *
 *
 *
 */

@EnableFeignClients(basePackages = {"com.lc.gulimall.product.feign"})
@MapperScan("com.lc.gulimall.product.dao")
@SpringBootApplication
@EnableDiscoveryClient
public class GulimallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }

}
