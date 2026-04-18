package org.scooterrental.service.serviceimpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scooterrental.model.entity.RentalPoint;
import org.scooterrental.model.entity.Scooter;
import org.scooterrental.model.exception.RentalPointAlreadyExistsException;
import org.scooterrental.model.exception.RentalPointNotEmptyException;
import org.scooterrental.model.exception.RentalPointNotFoundException;
import org.scooterrental.model.exception.SameRentalPointsIDException;
import org.scooterrental.repository.daointerface.RentalPointDao;
import org.scooterrental.repository.daointerface.ScooterDao;
import org.scooterrental.service.dto.RentalPointCreateDto;
import org.scooterrental.service.dto.RentalPointDetailsDto;
import org.scooterrental.service.dto.RentalPointResponseDto;
import org.scooterrental.service.dto.ScooterResponseDto;
import org.scooterrental.service.mapper.RentalPointMapper;
import org.scooterrental.service.mapper.ScooterMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class RentalPointServiceTest {

    @Mock
    private RentalPointDao rentalPointDao;

    @Mock
    private RentalPointMapper rentalPointMapper;

    @Mock
    private ScooterDao scooterDao;

    @Mock
    private ScooterMapper scooterMapper;

    @InjectMocks
    private RentalPointServiceImpl rentalPointService;

    @Test
    void addNewRentalPoint_ShouldReturnDto_WhenAllCorrect() {
        String location = "Москва";

        RentalPoint rentalPoint = new RentalPoint();
        rentalPoint.setLocation(location);

        RentalPointCreateDto rentalPointCreateDto = new RentalPointCreateDto();
        rentalPointCreateDto.setLocation(location);

        RentalPointResponseDto expected = new RentalPointResponseDto();
        expected.setParentPointId(1L);
        expected.setLocation(location);

        when(rentalPointDao.findRentalPointByLocation(location)).thenReturn(null);
        when(rentalPointMapper.toRentalPointDto(rentalPoint)).thenReturn(expected);
        when(rentalPointMapper.toRentalPointEntity(rentalPointCreateDto, null)).thenReturn(rentalPoint);

        RentalPointResponseDto actual = rentalPointService.addNewRentalPoint(rentalPointCreateDto);
        assertEquals(expected.getLocation(), actual.getLocation());
        assertEquals(expected.getRentalPointId(), actual.getRentalPointId());

        verify(rentalPointDao, times(1)).findRentalPointByLocation(location);
        verify(rentalPointDao, times(1)).create(rentalPoint);
        verify(rentalPointMapper, times(1)).toRentalPointEntity(rentalPointCreateDto, null);
        verify(rentalPointMapper, times(1)).toRentalPointDto(rentalPoint);
    }

    @Test
    void addNewRentalPoint_ShouldThrowRentalPointAlreadyExistsException_WhenRentalPointAlreadyExists() {
        String location = "Москва";

        RentalPoint rentalPoint = new RentalPoint();
        rentalPoint.setLocation(location);

        RentalPointCreateDto rentalPointCreateDto = new RentalPointCreateDto();
        rentalPointCreateDto.setLocation(location);

        when(rentalPointDao.findRentalPointByLocation(location)).thenReturn(rentalPoint);

        assertThrows(RentalPointAlreadyExistsException.class, () -> rentalPointService.addNewRentalPoint(rentalPointCreateDto));

        verify(rentalPointDao, times(1)).findRentalPointByLocation(location);
        verify(rentalPointMapper, never()).toRentalPointEntity(any(), any());
        verify(rentalPointMapper, never()).toRentalPointDto(any(RentalPoint.class));
    }

    @Test
    void addNewRentalPoint_ShouldThrowRentalPointNotFoundException_WhenParentPointNotFound() {
        String location = "Москва";

        RentalPointCreateDto rentalPointCreateDto = new RentalPointCreateDto();
        rentalPointCreateDto.setLocation(location);
        rentalPointCreateDto.setParentPointId(111L);

        when(rentalPointDao.findRentalPointByLocation(location)).thenReturn(null);
        when(rentalPointDao.findRentalPointById(111L)).thenReturn(null);

        assertThrows(RentalPointNotFoundException.class, () -> rentalPointService.addNewRentalPoint(rentalPointCreateDto));

        verify(rentalPointDao, times(1)).findRentalPointByLocation(location);
        verify(rentalPointMapper, never()).toRentalPointEntity(any(), any());
        verify(rentalPointMapper, never()).toRentalPointDto(any(RentalPoint.class));
    }

    @Test
    void setNewRentalPointLocation_ShouldReturnDto_WhenAllCorrect() {
        Long rentalPointId = 1L;
        String currLocation = "Москва";
        String newLocation = "Химки";

        RentalPoint rentalPoint = new RentalPoint();
        rentalPoint.setLocation(currLocation);
        rentalPoint.setRentalPointId(rentalPointId);

        RentalPointResponseDto expected = new RentalPointResponseDto();
        expected.setRentalPointId(rentalPointId);
        expected.setLocation(newLocation);

        when(rentalPointDao.findRentalPointByLocation(newLocation)).thenReturn(null);
        when(rentalPointDao.findRentalPointById(rentalPointId)).thenReturn(rentalPoint);
        when(rentalPointMapper.toRentalPointDto(rentalPoint)).thenReturn(expected);

        RentalPointResponseDto actual = rentalPointService.setNewRentalPointLocation(rentalPointId, newLocation);

        assertNotNull(actual);
        assertEquals(expected.getRentalPointId(), actual.getRentalPointId());
        assertEquals(expected.getLocation(), actual.getLocation());

        verify(rentalPointDao, times(1)).findRentalPointById(rentalPointId);
        verify(rentalPointDao, times(1)).update(rentalPoint);
        verify(rentalPointDao, times(1)).findRentalPointByLocation(newLocation);
        verify(rentalPointMapper, times(1)).toRentalPointDto(rentalPoint);
    }

    @Test
    void setNewRentalPointLocation_ShouldThrowRentalPointNotFoundException_WhenRentalPointNotFound() {
        Long rentalPointId = 1L;
        String newLocation = "Химки";

        when(rentalPointDao.findRentalPointByLocation(newLocation)).thenReturn(null);
        when(rentalPointDao.findRentalPointById(rentalPointId)).thenReturn(null);

        assertThrows(RentalPointNotFoundException.class, () -> rentalPointService.setNewRentalPointLocation(rentalPointId, newLocation));

        verify(rentalPointDao, times(1)).findRentalPointById(rentalPointId);
        verify(rentalPointDao, never()).update(any());
        verify(rentalPointDao, times(1)).findRentalPointByLocation(newLocation);
        verify(rentalPointMapper, never()).toRentalPointDto(any());
    }

    @Test
    void setNewRentalPointLocation_ShouldThrowRentalPointAlreadyExists_WhenThereRentalPointWithSameLocation() {
        Long rentalPointId = 1L;
        String newLocation = "Химки";

        RentalPoint rentalPoint = new RentalPoint();
        rentalPoint.setLocation(newLocation);
        rentalPoint.setRentalPointId(rentalPointId);

        when(rentalPointDao.findRentalPointByLocation(newLocation)).thenReturn(rentalPoint);

        assertThrows(RentalPointAlreadyExistsException.class, () -> rentalPointService.setNewRentalPointLocation(2L, newLocation));

        verify(rentalPointDao, times(1)).findRentalPointByLocation(newLocation);
        verify(rentalPointDao, never()).findRentalPointById(rentalPointId);
        verify(rentalPointDao, never()).update(any());
        verify(rentalPointMapper, never()).toRentalPointDto(any());
    }

    @Test
    void setNewParentPointId_ShouldReturnDto_WhenAllCorrect() {
        Long rentalPointId = 1L;
        Long newParentPointId = 2L;
        String location = "Москва";

        RentalPoint parentPoint = new RentalPoint();
        parentPoint.setLocation(location);
        parentPoint.setRentalPointId(newParentPointId);

        RentalPoint rentalPoint = new RentalPoint();
        rentalPoint.setLocation(location);
        rentalPoint.setRentalPointId(rentalPointId);

        RentalPointResponseDto expected = new RentalPointResponseDto();
        expected.setRentalPointId(rentalPointId);
        expected.setLocation(location);
        expected.setParentPointId(newParentPointId);

        when(rentalPointDao.findRentalPointById(rentalPointId)).thenReturn(rentalPoint);
        when(rentalPointDao.findRentalPointById(newParentPointId)).thenReturn(parentPoint);
        when(rentalPointMapper.toRentalPointDto(rentalPoint)).thenReturn(expected);

        RentalPointResponseDto actual = rentalPointService.setNewParentPointId(rentalPointId, newParentPointId);

        assertNotNull(actual);
        assertEquals(expected.getParentPointId(), actual.getParentPointId());

        verify(rentalPointDao, times(2)).findRentalPointById(anyLong());
        verify(rentalPointDao, times(1)).update(rentalPoint);
        verify(rentalPointMapper, times(1)).toRentalPointDto(rentalPoint);
    }

    @Test
    void setNewParentPointId_ShouldThrowRentalPointNotFoundException_WhenParentPointNotFound() {
        Long rentalPointId = 1L;
        Long newParentPointId = 2L;
        String location = "Москва";

        RentalPoint parentPoint = new RentalPoint();
        parentPoint.setLocation(location);
        parentPoint.setRentalPointId(newParentPointId);

        RentalPoint rentalPoint = new RentalPoint();
        rentalPoint.setLocation(location);
        rentalPoint.setRentalPointId(rentalPointId);

        when(rentalPointDao.findRentalPointById(newParentPointId)).thenReturn(parentPoint);
        when(rentalPointDao.findRentalPointById(rentalPointId)).thenReturn(null);

        assertThrows(RentalPointNotFoundException.class, () -> rentalPointService.setNewParentPointId(rentalPointId, newParentPointId));

        verify(rentalPointDao, times(2)).findRentalPointById(anyLong());
        verify(rentalPointDao, never()).update(rentalPoint);
        verify(rentalPointMapper, never()).toRentalPointDto(rentalPoint);
    }

    @Test
    void setNewParentPointId_ShouldThrowSameRentalPointsIDException_WhenGotSameRentalPoints() {
        Long rentalPointId = 1L;
        String location = "Москва";

        RentalPoint rentalPoint = new RentalPoint();
        rentalPoint.setLocation(location);
        rentalPoint.setRentalPointId(rentalPointId);

        when(rentalPointDao.findRentalPointById(rentalPointId)).thenReturn(rentalPoint);

        assertThrows(SameRentalPointsIDException.class, () -> rentalPointService.setNewParentPointId(rentalPointId, rentalPointId));

        verify(rentalPointDao, times(2)).findRentalPointById(anyLong());
        verify(rentalPointDao, never()).update(rentalPoint);
        verify(rentalPointMapper, never()).toRentalPointDto(rentalPoint);
    }

    @Test
    void setNewParentPointId_ShouldThrowRentalPointNotFoundException_WhenRentalPointNotFound() {
        Long rentalPointId = 1L;
        Long newParentPointId = 2L;
        String location = "Москва";

        RentalPoint rentalPoint = new RentalPoint();
        rentalPoint.setLocation(location);
        rentalPoint.setRentalPointId(rentalPointId);

        when(rentalPointDao.findRentalPointById(newParentPointId)).thenReturn(null);

        assertThrows(RentalPointNotFoundException.class, () -> rentalPointService.setNewParentPointId(rentalPointId, newParentPointId));

        verify(rentalPointDao, times(1)).findRentalPointById(anyLong());
        verify(rentalPointDao, never()).update(rentalPoint);
        verify(rentalPointMapper, never()).toRentalPointDto(rentalPoint);
    }

    @Test
    void deleteRentalPoint_ShouldDeleteRentalPoint_WhenAllCorrect() {
        Long rentalPointId = 1L;

        RentalPoint rentalPoint = new RentalPoint();
        rentalPoint.setRentalPointId(rentalPointId);

        when(rentalPointDao.findRentalPointById(rentalPointId)).thenReturn(rentalPoint);
        when(scooterDao.countScootersAtRentalPoint(rentalPointId)).thenReturn(0L);
        when(rentalPointDao.delete(rentalPointId)).thenReturn(true);

        rentalPointService.deleteRentalPoint(rentalPointId);

        verify(rentalPointDao, times(1)).findRentalPointById(rentalPointId);
        verify(rentalPointDao, times(1)).delete(rentalPointId);
        verify(scooterDao, times(1)).countScootersAtRentalPoint(rentalPointId);
    }

    @Test
    void deleteRentalPoint_ShouldThrowRentalPointNotFoundException_WhenRentalPointNotFound() {
        Long rentalPointId = 1L;

        when(rentalPointDao.findRentalPointById(rentalPointId)).thenReturn(null);

        assertThrows(RentalPointNotFoundException.class, () -> rentalPointService.deleteRentalPoint(rentalPointId));

        verify(rentalPointDao, times(1)).findRentalPointById(rentalPointId);
        verify(rentalPointDao, never()).delete(rentalPointId);
        verify(scooterDao, never()).countScootersAtRentalPoint(rentalPointId);
    }

    @Test
    void deleteRentalPoint_ShouldThrowRentalPointNotEmptyException_WhenRentalPointNotEmpty() {
        Long rentalPointId = 1L;

        RentalPoint rentalPoint = new RentalPoint();
        rentalPoint.setRentalPointId(rentalPointId);

        when(rentalPointDao.findRentalPointById(rentalPointId)).thenReturn(rentalPoint);
        when(scooterDao.countScootersAtRentalPoint(rentalPointId)).thenReturn(1L);

        assertThrows(RentalPointNotEmptyException.class, () -> rentalPointService.deleteRentalPoint(rentalPointId));

        verify(rentalPointDao, times(1)).findRentalPointById(rentalPointId);
        verify(rentalPointDao, never()).delete(rentalPointId);
        verify(scooterDao, times(1)).countScootersAtRentalPoint(rentalPointId);
    }

    @Test
    void deleteRentalPoint_ShouldThrowRuntimeException_WhenDeleteNotCorrect() {
        Long rentalPointId = 1L;

        RentalPoint rentalPoint = new RentalPoint();
        rentalPoint.setRentalPointId(rentalPointId);

        when(rentalPointDao.findRentalPointById(rentalPointId)).thenReturn(rentalPoint);
        when(scooterDao.countScootersAtRentalPoint(rentalPointId)).thenReturn(0L);
        when(rentalPointDao.delete(rentalPointId)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> rentalPointService.deleteRentalPoint(rentalPointId));

        verify(rentalPointDao, times(1)).findRentalPointById(rentalPointId);
        verify(rentalPointDao, times(1)).delete(rentalPointId);
        verify(scooterDao, times(1)).countScootersAtRentalPoint(rentalPointId);
    }

    @Test
    void getRentalPoint_ShouldReturnDto_WhenAllCorrect() {
        Long rentalPointId = 1L;
        RentalPoint rentalPoint = new RentalPoint();
        rentalPoint.setRentalPointId(rentalPointId);
        rentalPoint.setLocation("Москва");

        RentalPointResponseDto expected = new RentalPointResponseDto();
        expected.setRentalPointId(rentalPointId);
        expected.setLocation("Москва");

        when(rentalPointDao.findRentalPointById(rentalPointId)).thenReturn(rentalPoint);
        when(rentalPointMapper.toRentalPointDto(rentalPoint)).thenReturn(expected);

        RentalPointResponseDto actual = rentalPointService.getRentalPoint(rentalPointId);

        assertNotNull(actual);
        assertEquals(rentalPointId, actual.getRentalPointId());
        assertEquals("Москва", actual.getLocation());

        verify(rentalPointDao, times(1)).findRentalPointById(rentalPointId);
        verify(rentalPointMapper, times(1)).toRentalPointDto(rentalPoint);
    }

    @Test
    void getRentalPoint_ShouldThrowRentalPointNotFoundException_WhenRentalPointNotFound() {
        Long rentalPointId = 1L;

        when(rentalPointDao.findRentalPointById(rentalPointId)).thenReturn(null);

        assertThrows(RentalPointNotFoundException.class, () -> rentalPointService.getRentalPoint(rentalPointId));

        verify(rentalPointDao, times(1)).findRentalPointById(rentalPointId);
        verify(rentalPointMapper, never()).toRentalPointDto(any());
    }

    @Test
    void getAllRentalPoint_ShouldReturnListOfDto_WhenAllCorrect() {
        Long rentalPointId1 = 1L;
        Long rentalPointId2 = 2L;

        RentalPoint rentalPoint1 = new RentalPoint();
        rentalPoint1.setRentalPointId(rentalPointId1);

        RentalPointResponseDto rentalPointResponseDto1 = new RentalPointResponseDto();
        rentalPointResponseDto1.setRentalPointId(rentalPointId1);

        RentalPoint rentalPoint2 = new RentalPoint();
        rentalPoint2.setRentalPointId(rentalPointId2);

        RentalPointResponseDto rentalPointResponseDto2 = new RentalPointResponseDto();
        rentalPointResponseDto2.setRentalPointId(rentalPointId2);

        when(rentalPointDao.findRentalPoints()).thenReturn(new ArrayList<>(List.of(rentalPoint1, rentalPoint2)));
        when(rentalPointMapper.toRentalPointDto(rentalPoint1)).thenReturn(rentalPointResponseDto1);
        when(rentalPointMapper.toRentalPointDto(rentalPoint2)).thenReturn(rentalPointResponseDto2);

        List<RentalPointResponseDto> expected = new ArrayList<>(List.of(rentalPointResponseDto1, rentalPointResponseDto2));

        List<RentalPointResponseDto> actual = rentalPointService.getAllRentalPoints();

        assertNotNull(actual);
        assertEquals(expected, actual);

        verify(rentalPointDao, times(1)).findRentalPoints();
        verify(rentalPointMapper, times(2)).toRentalPointDto(any());
    }

    @Test
    void getRentalPointDetails_ShouldReturnDto_WhenAllCorrect() {
        Long rentalPointId = 1L;

        RentalPoint rentalPoint = new RentalPoint();
        rentalPoint.setRentalPointId(rentalPointId);
        rentalPoint.setLocation("Москва");

        RentalPointDetailsDto expected = new RentalPointDetailsDto();
        expected.setRentalPointId(rentalPointId);
        expected.setLocation("Москва");

        Scooter scooter = new Scooter();
        ScooterResponseDto scooterResponseDto = new ScooterResponseDto();


        when(rentalPointDao.findRentalPointById(rentalPointId)).thenReturn(rentalPoint);
        when(scooterDao.findScootersByRentalPoint(rentalPointId)).thenReturn(new ArrayList<>(List.of(scooter)));
        when(scooterMapper.toScooterDto(scooter)).thenReturn(scooterResponseDto);

        RentalPointDetailsDto actual = rentalPointService.getRentalPointDetails(rentalPointId);

        assertNotNull(actual);
        assertEquals(expected.getRentalPointId(), actual.getRentalPointId());

        verify(rentalPointDao, times(1)).findRentalPointById(rentalPointId);
    }

    @Test
    void getRentalPointDetails_ShouldThrowRentalPointNotFoundException_WhenRentalPointNotFound() {
        Long rentalPointId = 1L;

        when(rentalPointDao.findRentalPointById(rentalPointId)).thenReturn(null);

        assertThrows(RentalPointNotFoundException.class, () -> rentalPointService.getRentalPointDetails(rentalPointId));

        verify(rentalPointDao, times(1)).findRentalPointById(rentalPointId);
    }
}
