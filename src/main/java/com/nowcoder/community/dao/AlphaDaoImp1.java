package com.nowcoder.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public class AlphaDaoImp1 implements AlphaDao{
    @Override
    public String select() {
        return "This is Maggie!";
    }
}
