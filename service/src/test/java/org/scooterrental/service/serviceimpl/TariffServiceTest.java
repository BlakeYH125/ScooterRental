package org.scooterrental.service.serviceimpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scooterrental.model.entity.Tariff;
import org.scooterrental.model.enums.PaymentType;
import org.scooterrental.model.exception.TariffNotFoundException;
import org.scooterrental.model.exception.ValueLessZeroException;
import org.scooterrental.repository.daointerface.TariffDao;
import org.scooterrental.service.dto.TariffCreateDto;
import org.scooterrental.service.dto.TariffResponseDto;
import org.scooterrental.service.mapper.TariffMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TariffServiceTest {

    @Mock
    private TariffDao tariffDao;

    @Mock
    private TariffMapper tariffMapper;

    @InjectMocks
    private TariffServiceImpl tariffService;

    @Test
    void addNewTariff_ShouldReturnDto_WhenAllCorrect() {
        Long tariffId = 1L;
        BigDecimal price = new BigDecimal(500);

        Tariff tariff = new Tariff();
        tariff.setTariffId(tariffId);
        tariff.setPrice(price);

        TariffResponseDto expected = new TariffResponseDto();
        expected.setTariffId(tariffId);
        expected.setPrice(price);

        TariffCreateDto tariffCreateDto = new TariffCreateDto();
        tariffCreateDto.setPrice(price);

        when(tariffMapper.toTariffDto(tariff)).thenReturn(expected);
        when(tariffMapper.toTariffEntity(tariffCreateDto)).thenReturn(tariff);

        TariffResponseDto actual = tariffService.addNewTariff(tariffCreateDto);

        assertNotNull(actual);
        assertEquals(expected.getTariffId(), actual.getTariffId());
        assertEquals(expected.getPrice(), actual.getPrice());

        verify(tariffDao, times(1)).create(tariff);
        verify(tariffMapper, times(1)).toTariffDto(tariff);
        verify(tariffMapper, times(1)).toTariffEntity(tariffCreateDto);
    }

    @Test
    void setNewPaymentType_ShouldReturnDto_WhenAllCorrect() {
        Long tariffId = 1L;
        PaymentType newPaymentType = PaymentType.SEASON_TICKET;

        Tariff tariff = new Tariff();
        tariff.setTariffId(tariffId);

        TariffResponseDto expected = new TariffResponseDto();
        expected.setTariffId(tariffId);
        expected.setPaymentType(newPaymentType.name());

        when(tariffDao.findTariff(tariffId)).thenReturn(tariff);
        when(tariffMapper.toTariffDto(tariff)).thenReturn(expected);

        TariffResponseDto actual = tariffService.setNewPaymentType(tariffId, newPaymentType);

        assertNotNull(actual);
        assertEquals(expected.getPaymentType(), actual.getPaymentType());

        verify(tariffDao, times(1)).update(tariff);
        verify(tariffDao, times(1)).findTariff(tariffId);
        verify(tariffMapper, times(1)).toTariffDto(tariff);
    }

    @Test
    void setNewPaymentType_ShouldThrowTariffNotFoundException_WhenTariffNotFound() {
        Long tariffId = 1L;
        PaymentType newPaymentType = PaymentType.SEASON_TICKET;

        Tariff tariff = new Tariff();
        tariff.setTariffId(tariffId);

        when(tariffDao.findTariff(tariffId)).thenReturn(null);

        assertThrows(TariffNotFoundException.class, () -> tariffService.setNewPaymentType(tariffId, newPaymentType));

        verify(tariffDao, times(1)).findTariff(tariffId);
        verify(tariffDao, never()).update(tariff);
        verify(tariffMapper, never()).toTariffDto(tariff);
    }

    @Test
    void setNewPrice_ShouldReturnDto_WhenAllCorrect() {
        Long tariffId = 1L;
        BigDecimal newPrice = new BigDecimal(500);

        Tariff tariff = new Tariff();
        tariff.setTariffId(tariffId);

        TariffResponseDto expected = new TariffResponseDto();
        expected.setTariffId(tariffId);
        expected.setPrice(newPrice);

        when(tariffDao.findTariff(tariffId)).thenReturn(tariff);
        when(tariffMapper.toTariffDto(tariff)).thenReturn(expected);

        TariffResponseDto actual = tariffService.setNewPrice(tariffId, newPrice);

        assertNotNull(actual);
        assertEquals(expected.getPrice(), actual.getPrice());

        verify(tariffDao, times(1)).update(tariff);
        verify(tariffDao, times(1)).findTariff(tariffId);
        verify(tariffMapper, times(1)).toTariffDto(tariff);
    }

    @Test
    void setNewPrice_ShouldThrowTariffNotFoundException_WhenTariffNotFound() {
        Long tariffId = 1L;
        BigDecimal newPrice = new BigDecimal(500);

        Tariff tariff = new Tariff();
        tariff.setTariffId(tariffId);

        when(tariffDao.findTariff(tariffId)).thenReturn(null);

        assertThrows(TariffNotFoundException.class, () -> tariffService.setNewPrice(tariffId, newPrice));

        verify(tariffDao, times(1)).findTariff(tariffId);
        verify(tariffDao, never()).update(tariff);
        verify(tariffMapper, never()).toTariffDto(tariff);
    }

    @Test
    void setNewPrice_ShouldThrowValueLessZeroException_WhenValueLessZero() {
        Long tariffId = 1L;
        BigDecimal newPrice = new BigDecimal(-500);

        Tariff tariff = new Tariff();
        tariff.setTariffId(tariffId);

        when(tariffDao.findTariff(tariffId)).thenReturn(tariff);

        assertThrows(ValueLessZeroException.class, () -> tariffService.setNewPrice(tariffId, newPrice));

        verify(tariffDao, times(1)).findTariff(tariffId);
        verify(tariffDao, never()).update(tariff);
        verify(tariffMapper, never()).toTariffDto(tariff);
    }

    @Test
    void setNewDiscount_ShouldReturnDto_WhenAllCorrect() {
        Long tariffId = 1L;
        int newDiscount = 30;

        Tariff tariff = new Tariff();
        tariff.setTariffId(tariffId);

        TariffResponseDto expected = new TariffResponseDto();
        expected.setTariffId(tariffId);
        expected.setDiscount(newDiscount);

        when(tariffDao.findTariff(tariffId)).thenReturn(tariff);
        when(tariffMapper.toTariffDto(tariff)).thenReturn(expected);

        TariffResponseDto actual = tariffService.setNewDiscount(tariffId, newDiscount);

        assertNotNull(actual);
        assertEquals(expected.getDiscount(), actual.getDiscount());

        verify(tariffDao, times(1)).update(tariff);
        verify(tariffDao, times(1)).findTariff(tariffId);
        verify(tariffMapper, times(1)).toTariffDto(tariff);
    }

    @Test
    void setNewDiscount_ShouldThrowTariffNotFoundException_WhenTariffNotFound() {
        Long tariffId = 1L;
        int newDiscount = 30;

        Tariff tariff = new Tariff();
        tariff.setTariffId(tariffId);

        when(tariffDao.findTariff(tariffId)).thenReturn(null);

        assertThrows(TariffNotFoundException.class, () -> tariffService.setNewDiscount(tariffId, newDiscount));

        verify(tariffDao, times(1)).findTariff(tariffId);
        verify(tariffDao, never()).update(tariff);
        verify(tariffMapper, never()).toTariffDto(tariff);
    }

    @Test
    void setNewDiscount_ShouldThrowIllegalArgumentException_WhenValueIllegal() {
        Long tariffId = 1L;
        int newDiscount = 300;

        Tariff tariff = new Tariff();
        tariff.setTariffId(tariffId);

        when(tariffDao.findTariff(tariffId)).thenReturn(tariff);

        assertThrows(IllegalArgumentException.class, () -> tariffService.setNewDiscount(tariffId, newDiscount));

        verify(tariffDao, times(1)).findTariff(tariffId);
        verify(tariffDao, never()).update(tariff);
        verify(tariffMapper, never()).toTariffDto(tariff);
    }

    @Test
    void getTariff_ShouldReturnDto_WhenAllCorrect() {
        Long tariffId = 1L;

        Tariff tariff = new Tariff();
        tariff.setTariffId(tariffId);

        TariffResponseDto expected = new TariffResponseDto();
        expected.setTariffId(tariffId);

        when(tariffDao.findTariff(tariffId)).thenReturn(tariff);
        when(tariffMapper.toTariffDto(tariff)).thenReturn(expected);

        TariffResponseDto actual = tariffService.getTariff(tariffId);

        assertNotNull(actual);
        assertEquals(expected.getTariffId(), actual.getTariffId());

        verify(tariffDao, times(1)).findTariff(tariffId);
        verify(tariffMapper, times(1)).toTariffDto(tariff);
    }

    @Test
    void getTariff_ShouldThrowTariffNotFoundException_WhenTariffNotFound() {
        Long tariffId = 1L;

        Tariff tariff = new Tariff();
        tariff.setTariffId(tariffId);

        when(tariffDao.findTariff(tariffId)).thenReturn(null);

        assertThrows(TariffNotFoundException.class, () -> tariffService.getTariff(tariffId));

        verify(tariffDao, times(1)).findTariff(tariffId);
        verify(tariffMapper, never()).toTariffDto(tariff);
    }

    @Test
    void getAllTariffs_ShouldReturnListDto_WhenAllCorrect() {
        Long tariffId1 = 1L;
        Long tariffId2 = 2L;

        Tariff tariff1 = new Tariff();
        tariff1.setTariffId(tariffId1);

        Tariff tariff2 = new Tariff();
        tariff2.setTariffId(tariffId2);

        TariffResponseDto tariffResponseDto1 = new TariffResponseDto();
        tariffResponseDto1.setTariffId(tariffId1);

        TariffResponseDto tariffResponseDto2 = new TariffResponseDto();
        tariffResponseDto2.setTariffId(tariffId2);

        List<TariffResponseDto> expected = new ArrayList<>(List.of(tariffResponseDto1, tariffResponseDto2));

        when(tariffDao.findTariffs()).thenReturn(new ArrayList<>(List.of(tariff1, tariff2)));
        when(tariffMapper.toTariffDto(tariff1)).thenReturn(tariffResponseDto1);
        when(tariffMapper.toTariffDto(tariff2)).thenReturn(tariffResponseDto2);

        List<TariffResponseDto> actual = tariffService.getAllTariffs();

        assertNotNull(actual);
        assertEquals(expected, actual);

        verify(tariffDao, times(1)).findTariffs();
        verify(tariffMapper, times(2)).toTariffDto(any(Tariff.class));
    }
}
