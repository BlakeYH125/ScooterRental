package org.scooterrental.service.serviceinterface;

import org.scooterrental.model.enums.PaymentType;
import org.scooterrental.service.dto.TariffCreateDto;
import org.scooterrental.service.dto.TariffResponseDto;

import java.math.BigDecimal;
import java.util.List;

public interface TariffService {
    TariffResponseDto addNewTariff(TariffCreateDto tariffCreateDto);
    TariffResponseDto setNewPaymentType(Long tariffId, PaymentType newPaymentType);
    TariffResponseDto setNewPrice(Long tariffId, BigDecimal newPrice);
    TariffResponseDto setNewDiscount(Long tariffId, int newDiscount);
    TariffResponseDto getTariff(Long tariffId);
    List<TariffResponseDto> getAllTariffs();
}
