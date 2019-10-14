package com.dmf.resource1.filter;

import com.alibaba.fastjson.JSONObject;
import com.dmf.resource1.module.BodyReaderHttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public class ParamFilter implements Filter {

    /**
     * 登录登录认证的key
     */
    private String POST_AUTHORIZATION = "authorization";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("==========init ParamFilter==========");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        crossDomainRequest(resp);
        //心跳请求，放过拦截
        if ("OPTIONS".equals(httpServletRequest.getMethod())) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        String param = getBodyData(httpServletRequest);
        BodyReaderHttpServletRequestWrapper request = new BodyReaderHttpServletRequestWrapper(httpServletRequest, param);
        filterChain.doFilter(request, servletResponse);
    }

    /**
     * 处理请求体
     *
     * @param request
     * @return
     */
    private String getBodyData(HttpServletRequest request) {
        String result = null;
        String contentType = request.getContentType();
        if (contentType.contains("application/json")) {
            StringBuffer data = new StringBuffer();
            String line = null;
            try (BufferedReader reader = request.getReader()) {
                while (null != (line = reader.readLine())) {
                    data.append(line);
                }
            } catch (Exception e) {
                log.info("============解析请求体异常==========", e);
            }
            result = data.toString();
        } else if (contentType.contains("multipart/form-data")) {
            String auth = request.getParameter(POST_AUTHORIZATION);
            Map<String, String> map = new HashMap<String, String>();
            map.put(POST_AUTHORIZATION, auth);
            result = JSONObject.toJSONString(map);
        }
        log.info("============获取token失败==========");
        return result;
    }

    /**
     * Title: crossDomainRequest<br/>
     * Description: 放过跨域请求<br/>
     *
     * @param resp
     * @author: weizenghui<br       />
     */
    private void crossDomainRequest(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        resp.setHeader("Access-Control-Max-Age", "3600");
        resp.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept,Authorization");
        resp.setHeader("Access-Control-Request-Headers", "POST");
    }

    @Override
    public void destroy() {
        log.info("==========destory ParamFilter==========");
    }
}
