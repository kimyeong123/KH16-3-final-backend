package com.kh.final3.error;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ErrorRestControllerAdvice {
	
	@ExceptionHandler(value = {TargetNotfoundException.class, NoResourceFoundException.class})
	public ResponseEntity<String> notFound(Exception e) {
		return ResponseEntity.notFound().build();//404
	}
	
	@ExceptionHandler(UnauthorizationException.class)
	public ResponseEntity<String> unauthorize(UnauthorizationException el) {
		return ResponseEntity.status(401).build();
	}
	
	@ExceptionHandler(NeedPermissionException.class)
	public ResponseEntity<String> needPermission(NeedPermissionException e, Model model) {
		return ResponseEntity.status(403).build();
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> all(Exception e) {
		log.error("예외발생", e);
		return ResponseEntity.internalServerError().build();
	}
	
}
