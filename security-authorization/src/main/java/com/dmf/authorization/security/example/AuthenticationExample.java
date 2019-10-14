package com.dmf.authorization.security.example;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * spring security 身份认证基本流程
 * 1、用户名和密码被过滤器获取到，填充到Authentication，通常情况下是UsernamePasswordAuthenticationToken实现类
 * 2、AuthenticationManager身份认证管理器认证Authentication里面的信息
 * 3、认证成功后，AuthenticationManager身份认证管理器会返回一个信息更完善的Authentication(权限信息、身份信息、细节信息)，但是密码通常会被删除
 * 4、SecurityContextHolder.getContext()将第3步的Authentication通过SecurityContextHolder.getContext().setAuthentication()设置进去
 */
public class AuthenticationExample {
    private static AuthenticationManager am = new SampleAuthenticationManager();

    public static void main(String[] args) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        while(true) {
            System.out.println("Please enter your username:");
            String name = in.readLine();
            System.out.println("Please enter your password:");
            String password = in.readLine();
            try {
                // 第1步
                Authentication request = new UsernamePasswordAuthenticationToken(name, password);
                // 第2步、第3步
                Authentication result = am.authenticate(request);
                // 第4步
                SecurityContextHolder.getContext().setAuthentication(result);
                break;
            } catch(AuthenticationException e) {
                System.out.println("Authentication failed: " + e.getMessage());
            }
        }
        System.out.println("Successfully authenticated. Security context contains: " +
                SecurityContextHolder.getContext().getAuthentication());
    }
}

/**
 * 自定义的AuthenticationManager,用于认证Authentication对象
 */
class SampleAuthenticationManager implements AuthenticationManager {
    static final List<GrantedAuthority> AUTHORITIES = new ArrayList<GrantedAuthority>();

    static {
        AUTHORITIES.add(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        if (auth.getName().equals(auth.getCredentials())) {
            return new UsernamePasswordAuthenticationToken(auth.getName(),
                    auth.getCredentials(), AUTHORITIES);
        }
        throw new BadCredentialsException("Bad Credentials");
    }
}
