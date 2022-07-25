package com.nowcoder.community.controller.advice;

import com.nowcoder.community.utils.CommunityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler
    public void exceptionHandler(Exception e, HttpServletRequest request, HttpServletResponse response){
        logger.error("服务器发生异常："+e.getMessage());
        for(StackTraceElement element: e.getStackTrace()){
            logger.error(element.toString());
        }

        String header = request.getHeader("x-requested-with");
        if("XMLHttpRequest".equals(header)){
            // 异步请求
            String msg = CommunityUtils.getJSONString(1, "服务器发生异常");
            try{
                response.setContentType("application/plain;charset=utf-8");
                response.getWriter().write(msg);
            }catch (IOException exception){
                logger.error("响应出错: " + exception.getMessage());
            }
        }else{
            try{
                response.sendRedirect(request.getContextPath() + "/error");
            }catch (IOException exception){
                logger.error("响应出错: " + exception.getMessage());
            }
        }

    }
}
