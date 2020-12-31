package lk.ijse.dep.web.api;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import lk.ijse.dep.web.model.Item;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author:Tharanga Mahavila <tharangamahavila@gmail.com>
 * @since : 2020-12-09
 **/
@WebServlet(name = "ItemServlet", urlPatterns = ("/items"))
public class ItemServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Jsonb jsonb = JsonbBuilder.create();
        Item item = jsonb.fromJson(request.getReader(), Item.class);
        if(item.getId()==null || item.getName()==null || item.getPrice()==0.0 || item.getQty()==0.0){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if(!item.getId().matches("I\\d{3}")||item.getName().trim().isEmpty()){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        BasicDataSource cp = (BasicDataSource) getServletContext().getAttribute("cp");
            try(Connection connection = cp.getConnection()){
                PreparedStatement pst = connection.prepareStatement("INSERT INTO item VALUES (?,?,?,?)");
                pst.setObject(1,item.getId());
                pst.setObject(2,item.getName());
                pst.setObject(3,item.getQty());
                pst.setObject(4,item.getPrice());
                if(pst.executeUpdate()>0){
                    response.setStatus(HttpServletResponse.SC_CREATED);
                }else {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            } catch (SQLIntegrityConstraintViolationException e){
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } catch (JsonbException e){
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        BasicDataSource cp = (BasicDataSource) getServletContext().getAttribute("cp");
        response.setContentType("application/json");
            try(Connection connection = cp.getConnection();){
                PrintWriter out=response.getWriter();
                PreparedStatement pstm = connection.prepareStatement("SELECT * FROM item"+((id!=null)?" WHERE id=?":""));
                if(id!=null){
                    pstm.setObject(1,id);
                }
                ResultSet rs = pstm.executeQuery();
                List<Item> itemList=new ArrayList<>();
                while (rs.next()){
                    id=rs.getString(1);
                    String name=rs.getString(2);
                    double qty=Double.parseDouble(rs.getString(3));
                    double price=Double.parseDouble(rs.getString(4));
                    itemList.add(new Item(id,name,qty,price));
                }
                if(id!=null && itemList.isEmpty()){
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                }else {
                    Jsonb jsonb = JsonbBuilder.create();
                    out.println(jsonb.toJson(itemList));
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        if(id==null || !id.matches("I\\d{3}")){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        Jsonb jsonb = JsonbBuilder.create();
        Item item = jsonb.fromJson(req.getReader(), Item.class);
        if(item.getId()==null || item.getName()==null || item.getQty()==0.0 || item.getPrice()==0.0){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if(!item.getId().matches("I\\d{3}") || item.getId().trim().isEmpty()|| item.getName().trim().isEmpty()){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        BasicDataSource cp = (BasicDataSource) getServletContext().getAttribute("cp");
        try(Connection connection = cp.getConnection()){
            PreparedStatement pstm = connection.prepareStatement("SELECT * FROM item WHERE id=?");
            pstm.setObject(1,id);
            if(pstm.executeQuery().next()){
                pstm =connection.prepareStatement("UPDATE item SET name=?, qty=?, price=? WHERE id=?");
                pstm.setObject(1,item.getName());
                pstm.setObject(2,item.getQty());
                pstm.setObject(3,item.getPrice());
                pstm.setObject(4,item.getId());
                if(pstm.executeUpdate()>0){
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                } else {
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            }else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (JsonbException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        if(id==null || !id.matches("I\\d{3}")){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        BasicDataSource cp = (BasicDataSource) getServletContext().getAttribute("cp");
        try(Connection connection = cp.getConnection()){
            PreparedStatement pstm = connection.prepareStatement("SELECT * FROM item WHERE id=?");
            pstm.setObject(1,id);
            if(pstm.executeQuery().next()){
                pstm=connection.prepareStatement("DELETE FROM item WHERE id=?");
                pstm.setObject(1,id);
                if(pstm.executeUpdate()>0){
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                }else {
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            }else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLIntegrityConstraintViolationException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
