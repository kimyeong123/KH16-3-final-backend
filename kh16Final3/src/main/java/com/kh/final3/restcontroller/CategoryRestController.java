package com.kh.final3.restcontroller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.kh.final3.dto.CategoryDto;
import com.kh.final3.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/category")
@CrossOrigin
@RequiredArgsConstructor
public class CategoryRestController {

    private final CategoryService categoryService;

    // ✅ 전체 카테고리 (depth 1/2 전부)  ← 이거 추가
    @GetMapping
    public List<CategoryDto> list() {
        return categoryService.list();
    }

    @GetMapping("/top")
    public List<CategoryDto> top() {
        return categoryService.top();
    }

    @GetMapping("/{parentCode}/children")
    public List<CategoryDto> children(@PathVariable long parentCode) {
        return categoryService.children(parentCode);
    }
}