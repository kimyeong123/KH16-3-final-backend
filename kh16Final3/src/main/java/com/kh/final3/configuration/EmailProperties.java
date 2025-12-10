package com.kh.final3.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data//이게 있어야 setter
@Component//등록을 해야 외부에서 가져올 수 있음
@ConfigurationProperties(prefix="custom.email")
public class EmailProperties {
	private String username;
	private String password;
}