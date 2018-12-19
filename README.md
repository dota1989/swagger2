# swagger2
### 1、引入依赖
```
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger2</artifactId>
            <version>2.9.2</version>
        </dependency>
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger-ui</artifactId>
            <version>2.9.2</version>
        </dependency>
```
### 2、Swagger配置
```
package com.zb.myswagger2;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.List;

/**
 * Created by zhangbin on 2018/12/19.
 */
@Configuration
@EnableSwagger2
public class Swagger {
    //指定扫描哪些包
    @Bean
    public Docket createRestApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(swaggeInfo())
                .select()
                //要扫描的包路径
                .apis(RequestHandlerSelectors.basePackage("com.zb.myswagger2.controller"))
                .paths(PathSelectors.any())
                .build();
    }
    //配置swagger的基本信息
    private ApiInfo swaggeInfo(){
        return new ApiInfoBuilder()
                .title("Spring Boot 测试使用 Swagger2")
                //创始人
                .contact(new Contact("swagger-git","https://github.com/swagger-api/swagger-ui/",""))
                //版本
                .version("1.0")
                //描述
                .description("API 描述")
                .build();
    }

}

```

### 3、在Controller接口上配置说明
```
@RestController
@Api(value = "系统相关的接口")
public class HomeController {

    @ApiOperation(value = "查看系统信息",notes = "查看系统信息")
    @RequestMapping(value = "/home", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String home(){
        return "Hello Swagger2!";
    }
}
```

### 4、在网页查看
url：http://localhost:8080/swagger-ui.html

### 5、权限配置
在Swagger类里面加权限相关信息，如下
```
package com.zb.myswagger2;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.List;

/**
 * Created by zhangbin on 2018/12/19.
 */
@Configuration
@EnableSwagger2
public class Swagger {
    //指定扫描哪些包
    @Bean
    public Docket createRestApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(swaggeInfo())
                .select()
                //要扫描的包路径
                .apis(RequestHandlerSelectors.basePackage("com.zb.myswagger2.controller"))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(Lists.newArrayList(apiKey()))
                .securityContexts(Arrays.asList(securityContext()));
    }
    //配置swagger的基本信息
    private ApiInfo swaggeInfo(){
        return new ApiInfoBuilder()
                .title("Spring Boot 测试使用 Swagger2")
                //创始人
                .contact(new Contact("swagger-git","https://github.com/swagger-api/swagger-ui/",""))
                //版本
                .version("1.0")
                //描述
                .description("API 描述")
                .build();
    }

    @Bean
    public SecurityConfiguration security() {
        return SecurityConfigurationBuilder.builder().scopeSeparator(",")
                .additionalQueryStringParams(null)
                .useBasicAuthenticationWithAccessCodeGrant(false).build();
    }

    private ApiKey apiKey() {
        return new ApiKey("apiKey", "Authorization", "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth())
                .forPaths(PathSelectors.any()).build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope(
                "global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("apiKey",
                authorizationScopes));
    }

}

```

在Filter里面过滤swagger相关url
```
package com.zb.myswagger2.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 */
@WebFilter(filterName = "TokenFilter", urlPatterns = {"/*"}, asyncSupported = true)
public class TokenFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(TokenFilter.class);

    String[] includeUrls = new String[]{"/"};

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;

        String url = request.getRequestURI();
        logger.info("current_url: {}", url);
        boolean needFilter = isNeedFilter(url);

        //静态资源放行
        if(url.endsWith(".js")||url.endsWith(".css")||url.endsWith(".jpg")
                ||url.endsWith(".gif")||url.endsWith(".png")){
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        //swagger
        if(url.startsWith("/webjars") || url.startsWith("/v2") || url.startsWith("/swagger-resources")
                || url.startsWith("/csrf")
                || "/swagger-ui.html".equals(url) ){
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        if(!needFilter){
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            String token = request.getHeader("Authorization");

            // TODO 替换项目自己的校验
            if("token".equals(token)){
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                response.setContentType("text/html;charset=utf-8");
                response.getWriter().write("您还没有权限！");
            }
        }
    }

    @Override
    public void destroy() {

    }

    public boolean isNeedFilter(String uri) {
        for (String includeUrl : includeUrls) {
            if(includeUrl.equals(uri)) {
                return false;
            }
        }
        return true;
    }
}

```

