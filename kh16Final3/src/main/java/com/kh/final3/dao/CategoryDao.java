package com.kh.final3.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.final3.dto.CategoryDto;

@Repository
public class CategoryDao {

    @Autowired
    private SqlSession sqlSession;

    public List<CategoryDto> selectList() {
        return sqlSession.selectList("category.list");
    }

    public List<CategoryDto> selectTopList() {
        return sqlSession.selectList("category.listTop");
    }

    public List<CategoryDto> selectListByParent(long parentCode) {
        return sqlSession.selectList("category.listByParent", parentCode);
    }
    
}