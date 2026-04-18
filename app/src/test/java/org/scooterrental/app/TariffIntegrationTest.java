package org.scooterrental.app;

import org.junit.jupiter.api.Test;
import org.scooterrental.model.entity.Tariff;
import org.scooterrental.model.enums.PaymentType;
import org.scooterrental.repository.daointerface.TariffDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class TariffIntegrationTest extends IntegrationTestBase {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TariffDao tariffDao;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateTariff() throws Exception {
        String body = """
                {
                    "paymentType": "HOURLY",
                    "price": 500,
                    "discount": 0
                }
                """;
        mockMvc.perform(post("/scooter-rental/tariffs/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentType").value("HOURLY"))
                .andExpect(jsonPath("$.price").value(500));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldThrowForbiddenStatusWhenAddTariff() throws Exception {
        String body = """
                {
                    "paymentType": "HOURLY",
                    "price": 500,
                    "discount": 0
                }
                """;
        mockMvc.perform(post("/scooter-rental/tariffs/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldThrowArgumentNotValidExceptionWhenAddTariff() throws Exception {
        String body = "{}";
        mockMvc.perform(post("/scooter-rental/tariffs/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("MethodArgumentNotValidException"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdatePaymentType() throws Exception {
        Tariff tariff = new Tariff();
        tariff.setPaymentType(PaymentType.HOURLY);
        tariff.setPrice(new BigDecimal(500));
        tariffDao.create(tariff);
        Long generatedId = tariff.getTariffId();

        mockMvc.perform(patch("/scooter-rental/tariffs/" + generatedId + "/set-new-payment-type")
                        .param("newPaymentType", "SEASON_TICKET"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentType").value("SEASON_TICKET"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdatePrice() throws Exception {
        Tariff tariff = new Tariff();
        tariff.setPaymentType(PaymentType.HOURLY);
        tariff.setPrice(new BigDecimal(500));
        tariffDao.create(tariff);
        Long generatedId = tariff.getTariffId();

        mockMvc.perform(patch("/scooter-rental/tariffs/" + generatedId + "/set-new-price")
                        .param("newPrice", "700"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(700));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldThrowTariffNotFoundException() throws Exception {
        mockMvc.perform(patch("/scooter-rental/tariffs/" + 9999L + "/set-new-price")
                        .param("newPrice", "700"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("TariffNotFoundException"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateDiscount() throws Exception {
        Tariff tariff = new Tariff();
        tariff.setPaymentType(PaymentType.HOURLY);
        tariff.setPrice(new BigDecimal(500));
        tariff.setDiscount(0);
        tariffDao.create(tariff);
        Long generatedId = tariff.getTariffId();

        mockMvc.perform(patch("/scooter-rental/tariffs/" + generatedId + "/set-new-discount")
                        .param("newDiscount", "15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.discount").value(15));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturnDto() throws Exception {
        Tariff tariff = new Tariff();
        tariff.setPaymentType(PaymentType.HOURLY);
        tariff.setPrice(new BigDecimal(500));
        tariffDao.create(tariff);
        Long generatedId = tariff.getTariffId();

        mockMvc.perform(get("/scooter-rental/tariffs/" + generatedId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(500));
    }

    @Test
    void shouldThrowUnauthorized() throws Exception {
        mockMvc.perform(get("/scooter-rental/tariffs/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturnListDto() throws Exception {
        Tariff tariff = new Tariff();
        tariff.setPaymentType(PaymentType.HOURLY);
        tariff.setPrice(new BigDecimal(500));
        tariffDao.create(tariff);

        mockMvc.perform(get("/scooter-rental/tariffs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].price").value(500));
    }
}
