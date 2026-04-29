package org.scooterrental.service.serviceimpl;

import lombok.RequiredArgsConstructor;
import org.scooterrental.model.entity.Tariff;
import org.scooterrental.model.enums.PaymentType;
import org.scooterrental.model.exception.TariffNotFoundException;
import org.scooterrental.model.exception.ValueLessZeroException;
import org.scooterrental.repository.daointerface.TariffDao;
import org.scooterrental.service.dto.TariffCreateDto;
import org.scooterrental.service.dto.TariffResponseDto;
import org.scooterrental.service.mapper.TariffMapper;
import org.scooterrental.service.serviceinterface.TariffService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TariffServiceImpl implements TariffService {
    private final TariffDao tariffDao;
    private final TariffMapper tariffMapper;
    private static final Logger logger = LoggerFactory.getLogger(TariffServiceImpl.class);

    @Override
    public TariffResponseDto addNewTariff(TariffCreateDto tariffCreateDto) {
        Tariff tariff = tariffMapper.toTariffEntity(tariffCreateDto);
        tariffDao.create(tariff);
        logger.info("Новый тариф успешно добавлен");
        return tariffMapper.toTariffDto(tariff);
    }

    @Override
    public TariffResponseDto setNewPaymentType(Long tariffId, PaymentType newPaymentType) {
        Tariff tariff = getTariffOrThrow(tariffId);
        tariff.setPaymentType(newPaymentType);
        tariffDao.update(tariff);
        logger.info("Тарифу {} успешно присвоен новый способ оплаты", tariffId);
        return tariffMapper.toTariffDto(tariff);
    }

    @Override
    public TariffResponseDto setNewPrice(Long tariffId, BigDecimal newPrice) {
        Tariff tariff = getTariffOrThrow(tariffId);
        if (newPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValueLessZeroException();
        }
        tariff.setPrice(newPrice);
        tariffDao.update(tariff);
        logger.info("Тарифу {} успешно присвоена новая цена", tariffId);
        return tariffMapper.toTariffDto(tariff);
    }

    @Override
    public TariffResponseDto setNewDiscount(Long tariffId, int newDiscount) {
        Tariff tariff = getTariffOrThrow(tariffId);
        if (newDiscount < 0 || newDiscount > 100) {
            throw new IllegalArgumentException("Скидка не может выходить за диапазон 0-100");
        }
        tariff.setDiscount(newDiscount);
        tariffDao.update(tariff);
        logger.info("Тарифу {} успешно присвоена новая скидка", tariffId);
        return tariffMapper.toTariffDto(tariff);
    }

    @Override
    public TariffResponseDto getTariff(Long tariffId) {
        Tariff tariff = getTariffOrThrow(tariffId);
        logger.info("Тариф {} успешно запрошен", tariffId);
        return tariffMapper.toTariffDto(tariff);
    }

    @Override
    public List<TariffResponseDto> getAllTariffs() {
        List<TariffResponseDto> list = tariffDao.findTariffs().stream()
                .map(tariffMapper::toTariffDto)
                .toList();
        logger.info("Список всех тарифов успешно запрошен");
        return list;
    }

    private Tariff getTariffOrThrow(Long tariffId) {
        Tariff tariff = tariffDao.findTariff(tariffId);
        if (tariff == null) {
            throw new TariffNotFoundException();
        }
        return tariff;
    }
}
