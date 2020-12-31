package lk.ijse.dep.web.api;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import lk.ijse.dep.web.model.Order;
import lk.ijse.dep.web.model.OrderItem;
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
 * @since : 2020-12-30
 **/
@WebServlet(name = "OrderServlet", urlPatterns = ("/orders"))
public class OrderServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Jsonb jsonb = JsonbBuilder.create();
        Order order = jsonb.fromJson(request.getReader(), Order.class);
        if(order.getOrderID()==null || order.getSubTotal()==0.0 || order.getNetTotal()==0.0 || order.getOrderItem().size()<=0){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if(!order.getOrderID().matches("O\\d{3}")){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        for (int i=0; i<order.getOrderItem().size(); i++){
            if(!order.getOrderItem().get(i).getItemCode().matches("I\\d{3}") || !order.getOrderItem().get(i).getOrderId().matches("O\\d{3}")
            || order.getOrderItem().get(i).getPrice()==0.0 || order.getOrderItem().get(i).getQty()==0.0){
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }
        BasicDataSource cp = (BasicDataSource) getServletContext().getAttribute("cp");
        Connection connection = null;
        try{
            connection = cp.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement pstm;
            for (int i=0; i<order.getOrderItem().size(); i++){
                pstm = connection.prepareStatement("INSERT INTO order_item VALUES (?,?,?,?)");
                pstm.setObject(1,order.getOrderItem().get(i).getOrderId());
                pstm.setObject(2,order.getOrderItem().get(i).getItemCode());
                pstm.setObject(3,order.getOrderItem().get(i).getPrice());
                pstm.setObject(4,order.getOrderItem().get(i).getQty());
                pstm.executeUpdate();
            }
            pstm = connection.prepareStatement("INSERT INTO orders VALUES (?,?,?,?)");
            pstm.setObject(1,order.getOrderID());
            pstm.setObject(2,order.getSubTotal());
            pstm.setObject(3,order.getDiscount());
            pstm.setObject(4,order.getNetTotal());
            if(pstm.executeUpdate()>0){
                response.setStatus(HttpServletResponse.SC_CREATED);
                connection.commit();
            }else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                connection.rollback();
            }
        } catch (SQLIntegrityConstraintViolationException throwables) {
            throwables.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            if(connection!=null){
                try {
                    connection.rollback();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            if(connection!=null){
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (JsonbException e){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
           if(connection!=null){
               try {
                   connection.rollback();
               } catch (SQLException ex) {
                   ex.printStackTrace();
               }
           }
        } finally {
            if(connection!=null){
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        if(id!=null && !id.matches("O\\d{3}")){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        BasicDataSource cp = (BasicDataSource) getServletContext().getAttribute("cp");
        response.setContentType("application/json");
        try(Connection connection = cp.getConnection()){
            PrintWriter out = response.getWriter();
            PreparedStatement pstm = connection.prepareStatement("SELECT * FROM orders"+((id!=null)?" WHERE order_id=?":""));
            if(id!=null){
                pstm.setObject(1,id);
            }
            ResultSet rs = pstm.executeQuery();
            List<Order> orderList = new ArrayList<>();
            while (rs.next()){
                id = rs.getString(1);
                double subTotal = Double.parseDouble(rs.getString(2));
                double discount = Double.parseDouble(rs.getString(3));
                double netTotal = Double.parseDouble(rs.getString(4));
                pstm = connection.prepareStatement("SELECT * FROM order_item WHERE order_id=?");
                pstm.setObject(1,id);
                ResultSet rs1 = pstm.executeQuery();
                ArrayList<OrderItem> orderItemList = new ArrayList<>();
                while (rs1.next()){
                    orderItemList.add(new OrderItem(rs1.getString(1),rs1.getString(2),
                            Double.parseDouble(rs1.getString(3)),Double.parseDouble(rs1.getString(4))));
                }
                orderList.add(new Order(id,subTotal,discount,netTotal,orderItemList));
            }
            if(id!=null && orderList.isEmpty()){
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }else{
                Jsonb jsonb = JsonbBuilder.create();
                out.println(jsonb.toJson(orderList));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
