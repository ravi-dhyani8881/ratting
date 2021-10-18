package com.spring.ratting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Component
public class RattingInterceptorAppConfig extends WebMvcConfigurerAdapter {
   @Autowired
   RattingInterceptor rattingInterceptor;

   @Override
   public void addInterceptors(InterceptorRegistry registry) {
      registry.addInterceptor(rattingInterceptor);
   }
}