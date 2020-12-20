package lk.ijse.dep.web.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author:Tharanga Mahavila <tharangamahavila@gmail.com>
 * @since : 2020-12-20
 **/
@WebFilter(filterName = "corsFilter", urlPatterns = "/*")
public class corsFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse resp, FilterChain chain) throws IOException, ServletException {
        resp.addHeader("Access-Control-Allow-Origin","http://localhost:3000");
        resp.addHeader("Access-Control-Allow-Headers","Content-Type");
        resp.addHeader("Access-Control-Allow-Methods","POST, PUT, GET, OPTIONS, DELETE");
        chain.doFilter(request, resp);
    }
}
