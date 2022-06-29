package com.nowcoder.community.dao;
import org.springframework.stereotype.Repository;

@Repository
public class AlphaDaoImp2 implements AlphaDao{
    @Override
    public String select() {
        return "This is Eric";
    }
}
