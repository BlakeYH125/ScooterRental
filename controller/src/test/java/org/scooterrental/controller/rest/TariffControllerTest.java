package org.scooterrental.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.scooterrental.controller.advice.GlobalExceptionHandler;
import org.scooterrental.model.enums.PaymentType;
import org.scooterrental.model.exception.TariffNotFoundException;
import org.scooterrental.model.exception.ValueLessZeroException;
import org.scooterrental.service.dto.TariffCreateDto;
import org.scooterrental.service.dto.TariffResponseDto;
import org.scooterrental.service.serviceinterface.TariffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TariffController.class)
@Import(GlobalExceptionHandler.class)
public class TariffControllerTest {

    @SpringBootApplication
    @EnableMethodSecurity
    static class TestConfig {
    }

    @MockBean
    private TariffService tariffService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    void addNewTariff_ShouldReturn200AndDto_WhenAdminRequestsAndAllCorrect() throws Exception {
        TariffResponseDto expected = new TariffResponseDto();
        expected.setTariffId(1L);

        TariffCreateDto tariffCreateDto = new TariffCreateDto();
        tariffCreateDto.setPrice(new BigDecimal(400));
        tariffCreateDto.setDiscount(0);
        tariffCreateDto.setPaymentType("HOURLY");

        when(tariffService.addNewTariff(any(TariffCreateDto.class))).thenReturn(expected);

        mockMvc.perform(post("/scooter-rental/tariffs/add")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tariffCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tariffId").value(1L));

        verify(tariffService, times(1)).addNewTariff(any(TariffCreateDto.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void addNewTariff_ShouldReturn403_WhenUserRequests() throws Exception {
        TariffCreateDto tariffCreateDto = new TariffCreateDto();
        tariffCreateDto.setPrice(new BigDecimal(500));
        tariffCreateDto.setDiscount(15);
        tariffCreateDto.setPaymentType("HOURLY");

        mockMvc.perform(post("/scooter-rental/tariffs/add")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tariffCreateDto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));

        verify(tariffService, never()).addNewTariff(any(TariffCreateDto.class));
    }

    @Test
    void addNewTariff_ShouldReturn401_WhenNonameRequests() throws Exception {
        TariffCreateDto tariffCreateDto = new TariffCreateDto();

        mockMvc.perform(post("/scooter-rental/tariffs/add")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tariffCreateDto)))
                .andExpect(status().isUnauthorized());

        verify(tariffService, never()).addNewTariff(any(TariffCreateDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setNewPaymentType_ShouldReturn200AndDto_WhenAdminRequestsAndAllCorrect() throws Exception {
        Long tariffId = 1L;
        PaymentType newPaymentType = PaymentType.HOURLY;

        TariffResponseDto expected = new TariffResponseDto();
        expected.setTariffId(tariffId);
        expected.setPaymentType(newPaymentType.toString());

        when(tariffService.setNewPaymentType(tariffId, newPaymentType)).thenReturn(expected);

        mockMvc.perform(patch("/scooter-rental/tariffs/{tariffId}/set-new-payment-type", tariffId)
                        .param("newPaymentType", newPaymentType.toString())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tariffId").value(tariffId))
                .andExpect(jsonPath("$.paymentType").value(newPaymentType.toString()));

        verify(tariffService, times(1)).setNewPaymentType(tariffId, newPaymentType);
    }

    @Test
    @WithMockUser(roles = "USER")
    void setNewPaymentType_ShouldReturn403_WhenUserRequests() throws Exception {
        Long tariffId = 1L;
        PaymentType newPaymentType = PaymentType.HOURLY;

        mockMvc.perform(patch("/scooter-rental/tariffs/{tariffId}/set-new-payment-type", tariffId)
                        .param("newPaymentType", newPaymentType.toString())
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));

        verify(tariffService, never()).setNewPaymentType(anyLong(), any(PaymentType.class));
    }

    @Test
    void setNewPaymentType_ShouldReturn401_WhenNonameRequests() throws Exception {
        Long tariffId = 1L;
        PaymentType newPaymentType = PaymentType.HOURLY;

        mockMvc.perform(patch("/scooter-rental/tariffs/{tariffId}/set-new-payment-type", tariffId)
                        .param("newPaymentType", newPaymentType.toString())
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(tariffService, never()).setNewPaymentType(anyLong(), any(PaymentType.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setNewPaymentType_ShouldReturn404_WhenTariffNotFound() throws Exception {
        Long tariffId = 1L;
        PaymentType newPaymentType = PaymentType.HOURLY;

        when(tariffService.setNewPaymentType(tariffId, newPaymentType)).thenThrow(new TariffNotFoundException());

        mockMvc.perform(patch("/scooter-rental/tariffs/{tariffId}/set-new-payment-type", tariffId)
                        .param("newPaymentType", newPaymentType.toString())
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("TariffNotFoundException"));

        verify(tariffService, times(1)).setNewPaymentType(tariffId, newPaymentType);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setNewPaymentType_ShouldReturn400_WhenMissingRequestParam() throws Exception {
        Long tariffId = 1L;

        mockMvc.perform(patch("/scooter-rental/tariffs/{tariffId}/set-new-payment-type", tariffId)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("MissingServletRequestParameterException"));

        verify(tariffService, never()).setNewPaymentType(anyLong(), any(PaymentType.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setNewPaymentType_ShouldReturn400_WhenPaymentTypeIsInvalid() throws Exception {
        Long tariffId = 1L;

        mockMvc.perform(patch("/scooter-rental/tariffs/{tariffId}/set-new-payment-type", tariffId)
                        .param("newPaymentType", "abc")
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("MethodArgumentTypeMismatchException"));

        verify(tariffService, never()).setNewPaymentType(anyLong(), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setNewPrice_ShouldReturn200AndDto_WhenAdminRequestsAndAllCorrect() throws Exception {
        Long tariffId = 1L;
        BigDecimal newPrice = BigDecimal.valueOf(150.0);

        TariffResponseDto expected = new TariffResponseDto();
        expected.setTariffId(tariffId);
        expected.setPrice(newPrice);

        when(tariffService.setNewPrice(tariffId, newPrice)).thenReturn(expected);

        mockMvc.perform(patch("/scooter-rental/tariffs/{tariffId}/set-new-price", tariffId)
                        .param("newPrice", newPrice.toString())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tariffId").value(tariffId))
                .andExpect(jsonPath("$.price").value(150.0));

        verify(tariffService, times(1)).setNewPrice(tariffId, newPrice);
    }

    @Test
    @WithMockUser(roles = "USER")
    void setNewPrice_ShouldReturn403_WhenUserRequests() throws Exception {
        Long tariffId = 1L;
        BigDecimal newPrice = BigDecimal.valueOf(150.0);

        mockMvc.perform(patch("/scooter-rental/tariffs/{tariffId}/set-new-price", tariffId)
                        .param("newPrice", newPrice.toString())
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));

        verify(tariffService, never()).setNewPrice(anyLong(), any(BigDecimal.class));
    }

    @Test
    void setNewPrice_ShouldReturn401_WhenNonameRequests() throws Exception {
        Long tariffId = 1L;
        BigDecimal newPrice = BigDecimal.valueOf(150.0);

        mockMvc.perform(patch("/scooter-rental/tariffs/{tariffId}/set-new-price", tariffId)
                        .param("newPrice", newPrice.toString())
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(tariffService, never()).setNewPrice(anyLong(), any(BigDecimal.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setNewPrice_ShouldReturn404_WhenTariffNotFound() throws Exception {
        Long tariffId = 1L;
        BigDecimal newPrice = BigDecimal.valueOf(150.0);

        when(tariffService.setNewPrice(tariffId, newPrice)).thenThrow(new TariffNotFoundException());

        mockMvc.perform(patch("/scooter-rental/tariffs/{tariffId}/set-new-price", tariffId)
                        .param("newPrice", newPrice.toString())
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("TariffNotFoundException"));

        verify(tariffService, times(1)).setNewPrice(tariffId, newPrice);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setNewPrice_ShouldReturn400_WhenPriceIsNegative() throws Exception {
        Long tariffId = 1L;
        BigDecimal newPrice = BigDecimal.valueOf(-150.0);

        when(tariffService.setNewPrice(tariffId, newPrice)).thenThrow(new ValueLessZeroException());

        mockMvc.perform(patch("/scooter-rental/tariffs/{tariffId}/set-new-price", tariffId)
                        .param("newPrice", newPrice.toString())
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("ValueLessZeroException"));

        verify(tariffService, times(1)).setNewPrice(tariffId, newPrice);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setNewPrice_ShouldReturn400_WhenMissingRequestParam() throws Exception {
        Long tariffId = 1L;

        mockMvc.perform(patch("/scooter-rental/tariffs/{tariffId}/set-new-price", tariffId)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("MissingServletRequestParameterException"));

        verify(tariffService, never()).setNewPrice(anyLong(), any(BigDecimal.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setNewDiscount_ShouldReturn200AndDto_WhenAdminRequestsAndAllCorrect() throws Exception {
        Long tariffId = 1L;
        int newDiscount = 15;

        TariffResponseDto expected = new TariffResponseDto();
        expected.setTariffId(tariffId);
        expected.setDiscount(newDiscount);

        when(tariffService.setNewDiscount(tariffId, newDiscount)).thenReturn(expected);

        mockMvc.perform(patch("/scooter-rental/tariffs/{tariffId}/set-new-discount", tariffId)
                        .param("newDiscount", String.valueOf(newDiscount))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tariffId").value(tariffId))
                .andExpect(jsonPath("$.discount").value(newDiscount));

        verify(tariffService, times(1)).setNewDiscount(tariffId, newDiscount);
    }

    @Test
    @WithMockUser(roles = "USER")
    void setNewDiscount_ShouldReturn403_WhenUserRequests() throws Exception {
        Long tariffId = 1L;
        int newDiscount = 15;

        mockMvc.perform(patch("/scooter-rental/tariffs/{tariffId}/set-new-discount", tariffId)
                        .param("newDiscount", String.valueOf(newDiscount))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));

        verify(tariffService, never()).setNewDiscount(anyLong(), anyInt());
    }

    @Test
    void setNewDiscount_ShouldReturn401_WhenNonameRequests() throws Exception {
        Long tariffId = 1L;
        int newDiscount = 15;

        mockMvc.perform(patch("/scooter-rental/tariffs/{tariffId}/set-new-discount", tariffId)
                        .param("newDiscount", String.valueOf(newDiscount))
                        .with(csrf()))
                .andExpect(status().isUnauthorized());

        verify(tariffService, never()).setNewDiscount(anyLong(), anyInt());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setNewDiscount_ShouldReturn404_WhenTariffNotFound() throws Exception {
        Long tariffId = 1L;
        int newDiscount = 15;

        when(tariffService.setNewDiscount(tariffId, newDiscount)).thenThrow(new TariffNotFoundException());

        mockMvc.perform(patch("/scooter-rental/tariffs/{tariffId}/set-new-discount", tariffId)
                        .param("newDiscount", String.valueOf(newDiscount))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("TariffNotFoundException"));

        verify(tariffService, times(1)).setNewDiscount(tariffId, newDiscount);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setNewDiscount_ShouldReturn400_WhenDiscountIsInvalid() throws Exception {
        Long tariffId = 1L;
        int newDiscount = 150;

        when(tariffService.setNewDiscount(tariffId, newDiscount)).thenThrow(new IllegalArgumentException());

        mockMvc.perform(patch("/scooter-rental/tariffs/{tariffId}/set-new-discount", tariffId)
                        .param("newDiscount", String.valueOf(newDiscount))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("IllegalArgumentException"));

        verify(tariffService, times(1)).setNewDiscount(tariffId, newDiscount);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setNewDiscount_ShouldReturn400_WhenMissingRequestParam() throws Exception {
        Long tariffId = 1L;

        mockMvc.perform(patch("/scooter-rental/tariffs/{tariffId}/set-new-discount", tariffId)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("MissingServletRequestParameterException"));

        verify(tariffService, never()).setNewDiscount(anyLong(), anyInt());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTariff_ShouldReturn200AndDto_WhenAdminRequests() throws Exception {
        Long tariffId = 1L;
        TariffResponseDto expected = new TariffResponseDto();
        expected.setTariffId(tariffId);

        when(tariffService.getTariff(tariffId)).thenReturn(expected);

        mockMvc.perform(get("/scooter-rental/tariffs/{tariffId}", tariffId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tariffId").value(tariffId));

        verify(tariffService, times(1)).getTariff(tariffId);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getTariff_ShouldReturn200AndDto_WhenUserRequests() throws Exception {
        Long tariffId = 1L;
        TariffResponseDto expected = new TariffResponseDto();
        expected.setTariffId(tariffId);

        when(tariffService.getTariff(tariffId)).thenReturn(expected);

        mockMvc.perform(get("/scooter-rental/tariffs/{tariffId}", tariffId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tariffId").value(tariffId));

        verify(tariffService, times(1)).getTariff(tariffId);
    }

    @Test
    void getTariff_ShouldReturn401_WhenNonameRequests() throws Exception {
        Long tariffId = 1L;

        mockMvc.perform(get("/scooter-rental/tariffs/{tariffId}", tariffId))
                .andExpect(status().isUnauthorized());

        verify(tariffService, never()).getTariff(anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTariff_ShouldReturn404_WhenTariffNotFound() throws Exception {
        Long tariffId = 1L;

        when(tariffService.getTariff(tariffId)).thenThrow(new TariffNotFoundException());

        mockMvc.perform(get("/scooter-rental/tariffs/{tariffId}", tariffId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("TariffNotFoundException"));

        verify(tariffService, times(1)).getTariff(tariffId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllTariffs_ShouldReturn200AndList_WhenAdminRequests() throws Exception {
        TariffResponseDto dto1 = new TariffResponseDto();
        dto1.setTariffId(1L);

        TariffResponseDto dto2 = new TariffResponseDto();
        dto2.setTariffId(2L);

        when(tariffService.getAllTariffs()).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/scooter-rental/tariffs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].tariffId").value(1L))
                .andExpect(jsonPath("$[1].tariffId").value(2L));

        verify(tariffService, times(1)).getAllTariffs();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllTariffs_ShouldReturn200AndList_WhenUserRequests() throws Exception {
        TariffResponseDto dto = new TariffResponseDto();
        dto.setTariffId(1L);

        when(tariffService.getAllTariffs()).thenReturn(List.of(dto));

        mockMvc.perform(get("/scooter-rental/tariffs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].tariffId").value(1L));

        verify(tariffService, times(1)).getAllTariffs();
    }

    @Test
    void getAllTariffs_ShouldReturn401_WhenNonameRequests() throws Exception {
        mockMvc.perform(get("/scooter-rental/tariffs"))
                .andExpect(status().isUnauthorized());

        verify(tariffService, never()).getAllTariffs();
    }
}
