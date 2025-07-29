package org.back.beobachtungapp.config;

import java.nio.file.Paths;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.back.beobachtungapp.config.properties.AvatarProperties;
import org.back.beobachtungapp.filter.RequestResponseLoggingFilter;
import org.back.beobachtungapp.resolver.CurrentCompanionArgumentResolver;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
  private final CurrentCompanionArgumentResolver resolver;
  private final AvatarProperties avatarProperties;

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(resolver);
  }

  @Bean
  @Profile("local")
  public FilterRegistrationBean<RequestResponseLoggingFilter> loggingFilter() {
    FilterRegistrationBean<RequestResponseLoggingFilter> registrationBean =
        new FilterRegistrationBean<>();
    registrationBean.setFilter(new RequestResponseLoggingFilter());
    registrationBean.addUrlPatterns("/*");
    return registrationBean;
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
        .addResourceHandler("/avatar/**")
        .addResourceLocations(
            "file:" + Paths.get(avatarProperties.getUpload_dir()).toAbsolutePath() + "/");
  }
}
