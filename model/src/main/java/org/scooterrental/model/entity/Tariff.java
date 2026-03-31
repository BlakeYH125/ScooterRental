package org.scooterrental.model.entity;

import jakarta.persistence.*;
import org.scooterrental.model.eums.PaymentType;

import java.math.BigDecimal;

@Entity
@Table(name = "tariffs")
public class Tariff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tariff_id")
    private Long tariffId;

    @Column
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "discount")
    private int discount;

    public Tariff() {
    }

    public Tariff(PaymentType paymentType, BigDecimal price, int discount) {
        this.paymentType = paymentType;
        this.price = price;
        this.discount = discount;
    }

    public Long getTariffId() {
        return tariffId;
    }

    public void setTariffId(Long tariffId) {
        this.tariffId = tariffId;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
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
