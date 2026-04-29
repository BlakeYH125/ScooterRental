package org.scooterrental.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TariffCreateDto {

    @NotBlank(message = "Способ оплаты обязателен")
    private String paymentType;

    @NotNull(message = "Цена обязательна")
    @PositiveOrZero(message = "Цена не может быть отрицательной")
    private BigDecimal price;

    @PositiveOrZero(message = "Скидка не может быть отрицательной")
    private int discount;
}
