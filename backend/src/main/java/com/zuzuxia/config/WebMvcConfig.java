package com.zuzuxia.config;

import com.zuzuxia.security.AdminInterceptor;
import com.zuzuxia.security.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * Web 层配置：拦截器注册、跨域、上传图片的静态资源映射。
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final AdminInterceptor adminInterceptor;

    @Value("${upload.path:./uploads/}")
    private String uploadPath;

    @Value("${upload.url-prefix:/uploads/}")
    private String uploadUrlPrefix;

    /**
     * 公开接口白名单（无需登录）。
     *
     * <p><b>必须与设计文档 8.3 节保持一致。</b>
     * 注意 {@code /api/items/mine} 不在其中 —— 它需要登录。
     */
    private static final String[] PUBLIC_PATHS = {
            "/api/auth/login",
            "/api/auth/register",
            "/api/categories",
            "/api/items",
            "/api/items/hot",
            "/api/items/recommend",
            "/api/items/*",
            "/api/items/*/available-periods",
            "/api/ping",
            // Knife4j / Swagger UI
            "/doc.html",
            "/webjars/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/favicon.ico",
            "/error"
    };

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 顺序重要：先 AuthInterceptor 填充 UserContext，再 AdminInterceptor 判角色
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(PUBLIC_PATHS)
                .order(1);

        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/api/admin/**")
                .order(2);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 把上传目录暴露为静态资源，使 <img src="/uploads/xxx.jpg"> 可直接访问
        String location = Paths.get(uploadPath).toAbsolutePath().normalize().toUri().toString();
        registry.addResourceHandler(uploadUrlPrefix + "**")
                .addResourceLocations(location);

        // Knife4j 文档页面
        registry.addResourceHandler("/doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
