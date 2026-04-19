package org.scooterrental.service.serviceimpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scooterrental.model.entity.RentalPoint;
import org.scooterrental.model.entity.Scooter;
import org.scooterrental.model.enums.RentalPointType;
import org.scooterrental.model.exception.InvalidHierarchyException;
import org.scooterrental.model.exception.RentalPointNotEmptyException;
import org.scooterrental.model.exception.RentalPointNotFoundException;
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
        rentalPoint.setRentalPointType(RentalPointType.CITY);

        RentalPointCreateDto rentalPointCreateDto = new RentalPointCreateDto();
        rentalPointCreateDto.setLocation(location);
        rentalPointCreateDto.setRentalPointType(RentalPointType.CITY);
        rentalPointCreateDto.setParentPointId(null);

        RentalPointResponseDto expected = new RentalPointResponseDto();
        expected.setLocation(location);

        when(rentalPointMapper.toRentalPointDto(rentalPoint)).thenReturn(expected);
        when(rentalPointMapper.toRentalPointEntity(rentalPointCreateDto, null)).thenReturn(rentalPoint);

        RentalPointResponseDto actual = rentalPointService.addNewRentalPoint(rentalPointCreateDto);
        assertEquals(expected.getLocation(), actual.getLocation());

        verify(rentalPointDao, times(1)).create(rentalPoint);
        verify(rentalPointMapper, times(1)).toRentalPointEntity(rentalPointCreateDto, null);
        verify(rentalPointMapper, times(1)).toRentalPointDto(rentalPoint);
    }

    @Test
    void addNewRentalPoint_ShouldThrowRentalPointNotFoundException_WhenParentPointNotFound() {
        String location = "ЦАО";

        RentalPointCreateDto rentalPointCreateDto = new RentalPointCreateDto();
        rentalPointCreateDto.setLocation(location);
        rentalPointCreateDto.setRentalPointType(RentalPointType.DISTRICT);
        rentalPointCreateDto.setParentPointId(111L);

        when(rentalPointDao.findRentalPointById(111L)).thenReturn(null);

        assertThrows(RentalPointNotFoundException.class, () -> rentalPointService.addNewRentalPoint(rentalPointCreateDto));

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

        when(rentalPointDao.findRentalPointById(rentalPointId)).thenReturn(rentalPoint);
        when(rentalPointMapper.toRentalPointDto(rentalPoint)).thenReturn(expected);

        RentalPointResponseDto actual = rentalPointService.setNewRentalPointLocation(rentalPointId, newLocation);

        assertNotNull(actual);
        assertEquals(expected.getRentalPointId(), actual.getRentalPointId());
        assertEquals(expected.getLocation(), actual.getLocation());

        verify(rentalPointDao, times(1)).findRentalPointById(rentalPointId);
        verify(rentalPointDao, times(1)).update(rentalPoint);
        verify(rentalPointMapper, times(1)).toRentalPointDto(rentalPoint);
    }

    @Test
    void setNewRentalPointLocation_ShouldThrowRentalPointNotFoundException_WhenRentalPointNotFound() {
        Long rentalPointId = 1L;
        String newLocation = "Химки";

        when(rentalPointDao.findRentalPointById(rentalPointId)).thenReturn(null);

        assertThrows(RentalPointNotFoundException.class, () -> rentalPointService.setNewRentalPointLocation(rentalPointId, newLocation));

        verify(rentalPointDao, times(1)).findRentalPointById(rentalPointId);
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
        parentPoint.setRentalPointType(RentalPointType.CITY);

        RentalPoint rentalPoint = new RentalPoint();
        rentalPoint.setLocation(location);
        rentalPoint.setRentalPointId(rentalPointId);
        rentalPoint.setRentalPointType(RentalPointType.DISTRICT);

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

        verify(rentalPointDao, times(3)).findRentalPointById(anyLong());
        verify(rentalPointDao, times(1)).update(rentalPoint);
        verify(rentalPointMapper, times(1)).toRentalPointDto(rentalPoint);
    }

    @Test
    void setNewParentPointId_ShouldThrowInvalidHierarchyException_WhenHierarchyIsWrong() {
        Long rentalPointId = 1L;
        Long newParentPointId = 2L;
        String location = "Москва";

        RentalPoint parentPoint = new RentalPoint();
        parentPoint.setLocation(location);
        parentPoint.setRentalPointId(newParentPointId);
        parentPoint.setRentalPointType(RentalPointType.CITY);

        RentalPoint rentalPoint = new RentalPoint();
        rentalPoint.setLocation(location);
        rentalPoint.setRentalPointId(rentalPointId);
        rentalPoint.setRentalPointType(RentalPointType.CITY);

        when(rentalPointDao.findRentalPointById(newParentPointId)).thenReturn(parentPoint);
        when(rentalPointDao.findRentalPointById(rentalPointId)).thenReturn(rentalPoint);

        assertThrows(InvalidHierarchyException.class, () -> rentalPointService.setNewParentPointId(rentalPointId, newParentPointId));

        verify(rentalPointDao, times(2)).findRentalPointById(anyLong());
        verify(rentalPointDao, never()).update(any());
        verify(rentalPointMapper, never()).toRentalPointDto(any());
    }

    @Test
    void setNewParentPointId_ShouldThrowRentalPointNotFoundException_WhenParentPointNotFound() {
        Long rentalPointId = 1L;
        Long newParentPointId = 2L;

        when(rentalPointDao.findRentalPointById(newParentPointId)).thenReturn(null);

        assertThrows(RentalPointNotFoundException.class, () -> rentalPointService.setNewParentPointId(rentalPointId, newParentPointId));

        verify(rentalPointDao, times(1)).findRentalPointById(anyLong());
        verify(rentalPointDao, never()).update(any());
        verify(rentalPointMapper, never()).toRentalPointDto(any());
    }

    @Test
    void setNewParentPointId_ShouldThrowRentalPointNotFoundException_WhenRentalPointNotFound() {
        Long rentalPointId = 1L;
        Long newParentPointId = 2L;

        RentalPoint parentPoint = new RentalPoint();
        parentPoint.setRentalPointId(newParentPointId);

        when(rentalPointDao.findRentalPointById(newParentPointId)).thenReturn(parentPoint);
        when(rentalPointDao.findRentalPointById(rentalPointId)).thenReturn(null);

        assertThrows(RentalPointNotFoundException.class, () -> rentalPointService.setNewParentPointId(rentalPointId, newParentPointId));

        verify(rentalPointDao, times(2)).findRentalPointById(anyLong());
        verify(rentalPointDao, never()).update(any());
        verify(rentalPointMapper, never()).toRentalPointDto(any());
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
        rentalPoint.setRentalPointType(RentalPointType.CITY);
        rentalPoint.setChildPoints(new ArrayList<>());

        RentalPointDetailsDto expected = new RentalPointDetailsDto();
        expected.setRentalPointId(rentalPointId);
        expected.setLocation("Москва");
        expected.setRentalPointType(RentalPointType.CITY.name());
        expected.setChildPoints(new ArrayList<>());

        Scooter scooter = new Scooter();
        ScooterResponseDto scooterResponseDto = new ScooterResponseDto();

        when(rentalPointDao.findRentalPointById(rentalPointId)).thenReturn(rentalPoint);
        when(scooterDao.findScootersByRentalPoint(rentalPointId)).thenReturn(new ArrayList<>(List.of(scooter)));
        when(scooterMapper.toScooterDto(scooter)).thenReturn(scooterResponseDto);

        RentalPointDetailsDto actual = rentalPointService.getRentalPointDetails(rentalPointId);

        assertNotNull(actual);
        assertEquals(expected.getRentalPointId(), actual.getRentalPointId());
        assertEquals(expected.getRentalPointType(), actual.getRentalPointType());

        verify(rentalPointDao, times(1)).findRentalPointById(rentalPointId);
    }

    @Test
    void getRentalPointDetails_ShouldThrowRentalPointNotFoundException_WhenRentalPointNotFound() {
        Long rentalPointId = 1L;

        when(rentalPointDao.findRentalPointById(rentalPointId)).thenReturn(null);

        assertThrows(RentalPointNotFoundException.class, () -> rentalPointService.getRentalPointDetails(rentalPointId));

        verify(rentalPointDao, times(1)).findRentalPointById(rentalPointId);
    }

    @Test
    void getBuildingsByRentalPointId_ShouldReturnListOfDto_WhenStartPointIsBuilding() {
        Long rentalPointId = 1L;

        RentalPoint parentStreet = new RentalPoint();
        parentStreet.setLocation("ул. Ленина");

        RentalPoint building = new RentalPoint();
        building.setRentalPointId(rentalPointId);
        building.setRentalPointType(RentalPointType.BUILDING);
        building.setLocation("5");
        building.setParentPoint(parentStreet);

        RentalPointResponseDto mappedDto = new RentalPointResponseDto();
        mappedDto.setRentalPointId(rentalPointId);
        mappedDto.setLocation("5");

        when(rentalPointDao.findRentalPointById(rentalPointId)).thenReturn(building);
        when(rentalPointMapper.toRentalPointDto(building)).thenReturn(mappedDto);

        List<RentalPointResponseDto> actual = rentalPointService.getRentalStationsByParentId(rentalPointId);

        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertEquals("ул. Ленина, 5", actual.get(0).getLocation());

        verify(rentalPointDao, times(1)).findRentalPointById(rentalPointId);
        verify(rentalPointMapper, times(1)).toRentalPointDto(building);
    }

    @Test
    void getBuildingsByRentalPointId_ShouldReturnListOfDto_WhenStartPointHasComplexHierarchy() {
        Long startPointId = 1L;

        RentalPoint district = new RentalPoint();
        district.setRentalPointId(startPointId);
        district.setRentalPointType(RentalPointType.DISTRICT);
        district.setLocation("Центральный");
        district.setChildPoints(new ArrayList<>());

        RentalPoint street = new RentalPoint();
        street.setRentalPointType(RentalPointType.STREET);
        street.setLocation("ул. Пушкина");
        street.setParentPoint(district);
        street.setChildPoints(new ArrayList<>());

        RentalPoint building = new RentalPoint();
        building.setRentalPointId(2L);
        building.setRentalPointType(RentalPointType.BUILDING);
        building.setLocation("10");
        building.setParentPoint(street);

        street.getChildPoints().add(building);
        district.getChildPoints().add(street);

        RentalPointResponseDto mappedDto = new RentalPointResponseDto();
        mappedDto.setRentalPointId(2L);
        mappedDto.setLocation("10");

        when(rentalPointDao.findRentalPointById(startPointId)).thenReturn(district);
        when(rentalPointMapper.toRentalPointDto(building)).thenReturn(mappedDto);

        List<RentalPointResponseDto> actual = rentalPointService.getRentalStationsByParentId(startPointId);

        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertEquals("ул. Пушкина, 10", actual.get(0).getLocation());

        verify(rentalPointDao, times(1)).findRentalPointById(startPointId);
        verify(rentalPointMapper, times(1)).toRentalPointDto(building);
    }

    @Test
    void getBuildingsByRentalPointId_ShouldReturnDtoWithoutDeletedPoints_WhenThereDeletedPoints() {
        Long startPointId = 1L;

        RentalPoint street = new RentalPoint();
        street.setRentalPointId(startPointId);
        street.setRentalPointType(RentalPointType.STREET);
        street.setLocation("ул. Чехова");
        street.setChildPoints(new ArrayList<>());

        RentalPoint activeBuilding = new RentalPoint();
        activeBuilding.setRentalPointType(RentalPointType.BUILDING);
        activeBuilding.setLocation("1");
        activeBuilding.setParentPoint(street);

        RentalPoint deletedBuilding = new RentalPoint();
        deletedBuilding.setRentalPointType(RentalPointType.BUILDING);
        deletedBuilding.setLocation("2");
        deletedBuilding.setParentPoint(street);
        deletedBuilding.setDeleted(true);

        street.getChildPoints().add(activeBuilding);
        street.getChildPoints().add(deletedBuilding);

        RentalPointResponseDto mappedDto = new RentalPointResponseDto();
        mappedDto.setLocation("1");

        when(rentalPointDao.findRentalPointById(startPointId)).thenReturn(street);
        when(rentalPointMapper.toRentalPointDto(activeBuilding)).thenReturn(mappedDto);

        List<RentalPointResponseDto> actual = rentalPointService.getRentalStationsByParentId(startPointId);

        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertEquals("ул. Чехова, 1", actual.get(0).getLocation());

        verify(rentalPointDao, times(1)).findRentalPointById(startPointId);
        verify(rentalPointMapper, times(1)).toRentalPointDto(activeBuilding);
        verify(rentalPointMapper, never()).toRentalPointDto(deletedBuilding);
    }

    @Test
    void getBuildingsByRentalPointId_ShouldReturnEmptyList_WhenNoBuildingsFound() {
        Long startPointId = 1L;

        RentalPoint street = new RentalPoint();
        street.setRentalPointId(startPointId);
        street.setRentalPointType(RentalPointType.STREET);
        street.setLocation("ул. Ленина");
        street.setChildPoints(new ArrayList<>());

        when(rentalPointDao.findRentalPointById(startPointId)).thenReturn(street);

        List<RentalPointResponseDto> actual = rentalPointService.getRentalStationsByParentId(startPointId);

        assertNotNull(actual);
        assertEquals(0, actual.size());

        verify(rentalPointDao, times(1)).findRentalPointById(startPointId);
        verify(rentalPointMapper, never()).toRentalPointDto(any());
    }

    @Test
    void getBuildingsByRentalPointId_ShouldThrowRentalPointNotFoundException_WhenStartPointNotFound() {
        Long startPointId = 1L;

        when(rentalPointDao.findRentalPointById(startPointId)).thenReturn(null);

        assertThrows(RentalPointNotFoundException.class, () -> rentalPointService.getRentalStationsByParentId(startPointId));

        verify(rentalPointDao, times(1)).findRentalPointById(startPointId);
        verify(rentalPointMapper, never()).toRentalPointDto(any());
    }
}