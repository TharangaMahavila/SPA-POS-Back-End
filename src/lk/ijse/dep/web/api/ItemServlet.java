package lk.ijse.dep.web.api;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import lk.ijse.dep.web.model.Item;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author:Tharanga Mahavila <tharangamahavila@gmail.com>
 * @since : 2020-12-09
 **/
@WebServlet(name = "ItemServlet", urlPatterns = ("/items"))
public class ItemServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        BasicDataSource cp = (BasicDataSource) getServletContext().getAttribute("cp");
        response.addHeader("Access-Control-Allow-Origin","http://localhost:3000");
        response.setContentType("application/json");
        try(PrintWriter out=response.getWriter()){
            try{
                Connection connection = cp.getConnection();
                Statement stm = connection.createStatement();
                ResultSet rs = stm.executeQuery("SELECT * FROM item");
                List<Item> itemList=new ArrayList<>();
                while (rs.next()){
                    String id=rs.getString(1);
                    String name=rs.getString(2);
                    double qty=Double.parseDouble(rs.getString(3));
                    double price=Double.parseDouble(rs.getString(4));
                    itemList.add(new Item(id,name,qty,price));
                }
                Jsonb jsonb= JsonbBuilder.create();
                out.println(jsonb.toJson(itemList));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
