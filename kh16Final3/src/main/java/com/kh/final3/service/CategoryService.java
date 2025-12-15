package com.kh.final3.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kh.final3.dao.CategoryDao;
import com.kh.final3.dto.CategoryDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryDao categoryDao;

    public List<CategoryDto> list() {
        return categoryDao.selectList();
    }

    public List<CategoryDto> top() {
        return categoryDao.selectTopList();
    }

    public List<CategoryDto> children(long parentCode) {
        return categoryDao.selectListByParent(parentCode);
    }
}
