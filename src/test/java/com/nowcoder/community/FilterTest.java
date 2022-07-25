package com.nowcoder.community;
import com.nowcoder.community.utils.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FilterTest {

    @Autowired
    SensitiveFilter sensitiveFilter;

    @Test
    public void testFilter(){
        String s = "我就他妈的想赌博了怎么吸**毒样！";
        String filter = sensitiveFilter.filter(s);
        System.out.println(filter);
    }
}
