package lk.ijse.dep.web.model;

/**
 * @author:Tharanga Mahavila <tharangamahavila@gmail.com>
 * @since : 2020-12-30
 **/
public class OrderItem {
    String orderId;
    String itemCode;
    double price;
    double qty;

    public OrderItem() {
    }

    public OrderItem(String orderId, String itemCode, double price, double qty) {
        this.orderId = orderId;
        this.itemCode = itemCode;
        this.price = price;
        this.qty = qty;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "orderId='" + orderId + '\'' +
                ", itemCode='" + itemCode + '\'' +
                ", price=" + price +
                ", qty=" + qty +
                '}';
    }
}
