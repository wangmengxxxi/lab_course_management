package com.example.lab_course_management.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI labCourseManagementOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("实验室课程管理系统 API")
                        .description("实验室课程管理系统的API接口文档")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("开发者")
                                .email("dev@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .externalDocs(new ExternalDocumentation()
                        .description("项目文档")
                        .url("https://example.com/docs"));
    }
}


