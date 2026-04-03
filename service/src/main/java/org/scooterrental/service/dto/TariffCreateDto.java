package org.scooterrental.service.dto;

import java.math.BigDecimal;

public class TariffCreateDto {

    private String paymentType;

    private BigDecimal price;

    private int discount;

    public TariffCreateDto() {
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }
}
