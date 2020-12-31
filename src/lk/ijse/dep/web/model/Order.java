package lk.ijse.dep.web.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author:Tharanga Mahavila <tharangamahavila@gmail.com>
 * @since : 2020-12-30
 **/
public class Order {
    String orderID;
    double subTotal;
    double discount;
    double netTotal;
    List<OrderItem> orderItem;

    public Order() {
    }

    public Order(String orderID, double subTotal, double discount, double netTotal, List<OrderItem> orderItem) {
        this.orderID = orderID;
        this.subTotal = subTotal;
        this.discount = discount;
        this.netTotal = netTotal;
        this.orderItem = orderItem;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getNetTotal() {
        return netTotal;
    }

    public void setNetTotal(double netTotal) {
        this.netTotal = netTotal;
    }

    public List<OrderItem> getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(List<OrderItem> orderItem) {
        this.orderItem = orderItem;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderID='" + orderID + '\'' +
                ", subTotal=" + subTotal +
                ", discount=" + discount +
                ", netTotal=" + netTotal +
                ", orderItem=" + orderItem +
                '}';
    }
}
