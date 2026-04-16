package org.scooterrental.service.serviceimpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scooterrental.model.entity.RentalPoint;
import org.scooterrental.model.entity.Scooter;
import org.scooterrental.model.enums.ScooterStatus;
import org.scooterrental.model.exception.*;
import org.scooterrental.repository.daointerface.RentalPointDao;
import org.scooterrental.repository.daointerface.ScooterDao;
import org.scooterrental.service.dto.ScooterCreateDto;
import org.scooterrental.service.dto.ScooterResponseDto;
import org.scooterrental.service.mapper.ScooterMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ScooterServiceTest {

    @Mock
    private ScooterDao scooterDao;

    @Mock
    private RentalPointDao rentalPointDao;

    @Mock
    private ScooterMapper scooterMapper;

    @InjectMocks
    private ScooterServiceImpl scooterService;

    @Test
    void addNewScooter_ShouldReturnDtoWhenAllCorrect() {
        Scooter scooter = new Scooter();
        scooter.setModel("RX");

        ScooterCreateDto scooterCreateDto = new ScooterCreateDto();
        scooterCreateDto.setModel("RX");

        ScooterResponseDto scooterResponseDto = new ScooterResponseDto();
        scooterResponseDto.setModel("RX");

        when(scooterMapper.toScooterDto(scooter)).thenReturn(scooterResponseDto);
        when(scooterMapper.toScooterEntity(scooterCreateDto)).thenReturn(scooter);

        ScooterResponseDto actual = scooterService.addNewScooter(scooterCreateDto);

        assertNotNull(actual);
        assertEquals(scooterResponseDto.getModel(), actual.getModel());

        verify(scooterDao, times(1)).create(scooter);
        verify(scooterMapper, times(1)).toScooterDto(scooter);
        verify(scooterMapper, times(1)).toScooterEntity(scooterCreateDto);
    }

    @Test
    void setNewScooterModel_ShouldReturnDto_WhenAllCorrect() {
        Long scooterId = 1L;
        String newScooterModel = "RX";
        Scooter scooter = new Scooter();
        scooter.setScooterId(scooterId);

        ScooterResponseDto expected = new ScooterResponseDto();
        expected.setScooterId(scooterId);
        expected.setModel(newScooterModel);

        when(scooterDao.findScooter(scooterId)).thenReturn(scooter);
        when(scooterMapper.toScooterDto(scooter)).thenReturn(expected);

        ScooterResponseDto actual = scooterService.setNewScooterModel(scooterId, newScooterModel);

        assertNotNull(actual);
        assertEquals(expected.getModel(), actual.getModel());

        verify(scooterDao, times(1)).findScooter(scooterId);
        verify(scooterDao, times(1)).update(scooter);
        verify(scooterMapper, times(1)).toScooterDto(scooter);
    }

    @Test
    void setNewScooterModel_ShouldThrowScooterNotFoundException_WhenScooterNotFound() {
        Long scooterId = 1L;

        when(scooterDao.findScooter(scooterId)).thenReturn(null);

        assertThrows(ScooterNotFoundException.class, () -> scooterService.setNewScooterModel(scooterId, "RX"));

        verify(scooterDao, times(1)).findScooter(scooterId);
        verify(scooterDao, never()).update(any(Scooter.class));
        verify(scooterMapper, never()).toScooterDto(any(Scooter.class));
    }

    @Test
    void setNewBatteryLevel_ShouldReturnDto_WhenAllCorrect() {
        Long scooterId = 1L;
        int newBatteryLevel = 55;
        Scooter scooter = new Scooter();
        scooter.setScooterId(scooterId);

        ScooterResponseDto expected = new ScooterResponseDto();
        expected.setScooterId(scooterId);
        expected.setBatteryLevel(newBatteryLevel);

        when(scooterDao.findScooter(scooterId)).thenReturn(scooter);
        when(scooterMapper.toScooterDto(scooter)).thenReturn(expected);

        ScooterResponseDto actual = scooterService.setNewBatteryLevel(scooterId, newBatteryLevel);

        assertNotNull(actual);
        assertEquals(expected.getBatteryLevel(), actual.getBatteryLevel());

        verify(scooterDao, times(1)).findScooter(scooterId);
        verify(scooterDao, times(1)).update(scooter);
        verify(scooterMapper, times(1)).toScooterDto(scooter);
    }

    @Test
    void setNewBatteryLevel_ShouldThrowScooterNotFoundException_WhenScooterNotFound() {
        Long scooterId = 1L;

        when(scooterDao.findScooter(scooterId)).thenReturn(null);

        assertThrows(ScooterNotFoundException.class, () -> scooterService.setNewBatteryLevel(scooterId, 55));

        verify(scooterDao, times(1)).findScooter(scooterId);
        verify(scooterDao, never()).update(any(Scooter.class));
        verify(scooterMapper, never()).toScooterDto(any(Scooter.class));
    }

    @Test
    void setNewBatteryLevel_ShouldThrowIllegalArgumentException_WhenIncorrectBatteryLevel() {
        Long scooterId = 1L;
        int newBatteryLevel = 555;
        Scooter scooter = new Scooter();
        scooter.setScooterId(scooterId);

        when(scooterDao.findScooter(scooterId)).thenReturn(scooter);

        assertThrows(IllegalArgumentException.class, () -> scooterService.setNewBatteryLevel(scooterId, newBatteryLevel));

        verify(scooterDao, times(1)).findScooter(scooterId);
        verify(scooterDao, never()).update(scooter);
        verify(scooterMapper, never()).toScooterDto(scooter);
    }

    @Test
    void rechargeBattery_ShouldReturnDto_WhenAllCorrect() {
        Long scooterId = 1L;
        Scooter scooter = new Scooter();
        scooter.setScooterId(scooterId);

        ScooterResponseDto expected = new ScooterResponseDto();
        expected.setScooterId(scooterId);
        expected.setBatteryLevel(100);

        when(scooterDao.findScooter(scooterId)).thenReturn(scooter);
        when(scooterMapper.toScooterDto(scooter)).thenReturn(expected);

        ScooterResponseDto actual = scooterService.rechargeBattery(scooterId);

        assertNotNull(actual);
        assertEquals(expected.getBatteryLevel(), actual.getBatteryLevel());

        verify(scooterDao, times(1)).findScooter(scooterId);
        verify(scooterDao, times(1)).update(scooter);
        verify(scooterMapper, times(1)).toScooterDto(scooter);
    }

    @Test
    void rechargeBattery_ShouldThrowScooterNotFoundException_WhenScooterNotFound() {
        Long scooterId = 1L;

        when(scooterDao.findScooter(scooterId)).thenReturn(null);

        assertThrows(ScooterNotFoundException.class, () -> scooterService.rechargeBattery(scooterId));

        verify(scooterDao, times(1)).findScooter(scooterId);
        verify(scooterDao, never()).update(any(Scooter.class));
        verify(scooterMapper, never()).toScooterDto(any(Scooter.class));
    }

    @Test
    void putScooterInUse_ShouldReturnDto_WhenAllCorrect() {
        Long scooterId = 1L;
        Long rentalPointId = 2L;
        Scooter scooter = new Scooter();
        scooter.setScooterId(scooterId);
        scooter.setBatteryLevel(100);

        ScooterResponseDto expected = new ScooterResponseDto();
        expected.setScooterId(scooterId);
        expected.setBatteryLevel(100);
        expected.setScooterStatus("AVAILABLE");
        expected.setRentalPointId(rentalPointId);

        RentalPoint rentalPoint = new RentalPoint();
        rentalPoint.setRentalPointId(rentalPointId);

        when(scooterDao.findScooter(scooterId)).thenReturn(scooter);
        when(rentalPointDao.findRentalPointById(rentalPointId)).thenReturn(rentalPoint);
        when(scooterMapper.toScooterDto(scooter)).thenReturn(expected);

        ScooterResponseDto actual = scooterService.putScooterInUse(scooterId, rentalPointId);

        assertNotNull(actual);
        assertEquals(expected.getScooterStatus(), actual.getScooterStatus());
        assertEquals(expected.getRentalPointId(), actual.getRentalPointId());

        verify(scooterDao, times(1)).findScooter(scooterId);
        verify(scooterDao, times(1)).update(scooter);
        verify(rentalPointDao, times(1)).findRentalPointById(rentalPointId);
        verify(scooterMapper, times(1)).toScooterDto(scooter);
    }

    @Test
    void putScooterInUse_ShouldThrowScooterNotFoundException_WhenScooterNotFound() {
        Long scooterId = 1L;
        Long rentalPointId = 2L;

        Scooter scooter = new Scooter();
        scooter.setScooterId(scooterId);
        scooter.setBatteryLevel(100);

        when(scooterDao.findScooter(scooterId)).thenReturn(null);

        assertThrows(ScooterNotFoundException.class, () -> scooterService.putScooterInUse(scooterId, rentalPointId));

        verify(scooterDao, times(1)).findScooter(scooterId);
        verify(scooterDao, never()).update(scooter);
        verify(rentalPointDao, never()).findRentalPointById(rentalPointId);
        verify(scooterMapper, never()).toScooterDto(scooter);
    }

    @Test
    void putScooterInUse_ShouldThrowLowBatteryLevelException_WhenLowBatteryLevel() {
        Long scooterId = 1L;
        Long rentalPointId = 2L;

        Scooter scooter = new Scooter();
        scooter.setScooterId(scooterId);
        scooter.setBatteryLevel(3);

        when(scooterDao.findScooter(scooterId)).thenReturn(scooter);

        assertThrows(LowBatteryLevelException.class, () -> scooterService.putScooterInUse(scooterId, rentalPointId));

        verify(scooterDao, times(1)).findScooter(scooterId);
        verify(scooterDao, never()).update(scooter);
        verify(rentalPointDao, never()).findRentalPointById(rentalPointId);
        verify(scooterMapper, never()).toScooterDto(scooter);
    }

    @Test
    void putScooterInUse_ShouldThrowScooterAlreadyInRentException_WhenScooterAlreadyInRent() {
        Long scooterId = 1L;
        Long rentalPointId = 2L;

        Scooter scooter = new Scooter();
        scooter.setScooterId(scooterId);
        scooter.setBatteryLevel(100);
        scooter.setScooterStatus(ScooterStatus.IN_RENT);

        when(scooterDao.findScooter(scooterId)).thenReturn(scooter);

        assertThrows(ScooterAlreadyInRentException.class, () -> scooterService.putScooterInUse(scooterId, rentalPointId));

        verify(scooterDao, times(1)).findScooter(scooterId);
        verify(scooterDao, never()).update(scooter);
        verify(rentalPointDao, never()).findRentalPointById(rentalPointId);
        verify(scooterMapper, never()).toScooterDto(scooter);
    }

    @Test
    void putScooterInUse_ShouldThrowRentalPointNotFoundException_WhenRentalPointAlreadyInRent() {
        Long scooterId = 1L;
        Long rentalPointId = 2L;

        Scooter scooter = new Scooter();
        scooter.setScooterId(scooterId);
        scooter.setBatteryLevel(100);
        scooter.setScooterStatus(ScooterStatus.IN_WAREHOUSE);

        when(scooterDao.findScooter(scooterId)).thenReturn(scooter);
        when(rentalPointDao.findRentalPointById(rentalPointId)).thenReturn(null);

        assertThrows(RentalPointNotFoundException.class, () -> scooterService.putScooterInUse(scooterId, rentalPointId));

        verify(scooterDao, times(1)).findScooter(scooterId);
        verify(scooterDao, never()).update(scooter);
        verify(rentalPointDao, times(1)).findRentalPointById(rentalPointId);
        verify(scooterMapper, never()).toScooterDto(scooter);
    }

    @Test
    void putScooterInWarehouse_ShouldReturnDto_WhenAllCorrect() {
        Long scooterId = 1L;
        Scooter scooter = new Scooter();
        scooter.setScooterId(scooterId);
        scooter.setBatteryLevel(100);
        scooter.setScooterStatus(ScooterStatus.AVAILABLE);

        ScooterResponseDto expected = new ScooterResponseDto();
        expected.setScooterId(scooterId);
        expected.setBatteryLevel(100);
        expected.setScooterStatus("IN_WAREHOUSE");

        when(scooterDao.findScooter(scooterId)).thenReturn(scooter);
        when(scooterMapper.toScooterDto(scooter)).thenReturn(expected);

        ScooterResponseDto actual = scooterService.putScooterInWarehouse(scooterId);

        assertNotNull(actual);
        assertEquals(expected.getScooterStatus(), actual.getScooterStatus());

        verify(scooterDao, times(1)).findScooter(scooterId);
        verify(scooterDao, times(1)).update(scooter);
        verify(scooterMapper, times(1)).toScooterDto(scooter);
    }

    @Test
    void putScooterInWarehouse_ShouldThrowScooterNotFoundException_WhenScooterNotFound() {
        Long scooterId = 1L;

        Scooter scooter = new Scooter();
        scooter.setScooterId(scooterId);
        scooter.setBatteryLevel(100);

        when(scooterDao.findScooter(scooterId)).thenReturn(null);

        assertThrows(ScooterNotFoundException.class, () -> scooterService.putScooterInWarehouse(scooterId));

        verify(scooterDao, times(1)).findScooter(scooterId);
        verify(scooterDao, never()).update(scooter);
        verify(scooterMapper, never()).toScooterDto(scooter);
    }

    @Test
    void putScooterInWarehouse_ShouldThrowScooterInWarehouseException_WhenScooterAlreadyInWarehouse() {
        Long scooterId = 1L;

        Scooter scooter = new Scooter();
        scooter.setScooterId(scooterId);
        scooter.setBatteryLevel(100);
        scooter.setScooterStatus(ScooterStatus.IN_WAREHOUSE);

        when(scooterDao.findScooter(scooterId)).thenReturn(scooter);

        assertThrows(ScooterInWarehouseException.class, () -> scooterService.putScooterInWarehouse(scooterId));

        verify(scooterDao, times(1)).findScooter(scooterId);
        verify(scooterDao, never()).update(scooter);
        verify(scooterMapper, never()).toScooterDto(scooter);
    }

    @Test
    void putScooterInWarehouse_ShouldThrowScooterAlreadyInRentException_WhenScooterAlreadyInRent() {
        Long scooterId = 1L;

        Scooter scooter = new Scooter();
        scooter.setScooterId(scooterId);
        scooter.setBatteryLevel(100);
        scooter.setScooterStatus(ScooterStatus.IN_RENT);

        when(scooterDao.findScooter(scooterId)).thenReturn(scooter);

        assertThrows(ScooterAlreadyInRentException.class, () -> scooterService.putScooterInWarehouse(scooterId));

        verify(scooterDao, times(1)).findScooter(scooterId);
        verify(scooterDao, never()).update(scooter);
        verify(scooterMapper, never()).toScooterDto(scooter);
    }

    @Test
    void setScooterStatus_ShouldReturnDto_WhenAllCorrect() {
        Long scooterId = 1L;
        ScooterStatus newScooterStatus = ScooterStatus.IN_WAREHOUSE;

        Scooter scooter = new Scooter();
        scooter.setScooterId(scooterId);
        scooter.setScooterStatus(ScooterStatus.AVAILABLE);

        ScooterResponseDto expected = new ScooterResponseDto();
        expected.setScooterId(scooterId);
        expected.setScooterStatus("IN_WAREHOUSE");

        when(scooterDao.findScooter(scooterId)).thenReturn(scooter);
        when(scooterMapper.toScooterDto(scooter)).thenReturn(expected);

        ScooterResponseDto actual = scooterService.setNewScooterStatus(scooterId, newScooterStatus);

        assertNotNull(actual);
        assertEquals(expected.getScooterStatus(), actual.getScooterStatus());

        verify(scooterDao, times(1)).findScooter(scooterId);
        verify(scooterDao, times(1)).update(scooter);
        verify(scooterMapper, times(1)).toScooterDto(scooter);
    }

    @Test
    void setScooterStatus_ShouldThrowScooterAlreadyInRentException_WhenScooterInRentAndNewStatusIsInWarehouse() {
        Long scooterId = 1L;
        ScooterStatus newScooterStatus = ScooterStatus.IN_WAREHOUSE;

        Scooter scooter = new Scooter();
        scooter.setScooterId(scooterId);
        scooter.setScooterStatus(ScooterStatus.IN_RENT);

        when(scooterDao.findScooter(scooterId)).thenReturn(scooter);

        assertThrows(ScooterAlreadyInRentException.class, () -> scooterService.setNewScooterStatus(scooterId, newScooterStatus));

        verify(scooterDao, times(1)).findScooter(scooterId);
        verify(scooterDao, never()).update(scooter);
        verify(scooterMapper, never()).toScooterDto(scooter);
    }

    @Test
    void deleteScooter_ShouldDeleteScooter_WhenAllCorrect() {
        Long scooterId = 1L;

        when(scooterDao.delete(scooterId)).thenReturn(true);

        scooterService.deleteScooter(scooterId);

        verify(scooterDao, times(1)).delete(scooterId);
    }

    @Test
    void deleteScooter_ShouldThrowRuntimeException_WhenWasNotDelete() {
        Long scooterId = 1L;

        when(scooterDao.delete(scooterId)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> scooterService.deleteScooter(scooterId));

        verify(scooterDao, times(1)).delete(scooterId);
    }

    @Test
    void getScooter_ShouldReturnDto_WhenAllCorrect() {
        Long scooterId = 1L;

        Scooter scooter = new Scooter();
        scooter.setScooterId(scooterId);

        ScooterResponseDto expected = new ScooterResponseDto();
        expected.setScooterId(scooterId);

        when(scooterDao.findScooter(scooterId)).thenReturn(scooter);
        when(scooterMapper.toScooterDto(scooter)).thenReturn(expected);

        ScooterResponseDto actual = scooterService.getScooter(scooterId);

        assertNotNull(actual);
        assertEquals(expected.getScooterId(), actual.getScooterId());

        verify(scooterDao, times(1)).findScooter(scooterId);
    }

    @Test
    void getScooter_ShouldThrowScooterNotFoundException_WhenScooterNotFound() {
        Long scooterId = 1L;

        when(scooterDao.findScooter(scooterId)).thenReturn(null);

        assertThrows(ScooterNotFoundException.class, () -> scooterService.getScooter(scooterId));

        verify(scooterDao, times(1)).findScooter(scooterId);
    }

    @Test
    void getAllScooters_ShouldReturnListOfDto_WhenAllCorrect() {
        Long scooterId1 = 1L;
        Long scooterId2 = 2L;

        Scooter scooter1 = new Scooter();
        scooter1.setScooterId(scooterId1);

        Scooter scooter2 = new Scooter();
        scooter2.setScooterId(scooterId2);

        ScooterResponseDto scooterResponseDto1 = new ScooterResponseDto();
        scooterResponseDto1.setScooterId(scooterId1);

        ScooterResponseDto scooterResponseDto2 = new ScooterResponseDto();
        scooterResponseDto2.setScooterId(scooterId2);

        when(scooterDao.findScooters()).thenReturn(new ArrayList<>(List.of(scooter1, scooter2)));
        when(scooterMapper.toScooterDto(scooter1)).thenReturn(scooterResponseDto1);
        when(scooterMapper.toScooterDto(scooter2)).thenReturn(scooterResponseDto2);

        List<ScooterResponseDto> expected = new ArrayList<>(List.of(scooterResponseDto1, scooterResponseDto2));

        List<ScooterResponseDto> actual = scooterService.getAllScooters();

        assertNotNull(actual);
        assertEquals(expected, actual);

        verify(scooterDao, times(1)).findScooters();
        verify(scooterMapper, times(2)).toScooterDto(any());
    }
}
