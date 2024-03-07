package com.vote.interceptor;

import com.vote.util.JwtUtils;
import com.vote.util.StringUtil;
import io.jsonwebtoken.Claims;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 鉴权拦截器
 */
public class SysInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path=request.getRequestURI();
        System.out.println("path="+path);
        if(handler instanceof HandlerMethod){
            String token = request.getHeader("token");
            System.out.println("token="+token);
            if(StringUtil.isEmpty(token)){
                System.out.println("token为空！");
                throw new RuntimeException("签名验证不存在！");
            }else{
                Claims claims = JwtUtils.validateJWT(token).getClaims();
                if(claims!=null){
                    System.out.println("鉴权成功");
                    return true;
                }else{
                    System.out.println("鉴权失败");
                    throw new RuntimeException("鉴权失败！");
                }
            }
        }else{
            return true;
        }
    }
}
