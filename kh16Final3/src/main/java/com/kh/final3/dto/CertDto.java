package com.kh.final3.dto;

import java.sql.Timestamp;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CertDto {
	@NotBlank @Email
	private String certEmail;
	@Pattern(regexp = "^[0-9]{6}$")
	private String certNumber;
	private Timestamp certTime;
}
