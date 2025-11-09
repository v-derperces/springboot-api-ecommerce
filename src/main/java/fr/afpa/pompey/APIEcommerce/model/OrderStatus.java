package fr.afpa.pompey.APIEcommerce.model;

public enum OrderStatus {
    IN_PROGRESS("In progress"),
    PAID("Paid"),
    SHIPPED("Shipped"),
    DELIVERED("Delivered"),
    CANCELED("Canceled"),
    ;

    private String value;

    OrderStatus(String s) {
        this.value = s;
    }

    public String getValue() {
        return this.value;
    }

}
