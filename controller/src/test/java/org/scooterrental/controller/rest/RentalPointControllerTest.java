package org.scooterrental.controller.rest;

import org.scooterrental.model.exception.RentalPointNotEmptyException;
import org.scooterrental.model.exception.SameRentalPointsIDException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.scooterrental.controller.advice.GlobalExceptionHandler;
import org.scooterrental.model.exception.RentalPointAlreadyExistsException;
import org.scooterrental.model.exception.RentalPointNotFoundException;
import org.scooterrental.service.dto.RentalPointCreateDto;
import org.scooterrental.service.dto.RentalPointDetailsDto;
import org.scooterrental.service.dto.RentalPointResponseDto;
import org.scooterrental.service.serviceinterface.RentalPointService;
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

@WebMvcTest(RentalPointController.class)
@Import(GlobalExceptionHandler.class)
public class RentalPointControllerTest {

    @SpringBootApplication
    @EnableMethodSecurity
    static class TestConfig {
    }

    @MockBean
    private RentalPointService rentalPointService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    void addNewRentalPoint_ShouldReturn200AndDto_WhenAllCorrectAndAdminRequests() throws Exception {
        Long rentalPointId = 1L;

        RentalPointResponseDto expected = new RentalPointResponseDto();
        expected.setRentalPointId(1L);
        expected.setLocation("Москва");

        RentalPointCreateDto rentalPointCreateDto = new RentalPointCreateDto();
        rentalPointCreateDto.setLocation("Москва");

        when(rentalPointService.addNewRentalPoint(any(RentalPointCreateDto.class))).thenReturn(expected);

        mockMvc.perform(post("/scooter-rental/rental-points/add")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rentalPointCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rentalPointId").value(1L))
                .andExpect(jsonPath("$.location").value("Москва"));

        verify(rentalPointService, times(1)).addNewRentalPoint(any(RentalPointCreateDto.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void addNewRentalPoint_ShouldReturn403_WhenUserRequests() throws Exception {
        Long rentalPointId = 1L;

        RentalPointResponseDto expected = new RentalPointResponseDto();
        expected.setRentalPointId(1L);
        expected.setLocation("Москва");

        RentalPointCreateDto rentalPointCreateDto = new RentalPointCreateDto();
        rentalPointCreateDto.setLocation("Москва");

        when(rentalPointService.addNewRentalPoint(any(RentalPointCreateDto.class))).thenReturn(expected);

        mockMvc.perform(post("/scooter-rental/rental-points/add")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rentalPointCreateDto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));

        verify(rentalPointService, never()).addNewRentalPoint(any(RentalPointCreateDto.class));
    }

    @Test
    void addNewRentalPoint_ShouldReturn401_WhenNonameRequests() throws Exception {
        Long rentalPointId = 1L;

        RentalPointResponseDto expected = new RentalPointResponseDto();
        expected.setRentalPointId(1L);
        expected.setLocation("Москва");

        RentalPointCreateDto rentalPointCreateDto = new RentalPointCreateDto();
        rentalPointCreateDto.setLocation("Москва");

        when(rentalPointService.addNewRentalPoint(any(RentalPointCreateDto.class))).thenReturn(expected);

        mockMvc.perform(post("/scooter-rental/rental-points/add")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rentalPointCreateDto)))
                .andExpect(status().isUnauthorized());

        verify(rentalPointService, never()).addNewRentalPoint(any(RentalPointCreateDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addNewRentalPoint_ShouldReturn404_WhenAdminRequestsAndRentalPointNotFound() throws Exception {
        Long rentalPointId = 1L;

        RentalPointCreateDto rentalPointCreateDto = new RentalPointCreateDto();
        rentalPointCreateDto.setLocation("Москва");

        when(rentalPointService.addNewRentalPoint(any(RentalPointCreateDto.class))).thenThrow(new RentalPointNotFoundException());

        mockMvc.perform(post("/scooter-rental/rental-points/add")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rentalPointCreateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("RentalPointNotFoundException"));

        verify(rentalPointService, times(1)).addNewRentalPoint(any(RentalPointCreateDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setNewRentalPointLocation_ShouldReturn200AndDto_WhenAdminRequestsAndAllCorrect() throws Exception {
        Long rentalPointId = 1L;

        RentalPointResponseDto expected = new RentalPointResponseDto();
        expected.setRentalPointId(rentalPointId);
        expected.setLocation("Москва");

        RentalPointCreateDto rentalPointCreateDto = new RentalPointCreateDto();

        when(rentalPointService.setNewRentalPointLocation(rentalPointId, "Москва")).thenReturn(expected);

        mockMvc.perform(patch("/scooter-rental/rental-points/{rentalPointId}/set-new-location", rentalPointId)
                        .param("newLocation", "Москва")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rentalPointId").value(rentalPointId))
                .andExpect(jsonPath("$.location").value("Москва"));

        verify(rentalPointService, times(1)).setNewRentalPointLocation(anyLong(), anyString());
    }

    @Test
    @WithMockUser(roles = "USER")
    void setNewRentalPointLocation_ShouldReturn403_WhenUserRequests() throws Exception {
        Long rentalPointId = 1L;

        RentalPointResponseDto expected = new RentalPointResponseDto();
        expected.setRentalPointId(rentalPointId);
        expected.setLocation("Москва");

        RentalPointCreateDto rentalPointCreateDto = new RentalPointCreateDto();

        when(rentalPointService.setNewRentalPointLocation(rentalPointId, "Москва")).thenReturn(expected);

        mockMvc.perform(patch("/scooter-rental/rental-points/{rentalPointId}/set-new-location", rentalPointId)
                        .param("newLocation", "Москва")
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));

        verify(rentalPointService, never()).setNewRentalPointLocation(anyLong(), anyString());
    }

    @Test
    void setNewRentalPointLocation_ShouldReturn401_WhenNonameRequests() throws Exception {
        Long rentalPointId = 1L;

        RentalPointResponseDto expected = new RentalPointResponseDto();
        expected.setRentalPointId(rentalPointId);
        expected.setLocation("Москва");

        RentalPointCreateDto rentalPointCreateDto = new RentalPointCreateDto();

        when(rentalPointService.setNewRentalPointLocation(rentalPointId, "Москва")).thenReturn(expected);

        mockMvc.perform(patch("/scooter-rental/rental-points/{rentalPointId}/set-new-location", rentalPointId)
                        .param("newLocation", "Москва")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(rentalPointService, never()).setNewRentalPointLocation(anyLong(), anyString());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setNewRentalPointLocation_ShouldReturn409_WhenRentalPointWithSameLocationAlreadyExists() throws Exception {
        Long rentalPointId = 1L;

        RentalPointCreateDto rentalPointCreateDto = new RentalPointCreateDto();
        rentalPointCreateDto.setLocation("Москва");

        when(rentalPointService.setNewRentalPointLocation(rentalPointId, "Москва")).thenThrow(new RentalPointAlreadyExistsException());

        mockMvc.perform(patch("/scooter-rental/rental-points/{rentalPointId}/set-new-location", rentalPointId)
                        .param("newLocation", "Москва")
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("RentalPointAlreadyExistsException"));

        verify(rentalPointService, times(1)).setNewRentalPointLocation(anyLong(), anyString());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setNewRentalPointLocation_ShouldReturn404_WhenRentalPointNotFound() throws Exception {
        Long rentalPointId = 1L;

        RentalPointCreateDto rentalPointCreateDto = new RentalPointCreateDto();
        rentalPointCreateDto.setLocation("Москва");

        when(rentalPointService.setNewRentalPointLocation(rentalPointId, "Москва")).thenThrow(new RentalPointNotFoundException());

        mockMvc.perform(patch("/scooter-rental/rental-points/{rentalPointId}/set-new-location", rentalPointId)
                        .param("newLocation", "Москва")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("RentalPointNotFoundException"));

        verify(rentalPointService, times(1)).setNewRentalPointLocation(anyLong(), anyString());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setNewRentalPointLocation_ShouldReturn400_WhenMissingRequestParam() throws Exception {
        Long rentalPointId = 1L;

        mockMvc.perform(patch("/scooter-rental/rental-points/{rentalPointId}/set-new-location", rentalPointId)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("MissingServletRequestParameterException"));

        verify(rentalPointService, never()).setNewRentalPointLocation(anyLong(), anyString());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setNewParentPointId_ShouldReturn200AndDto_WhenAdminRequestsAndAllCorrect() throws Exception {
        Long rentalPointId = 1L;
        Long newParentPointId = 2L;

        RentalPointResponseDto expected = new RentalPointResponseDto();
        expected.setRentalPointId(rentalPointId);
        expected.setParentPointId(newParentPointId);

        when(rentalPointService.setNewParentPointId(rentalPointId, newParentPointId)).thenReturn(expected);

        mockMvc.perform(patch("/scooter-rental/rental-points/{rentalPointId}/set-new-parent-point-id", rentalPointId)
                        .param("newParentPointId", newParentPointId.toString())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rentalPointId").value(rentalPointId))
                .andExpect(jsonPath("$.parentPointId").value(newParentPointId));

        verify(rentalPointService, times(1)).setNewParentPointId(rentalPointId, newParentPointId);
    }

    @Test
    @WithMockUser(roles = "USER")
    void setNewParentPointId_ShouldReturn403_WhenUserRequests() throws Exception {
        Long rentalPointId = 1L;
        Long newParentPointId = 2L;

        mockMvc.perform(patch("/scooter-rental/rental-points/{rentalPointId}/set-new-parent-point-id", rentalPointId)
                        .param("newParentPointId", newParentPointId.toString())
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));

        verify(rentalPointService, never()).setNewParentPointId(anyLong(), anyLong());
    }

    @Test
    void setNewParentPointId_ShouldReturn401_WhenNonameRequests() throws Exception {
        Long rentalPointId = 1L;
        Long newParentPointId = 2L;

        mockMvc.perform(patch("/scooter-rental/rental-points/{rentalPointId}/set-new-parent-point-id", rentalPointId)
                        .param("newParentPointId", newParentPointId.toString())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(rentalPointService, never()).setNewParentPointId(anyLong(), anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setNewParentPointId_ShouldReturn409_WhenSameRentalPointsID() throws Exception {
        Long rentalPointId = 1L;
        Long newParentPointId = 1L;

        when(rentalPointService.setNewParentPointId(rentalPointId, newParentPointId)).thenThrow(new SameRentalPointsIDException());

        mockMvc.perform(patch("/scooter-rental/rental-points/{rentalPointId}/set-new-parent-point-id", rentalPointId)
                        .param("newParentPointId", newParentPointId.toString())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("SameRentalPointsIDException"));

        verify(rentalPointService, times(1)).setNewParentPointId(rentalPointId, newParentPointId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setNewParentPointId_ShouldReturn404_WhenRentalPointNotFound() throws Exception {
        Long rentalPointId = 1L;
        Long newParentPointId = 2L;

        when(rentalPointService.setNewParentPointId(rentalPointId, newParentPointId)).thenThrow(new RentalPointNotFoundException());

        mockMvc.perform(patch("/scooter-rental/rental-points/{rentalPointId}/set-new-parent-point-id", rentalPointId)
                        .param("newParentPointId", newParentPointId.toString())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("RentalPointNotFoundException"));

        verify(rentalPointService, times(1)).setNewParentPointId(rentalPointId, newParentPointId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setNewParentPointId_ShouldReturn400_WhenMissingRequestParam() throws Exception {
        Long rentalPointId = 1L;

        mockMvc.perform(patch("/scooter-rental/rental-points/{rentalPointId}/set-new-parent-point-id", rentalPointId)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("MissingServletRequestParameterException"));

        verify(rentalPointService, never()).setNewParentPointId(anyLong(), anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteRentalPoint_ShouldReturn200_WhenAdminRequestsAndAllCorrect() throws Exception {
        Long rentalPointId = 1L;

        mockMvc.perform(delete("/scooter-rental/rental-points/{rentalPointId}/delete", rentalPointId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Удаление точки аренды успешно"));

        verify(rentalPointService, times(1)).deleteRentalPoint(rentalPointId);
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteRentalPoint_ShouldReturn403_WhenUserRequests() throws Exception {
        Long rentalPointId = 1L;

        mockMvc.perform(delete("/scooter-rental/rental-points/{rentalPointId}/delete", rentalPointId)
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));

        verify(rentalPointService, never()).deleteRentalPoint(anyLong());
    }

    @Test
    void deleteRentalPoint_ShouldReturn401_WhenNonameRequests() throws Exception {
        Long rentalPointId = 1L;

        mockMvc.perform(delete("/scooter-rental/rental-points/{rentalPointId}/delete", rentalPointId)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(rentalPointService, never()).deleteRentalPoint(anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteRentalPoint_ShouldReturn409_WhenRentalPointNotEmpty() throws Exception {
        Long rentalPointId = 1L;

        doThrow(new RentalPointNotEmptyException()).when(rentalPointService).deleteRentalPoint(rentalPointId);

        mockMvc.perform(delete("/scooter-rental/rental-points/{rentalPointId}/delete", rentalPointId)
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("RentalPointNotEmptyException"));

        verify(rentalPointService, times(1)).deleteRentalPoint(rentalPointId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteRentalPoint_ShouldReturn404_WhenRentalPointNotFound() throws Exception {
        Long rentalPointId = 1L;

        doThrow(new RentalPointNotFoundException()).when(rentalPointService).deleteRentalPoint(rentalPointId);

        mockMvc.perform(delete("/scooter-rental/rental-points/{rentalPointId}/delete", rentalPointId)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("RentalPointNotFoundException"));

        verify(rentalPointService, times(1)).deleteRentalPoint(rentalPointId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllRentalPoints_ShouldReturn200AndList_WhenAdminRequests() throws Exception {
        RentalPointResponseDto dto1 = new RentalPointResponseDto();
        dto1.setRentalPointId(1L);
        dto1.setLocation("Москва");

        RentalPointResponseDto dto2 = new RentalPointResponseDto();
        dto2.setRentalPointId(2L);
        dto2.setLocation("Санкт-Петербург");

        when(rentalPointService.getAllRentalPoints()).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/scooter-rental/rental-points")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].rentalPointId").value(1L))
                .andExpect(jsonPath("$[0].location").value("Москва"))
                .andExpect(jsonPath("$[1].rentalPointId").value(2L))
                .andExpect(jsonPath("$[1].location").value("Санкт-Петербург"));

        verify(rentalPointService, times(1)).getAllRentalPoints();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllRentalPoints_ShouldReturn200AndList_WhenUserRequests() throws Exception {
        RentalPointResponseDto dto = new RentalPointResponseDto();
        dto.setRentalPointId(1L);
        dto.setLocation("Москва");

        when(rentalPointService.getAllRentalPoints()).thenReturn(List.of(dto));

        mockMvc.perform(get("/scooter-rental/rental-points")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].rentalPointId").value(1L))
                .andExpect(jsonPath("$[0].location").value("Москва"));

        verify(rentalPointService, times(1)).getAllRentalPoints();
    }

    @Test
    void getAllRentalPoints_ShouldReturn401_WhenNonameRequests() throws Exception {
        mockMvc.perform(get("/scooter-rental/rental-points")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(rentalPointService, never()).getAllRentalPoints();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRentalPointDetails_ShouldReturn200AndDto_WhenAdminRequests() throws Exception {
        Long rentalPointId = 1L;
        RentalPointDetailsDto expected = new RentalPointDetailsDto();
        expected.setRentalPointId(rentalPointId);
        expected.setLocation("Москва");

        when(rentalPointService.getRentalPointDetails(rentalPointId)).thenReturn(expected);

        mockMvc.perform(get("/scooter-rental/rental-points/{rentalPointId}/details", rentalPointId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rentalPointId").value(rentalPointId))
                .andExpect(jsonPath("$.location").value("Москва"));

        verify(rentalPointService, times(1)).getRentalPointDetails(rentalPointId);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getRentalPointDetails_ShouldReturn200AndDto_WhenUserRequests() throws Exception {
        Long rentalPointId = 1L;
        RentalPointDetailsDto expected = new RentalPointDetailsDto();
        expected.setRentalPointId(rentalPointId);
        expected.setLocation("Москва");

        when(rentalPointService.getRentalPointDetails(rentalPointId)).thenReturn(expected);

        mockMvc.perform(get("/scooter-rental/rental-points/{rentalPointId}/details", rentalPointId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rentalPointId").value(rentalPointId))
                .andExpect(jsonPath("$.location").value("Москва"));

        verify(rentalPointService, times(1)).getRentalPointDetails(rentalPointId);
    }

    @Test
    void getRentalPointDetails_ShouldReturn401_WhenNonameRequests() throws Exception {
        Long rentalPointId = 1L;

        mockMvc.perform(get("/scooter-rental/rental-points/{rentalPointId}/details", rentalPointId))
                .andExpect(status().isUnauthorized());

        verify(rentalPointService, never()).getRentalPointDetails(anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRentalPointDetails_ShouldReturn404_WhenRentalPointNotFound() throws Exception {
        Long rentalPointId = 1L;

        when(rentalPointService.getRentalPointDetails(rentalPointId)).thenThrow(new RentalPointNotFoundException());

        mockMvc.perform(get("/scooter-rental/rental-points/{rentalPointId}/details", rentalPointId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("RentalPointNotFoundException"));

        verify(rentalPointService, times(1)).getRentalPointDetails(rentalPointId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRentalPoint_ShouldReturn200AndDto_WhenAllCorrectAndAdminRequests() throws Exception {
        Long rentalPointId = 1L;
        RentalPointResponseDto expected = new RentalPointResponseDto();
        expected.setRentalPointId(rentalPointId);
        expected.setLocation("Москва");

        when(rentalPointService.getRentalPoint(rentalPointId)).thenReturn(expected);

        mockMvc.perform(get("/scooter-rental/rental-points/{rentalPointId}", rentalPointId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rentalPointId").value(rentalPointId))
                .andExpect(jsonPath("$.location").value("Москва"));
        verify(rentalPointService, times(1)).getRentalPoint(rentalPointId);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getRentalPoint_ShouldReturn200AndDto_WhenAllCorrectAndUserRequests() throws Exception {
        Long rentalPointId = 1L;
        RentalPointResponseDto expected = new RentalPointResponseDto();
        expected.setRentalPointId(rentalPointId);
        expected.setLocation("Москва");

        when(rentalPointService.getRentalPoint(rentalPointId)).thenReturn(expected);

        mockMvc.perform(get("/scooter-rental/rental-points/{rentalPointId}", rentalPointId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rentalPointId").value(rentalPointId))
                .andExpect(jsonPath("$.location").value("Москва"));
        verify(rentalPointService, times(1)).getRentalPoint(rentalPointId);
    }

    @Test
    void getRentalPoint_ShouldReturn401_WhenNonameRequests() throws Exception {
        Long rentalPointId = 1L;
        RentalPointResponseDto expected = new RentalPointResponseDto();
        expected.setRentalPointId(rentalPointId);
        expected.setLocation("Москва");

        when(rentalPointService.getRentalPoint(rentalPointId)).thenReturn(expected);

        mockMvc.perform(get("/scooter-rental/rental-points/{rentalPointId}", rentalPointId))
                .andExpect(status().isUnauthorized());
        verify(rentalPointService, never()).getRentalPoint(rentalPointId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRentalPoint_ShouldReturn404AndDto_WhenRentalPointNotFoundAndAdminRequests() throws Exception {
        Long rentalPointId = 1L;

        when(rentalPointService.getRentalPoint(rentalPointId)).thenThrow(new RentalPointNotFoundException());

        mockMvc.perform(get("/scooter-rental/rental-points/{rentalPointId}", rentalPointId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("RentalPointNotFoundException"));
        verify(rentalPointService, times(1)).getRentalPoint(rentalPointId);
    }
}
