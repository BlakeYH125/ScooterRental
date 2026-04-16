package org.scooterrental.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.scooterrental.controller.advice.GlobalExceptionHandler;
import org.scooterrental.model.enums.ScooterStatus;
import org.scooterrental.model.exception.*;
import org.scooterrental.service.dto.ScooterCreateDto;
import org.scooterrental.service.dto.ScooterResponseDto;
import org.scooterrental.service.serviceinterface.ScooterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ScooterController.class)
@Import(GlobalExceptionHandler.class)
public class ScooterControllerTest {

    @SpringBootApplication
    @EnableMethodSecurity
    static class TestConfig {
    }

    @MockBean
    private ScooterService scooterService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    void addNewScooter_ShouldReturn200AndDto_WhenAdminRequestsAndAllCorrect() throws Exception {
        ScooterResponseDto expected = new ScooterResponseDto();
        expected.setScooterId(1L);
        expected.setModel("Xiaomi");

        ScooterCreateDto scooterCreateDto = new ScooterCreateDto();
        scooterCreateDto.setModel("Xiaomi");

        when(scooterService.addNewScooter(any(ScooterCreateDto.class))).thenReturn(expected);

        mockMvc.perform(post("/scooter-rental/scooters/add")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scooterCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scooterId").value(1L))
                .andExpect(jsonPath("$.model").value("Xiaomi"));

        verify(scooterService, times(1)).addNewScooter(any(ScooterCreateDto.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void addNewScooter_ShouldReturn403_WhenUserRequests() throws Exception {
        ScooterCreateDto scooterCreateDto = new ScooterCreateDto();
        scooterCreateDto.setModel("Xiaomi");

        mockMvc.perform(post("/scooter-rental/scooters/add")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scooterCreateDto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));

        verify(scooterService, never()).addNewScooter(any(ScooterCreateDto.class));
    }

    @Test
    void addNewScooter_ShouldReturn401_WhenNonameRequests() throws Exception {
        ScooterCreateDto scooterCreateDto = new ScooterCreateDto();
        scooterCreateDto.setModel("Xiaomi");

        mockMvc.perform(post("/scooter-rental/scooters/add")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scooterCreateDto)))
                .andExpect(status().isUnauthorized());

        verify(scooterService, never()).addNewScooter(any(ScooterCreateDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setNewScooterModel_ShouldReturn200AndDto_WhenAdminRequestsAndAllCorrect() throws Exception {
        Long scooterId = 1L;
        String newModel = "Xiaomi";

        ScooterResponseDto expected = new ScooterResponseDto();
        expected.setScooterId(scooterId);
        expected.setModel(newModel);

        when(scooterService.setNewScooterModel(scooterId, newModel)).thenReturn(expected);

        mockMvc.perform(patch("/scooter-rental/scooters/{scooterId}/set-new-model", scooterId)
                        .param("newScooterModel", newModel)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scooterId").value(scooterId))
                .andExpect(jsonPath("$.model").value(newModel));

        verify(scooterService, times(1)).setNewScooterModel(scooterId, newModel);
    }

    @Test
    @WithMockUser(roles = "USER")
    void setNewScooterModel_ShouldReturn403_WhenUserRequests() throws Exception {
        Long scooterId = 1L;
        String newModel = "Xiaomi";

        mockMvc.perform(patch("/scooter-rental/scooters/{scooterId}/set-new-model", scooterId)
                        .param("newScooterModel", newModel)
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));

        verify(scooterService, never()).setNewScooterModel(anyLong(), anyString());
    }

    @Test
    void setNewScooterModel_ShouldReturn401_WhenNonameRequests() throws Exception {
        Long scooterId = 1L;
        String newModel = "Xiaomi";

        mockMvc.perform(patch("/scooter-rental/scooters/{scooterId}/set-new-model", scooterId)
                        .param("newScooterModel", newModel)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(scooterService, never()).setNewScooterModel(anyLong(), anyString());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setNewScooterModel_ShouldReturn404_WhenScooterNotFound() throws Exception {
        Long scooterId = 1L;
        String newModel = "Xiaomi";

        when(scooterService.setNewScooterModel(scooterId, newModel)).thenThrow(new ScooterNotFoundException());

        mockMvc.perform(patch("/scooter-rental/scooters/{scooterId}/set-new-model", scooterId)
                        .param("newScooterModel", newModel)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("ScooterNotFoundException"));

        verify(scooterService, times(1)).setNewScooterModel(scooterId, newModel);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setNewScooterModel_ShouldReturn400_WhenMissingRequestParam() throws Exception {
        Long scooterId = 1L;

        mockMvc.perform(patch("/scooter-rental/scooters/{scooterId}/set-new-model", scooterId)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("MissingServletRequestParameterException"));

        verify(scooterService, never()).setNewScooterModel(anyLong(), anyString());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setNewBatteryLevel_ShouldReturn200AndDto_WhenAdminRequestsAndAllCorrect() throws Exception {
        Long scooterId = 1L;
        int newBatteryLevel = 80;

        ScooterResponseDto expected = new ScooterResponseDto();
        expected.setScooterId(scooterId);
        expected.setBatteryLevel(newBatteryLevel);

        when(scooterService.setNewBatteryLevel(scooterId, newBatteryLevel)).thenReturn(expected);

        mockMvc.perform(patch("/scooter-rental/scooters/{scooterId}/set-new-battery-level", scooterId)
                        .param("newBatteryLevel", String.valueOf(newBatteryLevel))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scooterId").value(scooterId))
                .andExpect(jsonPath("$.batteryLevel").value(newBatteryLevel));

        verify(scooterService, times(1)).setNewBatteryLevel(scooterId, newBatteryLevel);
    }

    @Test
    @WithMockUser(roles = "USER")
    void setNewBatteryLevel_ShouldReturn403_WhenUserRequests() throws Exception {
        Long scooterId = 1L;
        int newBatteryLevel = 80;

        mockMvc.perform(patch("/scooter-rental/scooters/{scooterId}/set-new-battery-level", scooterId)
                        .param("newBatteryLevel", String.valueOf(newBatteryLevel))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));

        verify(scooterService, never()).setNewBatteryLevel(anyLong(), anyInt());
    }

    @Test
    void setNewBatteryLevel_ShouldReturn401_WhenNonameRequests() throws Exception {
        Long scooterId = 1L;
        int newBatteryLevel = 80;

        mockMvc.perform(patch("/scooter-rental/scooters/{scooterId}/set-new-battery-level", scooterId)
                        .param("newBatteryLevel", String.valueOf(newBatteryLevel))
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(scooterService, never()).setNewBatteryLevel(anyLong(), anyInt());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setNewBatteryLevel_ShouldReturn404_WhenScooterNotFound() throws Exception {
        Long scooterId = 1L;
        int newBatteryLevel = 80;

        when(scooterService.setNewBatteryLevel(scooterId, newBatteryLevel)).thenThrow(new ScooterNotFoundException());

        mockMvc.perform(patch("/scooter-rental/scooters/{scooterId}/set-new-battery-level", scooterId)
                        .param("newBatteryLevel", String.valueOf(newBatteryLevel))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("ScooterNotFoundException"));

        verify(scooterService, times(1)).setNewBatteryLevel(scooterId, newBatteryLevel);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setNewBatteryLevel_ShouldReturn400_WhenBatteryLevelIsInvalid() throws Exception {
        Long scooterId = 1L;
        int newBatteryLevel = 150;

        when(scooterService.setNewBatteryLevel(scooterId, newBatteryLevel)).thenThrow(new IllegalArgumentException());

        mockMvc.perform(patch("/scooter-rental/scooters/{scooterId}/set-new-battery-level", scooterId)
                        .param("newBatteryLevel", String.valueOf(newBatteryLevel))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("IllegalArgumentException"));

        verify(scooterService, times(1)).setNewBatteryLevel(scooterId, newBatteryLevel);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setNewBatteryLevel_ShouldReturn400_WhenMissingRequestParam() throws Exception {
        Long scooterId = 1L;

        mockMvc.perform(patch("/scooter-rental/scooters/{scooterId}/set-new-battery-level", scooterId)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("MissingServletRequestParameterException"));

        verify(scooterService, never()).setNewBatteryLevel(anyLong(), anyInt());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void rechargeBattery_ShouldReturn200AndDto_WhenAdminRequestsAndAllCorrect() throws Exception {
        Long scooterId = 1L;

        ScooterResponseDto expected = new ScooterResponseDto();
        expected.setScooterId(scooterId);
        expected.setBatteryLevel(100);

        when(scooterService.rechargeBattery(scooterId)).thenReturn(expected);

        mockMvc.perform(patch("/scooter-rental/scooters/{scooterId}/recharge-battery", scooterId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scooterId").value(scooterId))
                .andExpect(jsonPath("$.batteryLevel").value(100));

        verify(scooterService, times(1)).rechargeBattery(scooterId);
    }

    @Test
    @WithMockUser(roles = "USER")
    void rechargeBattery_ShouldReturn403_WhenUserRequests() throws Exception {
        Long scooterId = 1L;

        mockMvc.perform(patch("/scooter-rental/scooters/{scooterId}/recharge-battery", scooterId)
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));

        verify(scooterService, never()).rechargeBattery(anyLong());
    }

    @Test
    void rechargeBattery_ShouldReturn401_WhenNonameRequests() throws Exception {
        Long scooterId = 1L;

        mockMvc.perform(patch("/scooter-rental/scooters/{scooterId}/recharge-battery", scooterId)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(scooterService, never()).rechargeBattery(anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void rechargeBattery_ShouldReturn404_WhenScooterNotFound() throws Exception {
        Long scooterId = 1L;

        when(scooterService.rechargeBattery(scooterId)).thenThrow(new ScooterNotFoundException());

        mockMvc.perform(patch("/scooter-rental/scooters/{scooterId}/recharge-battery", scooterId)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("ScooterNotFoundException"));

        verify(scooterService, times(1)).rechargeBattery(scooterId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void putScooterInUse_ShouldReturn200AndDto_WhenAdminRequestsAndAllCorrect() throws Exception {
        Long scooterId = 1L;
        Long rentalPointId = 2L;

        ScooterResponseDto expected = new ScooterResponseDto();
        expected.setScooterId(scooterId);
        expected.setRentalPointId(rentalPointId);

        when(scooterService.putScooterInUse(scooterId, rentalPointId)).thenReturn(expected);

        mockMvc.perform(patch("/scooter-rental/scooters/{scooterId}/put-in-use", scooterId)
                        .param("rentalPointId", rentalPointId.toString())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scooterId").value(scooterId))
                .andExpect(jsonPath("$.rentalPointId").value(rentalPointId));

        verify(scooterService, times(1)).putScooterInUse(scooterId, rentalPointId);
    }

    @Test
    @WithMockUser(roles = "USER")
    void putScooterInUse_ShouldReturn403_WhenUserRequests() throws Exception {
        Long scooterId = 1L;
        Long rentalPointId = 2L;

        mockMvc.perform(patch("/scooter-rental/scooters/{scooterId}/put-in-use", scooterId)
                        .param("rentalPointId", rentalPointId.toString())
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));

        verify(scooterService, never()).putScooterInUse(anyLong(), anyLong());
    }

    @Test
    void putScooterInUse_ShouldReturn401_WhenNonameRequests() throws Exception {
        Long scooterId = 1L;
        Long rentalPointId = 2L;

        mockMvc.perform(patch("/scooter-rental/scooters/{scooterId}/put-in-use", scooterId)
                        .param("rentalPointId", rentalPointId.toString())
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(scooterService, never()).putScooterInUse(anyLong(), anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void putScooterInUse_ShouldReturn404_WhenScooterNotFound() throws Exception {
        Long scooterId = 1L;
        Long rentalPointId = 2L;

        when(scooterService.putScooterInUse(scooterId, rentalPointId)).thenThrow(new ScooterNotFoundException());

        mockMvc.perform(patch("/scooter-rental/scooters/{scooterId}/put-in-use", scooterId)
                        .param("rentalPointId", rentalPointId.toString())
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("ScooterNotFoundException"));

        verify(scooterService, times(1)).putScooterInUse(scooterId, rentalPointId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void putScooterInUse_ShouldReturn404_WhenRentalPointNotFound() throws Exception {
        Long scooterId = 1L;
        Long rentalPointId = 2L;

        when(scooterService.putScooterInUse(scooterId, rentalPointId)).thenThrow(new RentalPointNotFoundException());

        mockMvc.perform(patch("/scooter-rental/scooters/{scooterId}/put-in-use", scooterId)
                        .param("rentalPointId", rentalPointId.toString())
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("RentalPointNotFoundException"));

        verify(scooterService, times(1)).putScooterInUse(scooterId, rentalPointId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void putScooterInUse_ShouldReturn400_WhenLowBatteryLevel() throws Exception {
        Long scooterId = 1L;
        Long rentalPointId = 2L;

        when(scooterService.putScooterInUse(scooterId, rentalPointId)).thenThrow(new LowBatteryLevelException());

        mockMvc.perform(patch("/scooter-rental/scooters/{scooterId}/put-in-use", scooterId)
                        .param("rentalPointId", rentalPointId.toString())
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("LowBatteryLevelException"));

        verify(scooterService, times(1)).putScooterInUse(scooterId, rentalPointId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void putScooterInUse_ShouldReturn409_WhenScooterAlreadyInRent() throws Exception {
        Long scooterId = 1L;
        Long rentalPointId = 2L;

        when(scooterService.putScooterInUse(scooterId, rentalPointId)).thenThrow(new ScooterAlreadyInRentException());

        mockMvc.perform(patch("/scooter-rental/scooters/{scooterId}/put-in-use", scooterId)
                        .param("rentalPointId", rentalPointId.toString())
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("ScooterAlreadyInRentException"));

        verify(scooterService, times(1)).putScooterInUse(scooterId, rentalPointId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void putScooterInUse_ShouldReturn400_WhenMissingRequestParam() throws Exception {
        Long scooterId = 1L;

        mockMvc.perform(patch("/scooter-rental/scooters/{scooterId}/put-in-use", scooterId)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("MissingServletRequestParameterException"));

        verify(scooterService, never()).putScooterInUse(anyLong(), anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void putScooterInWarehouse_ShouldReturn200AndDto_WhenAdminRequestsAndAllCorrect() throws Exception {
        Long scooterId = 1L;

        ScooterResponseDto expected = new ScooterResponseDto();
        expected.setScooterId(scooterId);
        expected.setScooterStatus(ScooterStatus.IN_WAREHOUSE.toString());

        when(scooterService.putScooterInWarehouse(scooterId)).thenReturn(expected);

        mockMvc.perform(patch("/scooter-rental/scooters/{scooterId}/put-in-warehouse", scooterId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scooterId").value(scooterId))
                .andExpect(jsonPath("$.scooterStatus").value(ScooterStatus.IN_WAREHOUSE.toString()));

        verify(scooterService, times(1)).putScooterInWarehouse(scooterId);
    }

    @Test
    @WithMockUser(roles = "USER")
    void putScooterInWarehouse_ShouldReturn403_WhenUserRequests() throws Exception {
        Long scooterId = 1L;

        mockMvc.perform(patch("/scooter-rental/scooters/{scooterId}/put-in-warehouse", scooterId)
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));

        verify(scooterService, never()).putScooterInWarehouse(anyLong());
    }

    @Test
    void putScooterInWarehouse_ShouldReturn401_WhenNonameRequests() throws Exception {
        Long scooterId = 1L;

        mockMvc.perform(patch("/scooter-rental/scooters/{scooterId}/put-in-warehouse", scooterId)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(scooterService, never()).putScooterInWarehouse(anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void putScooterInWarehouse_ShouldReturn404_WhenScooterNotFound() throws Exception {
        Long scooterId = 1L;

        when(scooterService.putScooterInWarehouse(scooterId)).thenThrow(new ScooterNotFoundException());

        mockMvc.perform(patch("/scooter-rental/scooters/{scooterId}/put-in-warehouse", scooterId)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("ScooterNotFoundException"));

        verify(scooterService, times(1)).putScooterInWarehouse(scooterId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void putScooterInWarehouse_ShouldReturn409_WhenScooterInWarehouse() throws Exception {
        Long scooterId = 1L;

        when(scooterService.putScooterInWarehouse(scooterId)).thenThrow(new ScooterInWarehouseException());

        mockMvc.perform(patch("/scooter-rental/scooters/{scooterId}/put-in-warehouse", scooterId)
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("ScooterInWarehouseException"));

        verify(scooterService, times(1)).putScooterInWarehouse(scooterId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void putScooterInWarehouse_ShouldReturn409_WhenScooterAlreadyInRent() throws Exception {
        Long scooterId = 1L;

        when(scooterService.putScooterInWarehouse(scooterId)).thenThrow(new ScooterAlreadyInRentException());

        mockMvc.perform(patch("/scooter-rental/scooters/{scooterId}/put-in-warehouse", scooterId)
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("ScooterAlreadyInRentException"));

        verify(scooterService, times(1)).putScooterInWarehouse(scooterId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setNewScooterStatus_ShouldReturn200AndDto_WhenAdminRequestsAndAllCorrect() throws Exception {
        Long scooterId = 1L;
        ScooterStatus newStatus = ScooterStatus.AVAILABLE;

        ScooterResponseDto expected = new ScooterResponseDto();
        expected.setScooterId(scooterId);
        expected.setScooterStatus(newStatus.toString());

        when(scooterService.setNewScooterStatus(scooterId, newStatus)).thenReturn(expected);

        mockMvc.perform(patch("/scooter-rental/scooters/{scooterId}/set-new-scooter-status", scooterId)
                        .param("newScooterStatus", newStatus.toString())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scooterId").value(scooterId))
                .andExpect(jsonPath("$.scooterStatus").value(newStatus.toString()));

        verify(scooterService, times(1)).setNewScooterStatus(scooterId, newStatus);
    }

    @Test
    @WithMockUser(roles = "USER")
    void setNewScooterStatus_ShouldReturn403_WhenUserRequests() throws Exception {
        Long scooterId = 1L;
        ScooterStatus newStatus = ScooterStatus.AVAILABLE;

        mockMvc.perform(patch("/scooter-rental/scooters/{scooterId}/set-new-scooter-status", scooterId)
                        .param("newScooterStatus", newStatus.toString())
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));

        verify(scooterService, never()).setNewScooterStatus(anyLong(), any(ScooterStatus.class));
    }

    @Test
    void setNewScooterStatus_ShouldReturn401_WhenNonameRequests() throws Exception {
        Long scooterId = 1L;
        ScooterStatus newStatus = ScooterStatus.AVAILABLE;

        mockMvc.perform(patch("/scooter-rental/scooters/{scooterId}/set-new-scooter-status", scooterId)
                        .param("newScooterStatus", newStatus.toString())
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(scooterService, never()).setNewScooterStatus(anyLong(), any(ScooterStatus.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setNewScooterStatus_ShouldReturn404_WhenScooterNotFound() throws Exception {
        Long scooterId = 1L;
        ScooterStatus newStatus = ScooterStatus.AVAILABLE;

        when(scooterService.setNewScooterStatus(scooterId, newStatus)).thenThrow(new ScooterNotFoundException());

        mockMvc.perform(patch("/scooter-rental/scooters/{scooterId}/set-new-scooter-status", scooterId)
                        .param("newScooterStatus", newStatus.toString())
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("ScooterNotFoundException"));

        verify(scooterService, times(1)).setNewScooterStatus(scooterId, newStatus);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setNewScooterStatus_ShouldReturn409_WhenScooterAlreadyInRent() throws Exception {
        Long scooterId = 1L;
        ScooterStatus newStatus = ScooterStatus.IN_WAREHOUSE;

        when(scooterService.setNewScooterStatus(scooterId, newStatus)).thenThrow(new ScooterAlreadyInRentException());

        mockMvc.perform(patch("/scooter-rental/scooters/{scooterId}/set-new-scooter-status", scooterId)
                        .param("newScooterStatus", newStatus.toString())
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("ScooterAlreadyInRentException"));

        verify(scooterService, times(1)).setNewScooterStatus(scooterId, newStatus);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setNewScooterStatus_ShouldReturn400_WhenMissingRequestParam() throws Exception {
        Long scooterId = 1L;

        mockMvc.perform(patch("/scooter-rental/scooters/{scooterId}/set-new-scooter-status", scooterId)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("MissingServletRequestParameterException"));

        verify(scooterService, never()).setNewScooterStatus(anyLong(), any(ScooterStatus.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setNewScooterStatus_ShouldReturn400_WhenTypeMismatch() throws Exception {
        Long scooterId = 1L;

        mockMvc.perform(patch("/scooter-rental/scooters/{scooterId}/set-new-scooter-status", scooterId)
                        .param("newScooterStatus", "abc")
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("MethodArgumentTypeMismatchException"));

        verify(scooterService, never()).setNewScooterStatus(anyLong(), any(ScooterStatus.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteScooter_ShouldReturn200_WhenAdminRequestsAndAllCorrect() throws Exception {
        Long scooterId = 1L;

        doNothing().when(scooterService).deleteScooter(scooterId);

        mockMvc.perform(delete("/scooter-rental/scooters/{scooterId}/delete", scooterId)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(scooterService, times(1)).deleteScooter(scooterId);
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteScooter_ShouldReturn403_WhenUserRequests() throws Exception {
        Long scooterId = 1L;

        mockMvc.perform(delete("/scooter-rental/scooters/{scooterId}/delete", scooterId)
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));

        verify(scooterService, never()).deleteScooter(anyLong());
    }

    @Test
    void deleteScooter_ShouldReturn401_WhenNonameRequests() throws Exception {
        Long scooterId = 1L;

        mockMvc.perform(delete("/scooter-rental/scooters/{scooterId}/delete", scooterId)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(scooterService, never()).deleteScooter(anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteScooter_ShouldReturn500_WhenRuntimeExceptionThrown() throws Exception {
        Long scooterId = 1L;

        doThrow(new RuntimeException()).when(scooterService).deleteScooter(scooterId);

        mockMvc.perform(delete("/scooter-rental/scooters/{scooterId}/delete", scooterId)
                        .with(csrf()))
                .andExpect(status().isInternalServerError());

        verify(scooterService, times(1)).deleteScooter(scooterId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getScooter_ShouldReturn200AndDto_WhenAdminRequests() throws Exception {
        Long scooterId = 1L;
        ScooterResponseDto expected = new ScooterResponseDto();
        expected.setScooterId(scooterId);
        expected.setModel("Xiaomi");

        when(scooterService.getScooter(scooterId)).thenReturn(expected);

        mockMvc.perform(get("/scooter-rental/scooters/{scooterId}", scooterId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scooterId").value(scooterId))
                .andExpect(jsonPath("$.model").value("Xiaomi"));

        verify(scooterService, times(1)).getScooter(scooterId);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getScooter_ShouldReturn200AndDto_WhenUserRequests() throws Exception {
        Long scooterId = 1L;
        ScooterResponseDto expected = new ScooterResponseDto();
        expected.setScooterId(scooterId);
        expected.setModel("Xiaomi");

        when(scooterService.getScooter(scooterId)).thenReturn(expected);

        mockMvc.perform(get("/scooter-rental/scooters/{scooterId}", scooterId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scooterId").value(scooterId))
                .andExpect(jsonPath("$.model").value("Xiaomi"));

        verify(scooterService, times(1)).getScooter(scooterId);
    }

    @Test
    void getScooter_ShouldReturn401_WhenNonameRequests() throws Exception {
        Long scooterId = 1L;

        mockMvc.perform(get("/scooter-rental/scooters/{scooterId}", scooterId))
                .andExpect(status().isUnauthorized());

        verify(scooterService, never()).getScooter(anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getScooter_ShouldReturn404_WhenScooterNotFound() throws Exception {
        Long scooterId = 1L;

        when(scooterService.getScooter(scooterId)).thenThrow(new ScooterNotFoundException());

        mockMvc.perform(get("/scooter-rental/scooters/{scooterId}", scooterId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("ScooterNotFoundException"));

        verify(scooterService, times(1)).getScooter(scooterId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllScooters_ShouldReturn200AndList_WhenAdminRequests() throws Exception {
        ScooterResponseDto dto1 = new ScooterResponseDto();
        dto1.setScooterId(1L);
        dto1.setModel("Xiaomi");

        ScooterResponseDto dto2 = new ScooterResponseDto();
        dto2.setScooterId(2L);
        dto2.setModel("Ninebot");

        when(scooterService.getAllScooters()).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/scooter-rental/scooters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].scooterId").value(1L))
                .andExpect(jsonPath("$[0].model").value("Xiaomi"))
                .andExpect(jsonPath("$[1].scooterId").value(2L))
                .andExpect(jsonPath("$[1].model").value("Ninebot"));

        verify(scooterService, times(1)).getAllScooters();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllScooters_ShouldReturn200AndList_WhenUserRequests() throws Exception {
        ScooterResponseDto dto = new ScooterResponseDto();
        dto.setScooterId(1L);
        dto.setModel("Xiaomi");

        when(scooterService.getAllScooters()).thenReturn(List.of(dto));

        mockMvc.perform(get("/scooter-rental/scooters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].scooterId").value(1L))
                .andExpect(jsonPath("$[0].model").value("Xiaomi"));

        verify(scooterService, times(1)).getAllScooters();
    }

    @Test
    void getAllScooters_ShouldReturn401_WhenNonameRequests() throws Exception {
        mockMvc.perform(get("/scooter-rental/scooters"))
                .andExpect(status().isUnauthorized());

        verify(scooterService, never()).getAllScooters();
    }
}
