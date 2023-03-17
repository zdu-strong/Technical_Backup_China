package com.springboot.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController extends BaseController {
	@GetMapping("/")
	public ResponseEntity<?> helloWorld() {
		return ResponseEntity.ok("Hello, World!");
	}
}
