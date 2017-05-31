package filter;



import common.util.Cache;
import common.util.CacheManager;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by heren on 2016/6/1.
 */
public class ApiFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request ;
        HttpServletResponse httpServletResponse = (HttpServletResponse)response ;
        String acceToken = "" ;
        String clientId = httpServletRequest.getRemoteHost() ;
        StringBuffer requestURL = httpServletRequest.getRequestURL();
        String path = requestURL.toString() ;

        if(path.contains("sso")||path.contains("check-login")||path.contains("exp-prepare")){
            chain.doFilter(request,response);
            return ;
        }

        HttpSession httpSession = httpServletRequest.getSession() ;
        acceToken = (String) httpSession.getAttribute("ACCESS_TOKEN");


        Cache cache= CacheManager.getCacheInfo("code_" + clientId) ;
        if(cache !=null&&cache.getValue().equals(acceToken)){
            chain.doFilter(request,response);
        }else{
            ((HttpServletResponse) response).sendError(302,"has no right");
        }
        System.out.println(acceToken);
    }

    @Override
    public void destroy() {

    }
}
