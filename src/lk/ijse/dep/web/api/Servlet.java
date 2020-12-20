package lk.ijse.dep.web.api;

import jakarta.json.Json;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import lk.ijse.dep.web.model.Customer;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author:Tharanga Mahavila <tharangamahavila@gmail.com>
 * @since : 2020-12-08
 **/
@javax.servlet.annotation.WebServlet(name = "Servlet",urlPatterns = ("/customers"))
public class Servlet extends javax.servlet.http.HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        BasicDataSource cp = (BasicDataSource) getServletContext().getAttribute("cp");
        resp.setContentType("application/json");
        try(Connection connection = cp.getConnection()){
            PrintWriter out =resp.getWriter();
                PreparedStatement pstm = connection.prepareStatement("SELECT * FROM customer"+((id!=null)? " WHERE id=?":""));
                if(id!=null){
                    pstm.setObject(1,id);
                }
                ResultSet rst = pstm.executeQuery();
                List<Customer> customerList = new ArrayList<>();
                while (rst.next()){
                    id=rst.getString(1);
                    String name=rst.getString(2);
                    String address=rst.getString(3);
                    customerList.add(new Customer(id,name,address));
                }
                if(id!=null && customerList.isEmpty()){
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                }else {
                    Jsonb jsonb = JsonbBuilder.create();
                    out.println(jsonb.toJson(customerList));
                    connection.close();
                }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BasicDataSource cp = (BasicDataSource) getServletContext().getAttribute("cp");

        try(Connection connection = cp.getConnection();){
            Jsonb jsonb = JsonbBuilder.create();
            Customer customer = jsonb.fromJson(req.getReader(), Customer.class);

            if(customer.getId()==null || customer.getName()==null || customer.getAddress()==null){
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            if(!customer.getId().matches("C\\d{3}")||customer.getName().trim().isEmpty()||customer.getAddress().trim().isEmpty()){
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            PreparedStatement pstm=connection.prepareStatement("INSERT INTO customer VALUES (?,?,?)");
            pstm.setString(1,customer.getId());
            pstm.setString(2,customer.getName());
            pstm.setString(3,customer.getAddress());
            if(pstm.executeUpdate()>0){
                resp.setStatus(HttpServletResponse.SC_CREATED);
            }else{
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            connection.close();
        } catch (SQLIntegrityConstraintViolationException ex) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }catch (SQLException ex){
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ex.printStackTrace();
        }catch (JsonbException ex){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id=req.getParameter("id");
        if(id==null || !id.matches("C\\d{3}")){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        BasicDataSource cp = (BasicDataSource) getServletContext().getAttribute("cp");

        try(Connection connection = cp.getConnection();){
            Jsonb jsonb = JsonbBuilder.create();
            Customer customer = jsonb.fromJson(req.getReader(), Customer.class);

            if(customer.getId()==null || customer.getName()==null || customer.getAddress()==null){
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            if(!customer.getId().matches("C\\d{3}")||customer.getName().trim().isEmpty()||customer.getAddress().trim().isEmpty()){
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            PreparedStatement pstm = connection.prepareStatement("SELECT  * FROM customer WHERE id=?");
            pstm.setObject(1,id);
            if(pstm.executeQuery().next()) {
                pstm = connection.prepareStatement("UPDATE customer SET name=?,address=? WHERE id=?");
                pstm.setString(1, customer.getName());
                pstm.setString(2, customer.getAddress());
                pstm.setString(3, customer.getId());
                if (pstm.executeUpdate() > 0) {
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            }else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }catch (JsonbException ex){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id=req.getParameter("id");
        if(id==null || !id.matches("C\\d{3}")){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        BasicDataSource cp = (BasicDataSource) getServletContext().getAttribute("cp");
        try(Connection connection = cp.getConnection();){

            PreparedStatement pstm = connection.prepareStatement("SELECT  * FROM customer WHERE id=?");
            pstm.setObject(1,id);
            if(pstm.executeQuery().next()) {
                pstm = connection.prepareStatement("DELETE FROM customer WHERE id=?");
                pstm.setString(1, id);
                if (pstm.executeUpdate() > 0) {
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            }else{
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }catch (SQLIntegrityConstraintViolationException ex){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
        catch (SQLException throwables) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throwables.printStackTrace();
        }
    }
}
