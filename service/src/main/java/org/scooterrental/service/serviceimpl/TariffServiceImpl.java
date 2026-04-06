package org.scooterrental.service.serviceimpl;

import org.scooterrental.model.entity.Tariff;
import org.scooterrental.model.enums.PaymentType;
import org.scooterrental.model.exception.TariffNotFoundException;
import org.scooterrental.model.exception.ValueLessZeroException;
import org.scooterrental.repository.daointerface.TariffDao;
import org.scooterrental.service.dto.TariffCreateDto;
import org.scooterrental.service.dto.TariffResponseDto;
import org.scooterrental.service.mapper.TariffMapper;
import org.scooterrental.service.serviceinterface.TariffService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class TariffServiceImpl implements TariffService {
    private final TariffDao tariffDao;
    private final TariffMapper tariffMapper;

    public TariffServiceImpl(TariffDao tariffDao, TariffMapper tariffMapper) {
        this.tariffDao = tariffDao;
        this.tariffMapper = tariffMapper;
    }

    @Override
    public TariffResponseDto addNewTariff(TariffCreateDto tariffCreateDto) {
        Tariff tariff = tariffMapper.toTariffEntity(tariffCreateDto);
        tariffDao.create(tariff);
        return tariffMapper.toTariffDto(tariff);
    }

    @Override
    public TariffResponseDto setNewPaymentType(Long tariffId, PaymentType newPaymentType) {
        Tariff tariff = getTariffOrThrow(tariffId);
        tariff.setPaymentType(newPaymentType);
        tariffDao.update(tariff);
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
        return tariffMapper.toTariffDto(tariff);
    }

    @Override
    public TariffResponseDto getTariff(Long tariffId) {
        Tariff tariff = getTariffOrThrow(tariffId);
        return tariffMapper.toTariffDto(tariff);
    }

    @Override
    public List<TariffResponseDto> getAllTariffs() {
        return tariffDao.findTariffs().stream()
                .map(tariffMapper::toTariffDto)
                .toList();
    }

    private Tariff getTariffOrThrow(Long tariffId) {
        Tariff tariff = tariffDao.findTariff(tariffId);
        if (tariff == null) {
            throw new TariffNotFoundException();
        }
        return tariff;
    }
}
