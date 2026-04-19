package org.scooterrental.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.scooterrental.controller.advice.GlobalExceptionHandler;
import org.scooterrental.model.entity.User;
import org.scooterrental.model.exception.RentalPointNotFoundException;
import org.scooterrental.model.exception.ScooterNotFoundException;
import org.scooterrental.model.exception.ScooterNotAvailableException;
import org.scooterrental.model.exception.UserBannedException;
import org.scooterrental.model.exception.UserAlreadyHasActiveTripException;
import org.scooterrental.model.exception.UserHasNoActiveSeasonTicketException;
import org.scooterrental.model.exception.UserNotFoundException;
import org.scooterrental.model.exception.TariffNotFoundException;
import org.scooterrental.model.exception.TripAlreadyCompletedException;
import org.scooterrental.model.exception.TripNotFoundException;
import org.scooterrental.model.exception.ValueLessZeroException;
import org.scooterrental.model.exception.LowBatteryLevelException;
import org.scooterrental.service.dto.TripCreateDto;
import org.scooterrental.service.dto.TripResponseDto;
import org.scooterrental.service.serviceinterface.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TripController.class)
@Import(GlobalExceptionHandler.class)
public class TripControllerTest {

    @SpringBootApplication
    @EnableMethodSecurity
    static class TestConfig {
    }

    @MockBean
    private TripService tripService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void startTrip_ShouldReturn200AndDto_WhenUserRequestsForSelfAndAllCorrect() throws Exception {
        Long userId = 1L;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        TripCreateDto tripCreateDto = new TripCreateDto();
        tripCreateDto.setUserId(userId);
        tripCreateDto.setScooterId(1L);
        tripCreateDto.setTariffId(1L);

        TripResponseDto expected = new TripResponseDto();
        expected.setTripId(1L);

        when(tripService.startTrip(any(TripCreateDto.class))).thenReturn(expected);

        mockMvc.perform(post("/scooter-rental/trips/start")
                        .with(csrf())
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tripCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripId").value(1L));

        verify(tripService, times(1)).startTrip(any(TripCreateDto.class));
    }

    @Test
    void startTrip_ShouldReturn403_WhenUserRequestsForOther() throws Exception {
        Long userId = 1L;
        Long otherUserId = 2L;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        TripCreateDto tripCreateDto = new TripCreateDto();
        tripCreateDto.setUserId(otherUserId);
        tripCreateDto.setScooterId(1L);
        tripCreateDto.setTariffId(1L);

        mockMvc.perform(post("/scooter-rental/trips/start")
                        .with(csrf())
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tripCreateDto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));

        verify(tripService, never()).startTrip(any(TripCreateDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void startTrip_ShouldReturn403_WhenAdminRequests() throws Exception {
        Long userId = 1L;

        TripCreateDto tripCreateDto = new TripCreateDto();
        tripCreateDto.setUserId(userId);
        tripCreateDto.setScooterId(1L);
        tripCreateDto.setTariffId(1L);

        mockMvc.perform(post("/scooter-rental/trips/start")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tripCreateDto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));

        verify(tripService, never()).startTrip(any(TripCreateDto.class));
    }

    @Test
    void startTrip_ShouldReturn401_WhenNonameRequests() throws Exception {
        mockMvc.perform(post("/scooter-rental/trips/start")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(tripService, never()).startTrip(any(TripCreateDto.class));
    }

    @Test
    void startTrip_ShouldReturn404_WhenUserNotFound() throws Exception {
        Long userId = 1L;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        TripCreateDto tripCreateDto = new TripCreateDto();
        tripCreateDto.setUserId(userId);
        tripCreateDto.setScooterId(1L);
        tripCreateDto.setTariffId(1L);

        when(tripService.startTrip(any(TripCreateDto.class))).thenThrow(new UserNotFoundException());

        mockMvc.perform(post("/scooter-rental/trips/start")
                        .with(csrf())
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tripCreateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("UserNotFoundException"));

        verify(tripService, times(1)).startTrip(any(TripCreateDto.class));
    }

    @Test
    void startTrip_ShouldReturn403_WhenUserBanned() throws Exception {
        Long userId = 1L;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        TripCreateDto tripCreateDto = new TripCreateDto();
        tripCreateDto.setUserId(userId);
        tripCreateDto.setScooterId(1L);
        tripCreateDto.setTariffId(1L);

        when(tripService.startTrip(any(TripCreateDto.class))).thenThrow(new UserBannedException());

        mockMvc.perform(post("/scooter-rental/trips/start")
                        .with(csrf())
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tripCreateDto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("UserBannedException"));

        verify(tripService, times(1)).startTrip(any(TripCreateDto.class));
    }

    @Test
    void startTrip_ShouldReturn400_WhenBalanceLessZero() throws Exception {
        Long userId = 1L;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        TripCreateDto tripCreateDto = new TripCreateDto();
        tripCreateDto.setUserId(userId);
        tripCreateDto.setScooterId(1L);
        tripCreateDto.setTariffId(1L);

        when(tripService.startTrip(any(TripCreateDto.class))).thenThrow(new ValueLessZeroException("Невозможно начать поездку при отрицательном балансе"));

        mockMvc.perform(post("/scooter-rental/trips/start")
                        .with(csrf())
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tripCreateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("ValueLessZeroException"));

        verify(tripService, times(1)).startTrip(any(TripCreateDto.class));
    }

    @Test
    void startTrip_ShouldReturn404_WhenScooterNotFound() throws Exception {
        Long userId = 1L;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        TripCreateDto tripCreateDto = new TripCreateDto();
        tripCreateDto.setUserId(userId);
        tripCreateDto.setScooterId(1L);
        tripCreateDto.setTariffId(1L);

        when(tripService.startTrip(any(TripCreateDto.class))).thenThrow(new ScooterNotFoundException());

        mockMvc.perform(post("/scooter-rental/trips/start")
                        .with(csrf())
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tripCreateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("ScooterNotFoundException"));

        verify(tripService, times(1)).startTrip(any(TripCreateDto.class));
    }

    @Test
    void startTrip_ShouldReturn409_WhenScooterNotAvailable() throws Exception {
        Long userId = 1L;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        TripCreateDto tripCreateDto = new TripCreateDto();
        tripCreateDto.setUserId(userId);
        tripCreateDto.setScooterId(1L);
        tripCreateDto.setTariffId(1L);

        when(tripService.startTrip(any(TripCreateDto.class))).thenThrow(new ScooterNotAvailableException());

        mockMvc.perform(post("/scooter-rental/trips/start")
                        .with(csrf())
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tripCreateDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("ScooterNotAvailableException"));

        verify(tripService, times(1)).startTrip(any(TripCreateDto.class));
    }

    @Test
    void startTrip_ShouldReturn400_WhenLowBatteryLevel() throws Exception {
        Long userId = 1L;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        TripCreateDto tripCreateDto = new TripCreateDto();
        tripCreateDto.setUserId(userId);
        tripCreateDto.setScooterId(1L);
        tripCreateDto.setTariffId(1L);

        when(tripService.startTrip(any(TripCreateDto.class))).thenThrow(new LowBatteryLevelException());

        mockMvc.perform(post("/scooter-rental/trips/start")
                        .with(csrf())
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tripCreateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("LowBatteryLevelException"));

        verify(tripService, times(1)).startTrip(any(TripCreateDto.class));
    }

    @Test
    void startTrip_ShouldReturn404_WhenTariffNotFound() throws Exception {
        Long userId = 1L;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        TripCreateDto tripCreateDto = new TripCreateDto();
        tripCreateDto.setUserId(userId);
        tripCreateDto.setScooterId(1L);
        tripCreateDto.setTariffId(1L);

        when(tripService.startTrip(any(TripCreateDto.class))).thenThrow(new TariffNotFoundException());

        mockMvc.perform(post("/scooter-rental/trips/start")
                        .with(csrf())
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tripCreateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("TariffNotFoundException"));

        verify(tripService, times(1)).startTrip(any(TripCreateDto.class));
    }

    @Test
    void startTrip_ShouldReturn409_WhenUserHasNoActiveSeasonTicket() throws Exception {
        Long userId = 1L;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        TripCreateDto tripCreateDto = new TripCreateDto();
        tripCreateDto.setUserId(userId);
        tripCreateDto.setScooterId(1L);
        tripCreateDto.setTariffId(1L);

        when(tripService.startTrip(any(TripCreateDto.class))).thenThrow(new UserHasNoActiveSeasonTicketException());

        mockMvc.perform(post("/scooter-rental/trips/start")
                        .with(csrf())
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tripCreateDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("UserHasNoActiveSeasonTicketException"));

        verify(tripService, times(1)).startTrip(any(TripCreateDto.class));
    }

    @Test
    void startTrip_ShouldReturn409_WhenUserAlreadyHasActiveTrip() throws Exception {
        Long userId = 1L;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        TripCreateDto tripCreateDto = new TripCreateDto();
        tripCreateDto.setUserId(userId);
        tripCreateDto.setScooterId(1L);
        tripCreateDto.setTariffId(1L);

        when(tripService.startTrip(any(TripCreateDto.class))).thenThrow(new UserAlreadyHasActiveTripException());

        mockMvc.perform(post("/scooter-rental/trips/start")
                        .with(csrf())
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tripCreateDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("UserAlreadyHasActiveTripException"));

        verify(tripService, times(1)).startTrip(any(TripCreateDto.class));
    }

    @Test
    void finishTrip_ShouldReturn200AndDto_WhenUserRequestsForSelfAndAllCorrect() throws Exception {
        Long tripId = 1L;
        Long userId = 1L;
        Long endPointId = 2L;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        TripResponseDto expected = new TripResponseDto();
        expected.setTripId(tripId);

        when(tripService.finishTrip(tripId, endPointId, userId)).thenReturn(expected);

        mockMvc.perform(patch("/scooter-rental/trips/{tripId}/finish", tripId)
                        .param("endRentalPointId", endPointId.toString())
                        .with(csrf())
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripId").value(tripId));

        verify(tripService, times(1)).finishTrip(tripId, endPointId, userId);
    }

    @Test
    void finishTrip_ShouldReturn403_WhenUserRequestsForOther() throws Exception {
        Long tripId = 1L;
        Long userId = 1L;
        Long endPointId = 2L;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        when(tripService.finishTrip(tripId, endPointId, userId)).thenThrow(new AccessDeniedException("Вы не можете завершить чужую поездку"));

        mockMvc.perform(patch("/scooter-rental/trips/{tripId}/finish", tripId)
                        .param("endRentalPointId", endPointId.toString())
                        .with(csrf())
                        .with(authentication(auth)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));

        verify(tripService, times(1)).finishTrip(tripId, endPointId, userId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void finishTrip_ShouldReturn403_WhenAdminRequests() throws Exception {
        Long tripId = 1L;
        Long userId = 1L;
        Long endPointId = 2L;

        mockMvc.perform(patch("/scooter-rental/trips/{tripId}/finish", tripId)
                        .param("endRentalPointId", endPointId.toString())
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));

        verify(tripService, never()).finishTrip(anyLong(), anyLong(), anyLong());
    }

    @Test
    void finishTrip_ShouldReturn401_WhenNonameRequests() throws Exception {
        Long tripId = 1L;
        Long endPointId = 2L;

        mockMvc.perform(patch("/scooter-rental/trips/{tripId}/finish", tripId)
                        .param("endRentalPointId", endPointId.toString())
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(tripService, never()).finishTrip(anyLong(), anyLong(), anyLong());
    }

    @Test
    void finishTrip_ShouldReturn404_WhenTripNotFound() throws Exception {
        Long tripId = 1L;
        Long userId = 1L;
        Long endPointId = 2L;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        when(tripService.finishTrip(tripId, endPointId, userId)).thenThrow(new TripNotFoundException());

        mockMvc.perform(patch("/scooter-rental/trips/{tripId}/finish", tripId)
                        .param("endRentalPointId", endPointId.toString())
                        .with(csrf())
                        .with(authentication(auth)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("TripNotFoundException"));

        verify(tripService, times(1)).finishTrip(tripId, endPointId, userId);
    }

    @Test
    void finishTrip_ShouldReturn409_WhenTripAlreadyCompleted() throws Exception {
        Long tripId = 1L;
        Long userId = 1L;
        Long endPointId = 2L;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        when(tripService.finishTrip(tripId, endPointId, userId)).thenThrow(new TripAlreadyCompletedException());

        mockMvc.perform(patch("/scooter-rental/trips/{tripId}/finish", tripId)
                        .param("endRentalPointId", endPointId.toString())
                        .with(csrf())
                        .with(authentication(auth)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("TripAlreadyCompletedException"));

        verify(tripService, times(1)).finishTrip(tripId, endPointId, userId);
    }

    @Test
    void finishTrip_ShouldReturn404_WhenRentalPointNotFound() throws Exception {
        Long tripId = 1L;
        Long userId = 1L;
        Long endPointId = 2L;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        when(tripService.finishTrip(tripId, endPointId, userId)).thenThrow(new RentalPointNotFoundException());

        mockMvc.perform(patch("/scooter-rental/trips/{tripId}/finish", tripId)
                        .param("endRentalPointId", endPointId.toString())
                        .with(csrf())
                        .with(authentication(auth)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("RentalPointNotFoundException"));

        verify(tripService, times(1)).finishTrip(tripId, endPointId, userId);
    }

    @Test
    void finishTrip_ShouldReturn400_WhenMissingRequestParam() throws Exception {
        Long tripId = 1L;
        Long userId = 1L;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        mockMvc.perform(patch("/scooter-rental/trips/{tripId}/finish", tripId)
                        .with(csrf())
                        .with(authentication(auth)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("MissingServletRequestParameterException"));

        verify(tripService, never()).finishTrip(anyLong(), anyLong(), anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void emergencyFinishTrip_ShouldReturn200AndDto_WhenAdminRequestsAndAllCorrect() throws Exception {
        Long tripId = 1L;

        TripResponseDto expected = new TripResponseDto();
        expected.setTripId(tripId);

        when(tripService.emergencyFinishTrip(tripId)).thenReturn(expected);

        mockMvc.perform(patch("/scooter-rental/trips/{tripId}/emergency-finish", tripId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripId").value(tripId));

        verify(tripService, times(1)).emergencyFinishTrip(tripId);
    }

    @Test
    @WithMockUser(roles = "USER")
    void emergencyFinishTrip_ShouldReturn403_WhenUserRequests() throws Exception {
        Long tripId = 1L;

        mockMvc.perform(patch("/scooter-rental/trips/{tripId}/emergency-finish", tripId)
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));

        verify(tripService, never()).emergencyFinishTrip(anyLong());
    }

    @Test
    void emergencyFinishTrip_ShouldReturn401_WhenNonameRequests() throws Exception {
        Long tripId = 1L;

        mockMvc.perform(patch("/scooter-rental/trips/{tripId}/emergency-finish", tripId)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(tripService, never()).emergencyFinishTrip(anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void emergencyFinishTrip_ShouldReturn404_WhenTripNotFound() throws Exception {
        Long tripId = 1L;

        when(tripService.emergencyFinishTrip(tripId)).thenThrow(new TripNotFoundException());

        mockMvc.perform(patch("/scooter-rental/trips/{tripId}/emergency-finish", tripId)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("TripNotFoundException"));

        verify(tripService, times(1)).emergencyFinishTrip(tripId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void emergencyFinishTrip_ShouldReturn409_WhenTripAlreadyCompleted() throws Exception {
        Long tripId = 1L;

        when(tripService.emergencyFinishTrip(tripId)).thenThrow(new TripAlreadyCompletedException());

        mockMvc.perform(patch("/scooter-rental/trips/{tripId}/emergency-finish", tripId)
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("TripAlreadyCompletedException"));

        verify(tripService, times(1)).emergencyFinishTrip(tripId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTrip_ShouldReturn200AndDto_WhenAdminRequestsAndAllCorrect() throws Exception {
        Long tripId = 1L;
        TripResponseDto expected = new TripResponseDto();
        expected.setTripId(tripId);

        when(tripService.getTrip(tripId)).thenReturn(expected);

        mockMvc.perform(get("/scooter-rental/trips/{tripId}", tripId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripId").value(tripId));

        verify(tripService, times(1)).getTrip(tripId);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getTrip_ShouldReturn403_WhenUserRequests() throws Exception {
        Long tripId = 1L;

        mockMvc.perform(get("/scooter-rental/trips/{tripId}", tripId))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));

        verify(tripService, never()).getTrip(anyLong());
    }

    @Test
    void getTrip_ShouldReturn401_WhenNonameRequests() throws Exception {
        Long tripId = 1L;

        mockMvc.perform(get("/scooter-rental/trips/{tripId}", tripId))
                .andExpect(status().isUnauthorized());

        verify(tripService, never()).getTrip(anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTrip_ShouldReturn404_WhenTripNotFound() throws Exception {
        Long tripId = 1L;

        when(tripService.getTrip(tripId)).thenThrow(new TripNotFoundException());

        mockMvc.perform(get("/scooter-rental/trips/{tripId}", tripId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("TripNotFoundException"));

        verify(tripService, times(1)).getTrip(tripId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllTrips_ShouldReturn200AndList_WhenAdminRequests() throws Exception {
        TripResponseDto dto1 = new TripResponseDto();
        dto1.setTripId(1L);

        TripResponseDto dto2 = new TripResponseDto();
        dto2.setTripId(2L);

        when(tripService.getAllTrips()).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/scooter-rental/trips"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].tripId").value(1L))
                .andExpect(jsonPath("$[1].tripId").value(2L));

        verify(tripService, times(1)).getAllTrips();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllTrips_ShouldReturn403_WhenUserRequests() throws Exception {
        mockMvc.perform(get("/scooter-rental/trips"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));

        verify(tripService, never()).getAllTrips();
    }

    @Test
    void getAllTrips_ShouldReturn401_WhenNonameRequests() throws Exception {
        mockMvc.perform(get("/scooter-rental/trips"))
                .andExpect(status().isUnauthorized());

        verify(tripService, never()).getAllTrips();
    }

    @Test
    void getUserHistory_ShouldReturn200AndList_WhenUserRequestsOwnHistory() throws Exception {
        Long userId = 1L;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        when(tripService.getUserHistory(userId)).thenReturn(List.of(new TripResponseDto()));

        mockMvc.perform(get("/scooter-rental/trips/user-history/{userId}", userId)
                        .with(authentication(auth)))
                .andExpect(status().isOk());

        verify(tripService, times(1)).getUserHistory(userId);
    }

    @Test
    void getUserHistory_ShouldReturn403_WhenUserRequestsOthersHistory() throws Exception {
        Long userId = 1L;
        Long otherUserId = 2L;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        mockMvc.perform(get("/scooter-rental/trips/user-history/{userId}", otherUserId)
                        .with(authentication(auth)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));

        verify(tripService, never()).getUserHistory(anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserHistory_ShouldReturn200AndList_WhenAdminRequests() throws Exception {
        Long userId = 1L;
        when(tripService.getUserHistory(userId)).thenReturn(List.of(new TripResponseDto()));

        mockMvc.perform(get("/scooter-rental/trips/user-history/{userId}", userId))
                .andExpect(status().isOk());

        verify(tripService, times(1)).getUserHistory(userId);
    }

    @Test
    void getUserHistory_ShouldReturn401_WhenNonameRequests() throws Exception {
        Long userId = 1L;

        mockMvc.perform(get("/scooter-rental/trips/user-history/{userId}", userId))
                .andExpect(status().isUnauthorized());

        verify(tripService, never()).getUserHistory(anyLong());
    }

    @Test
    void getUserHistory_ShouldReturn404_WhenUserNotFound() throws Exception {
        Long userId = 1L;
        User customUser = new User();
        customUser.setUserId(userId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, authorities);

        when(tripService.getUserHistory(userId)).thenThrow(new UserNotFoundException());

        mockMvc.perform(get("/scooter-rental/trips/user-history/{userId}", userId)
                        .with(authentication(auth)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("UserNotFoundException"));

        verify(tripService, times(1)).getUserHistory(userId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getScooterHistory_ShouldReturn200AndList_WhenAdminRequests() throws Exception {
        Long scooterId = 1L;
        when(tripService.getScooterHistory(scooterId)).thenReturn(List.of(new TripResponseDto()));

        mockMvc.perform(get("/scooter-rental/trips/scooter-history/{scooterId}", scooterId))
                .andExpect(status().isOk());

        verify(tripService, times(1)).getScooterHistory(scooterId);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getScooterHistory_ShouldReturn403_WhenUserRequests() throws Exception {
        Long scooterId = 1L;

        mockMvc.perform(get("/scooter-rental/trips/scooter-history/{scooterId}", scooterId))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));

        verify(tripService, never()).getScooterHistory(anyLong());
    }

    @Test
    void getScooterHistory_ShouldReturn401_WhenNonameRequests() throws Exception {
        Long scooterId = 1L;

        mockMvc.perform(get("/scooter-rental/trips/scooter-history/{scooterId}", scooterId))
                .andExpect(status().isUnauthorized());

        verify(tripService, never()).getScooterHistory(anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getScooterHistory_ShouldReturn404_WhenScooterNotFound() throws Exception {
        Long scooterId = 1L;

        when(tripService.getScooterHistory(scooterId)).thenThrow(new ScooterNotFoundException());

        mockMvc.perform(get("/scooter-rental/trips/scooter-history/{scooterId}", scooterId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("ScooterNotFoundException"));

        verify(tripService, times(1)).getScooterHistory(scooterId);
    }
}

