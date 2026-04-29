package org.scooterrental.service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TariffResponseDto {

    private Long tariffId;

    private String paymentType;

    private BigDecimal price;

    private int discount;
}
