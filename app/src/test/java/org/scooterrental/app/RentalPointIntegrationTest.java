package org.scooterrental.app;

import org.junit.jupiter.api.Test;
import org.scooterrental.model.entity.RentalPoint;
import org.scooterrental.repository.daointerface.RentalPointDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

public class RentalPointIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RentalPointDao rentalPointDao;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateRentalPoint() throws Exception {
        String body = """
                {
                    "location": "Москва"
                }
                """;
        mockMvc.perform(post("/scooter-rental/rental-points/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location").value("Москва"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldThrowForbiddenStatusWhenAdd() throws Exception {
        String body = """
                {
                    "location": "Москва"
                }
                """;
        mockMvc.perform(post("/scooter-rental/rental-points/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldThrowArgumentNotValidExceptionWhenAdd() throws Exception {
        String body = """
                {
                }
                """;
        mockMvc.perform(post("/scooter-rental/rental-points/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("MethodArgumentNotValidException"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateLocation() throws Exception {
        String newLocation = "Химки";

        RentalPoint rentalPoint = new RentalPoint();
        rentalPoint.setLocation("Москва");
        rentalPointDao.create(rentalPoint);
        Long generatedId = rentalPoint.getRentalPointId();

        mockMvc.perform(patch("/scooter-rental/rental-points/" + generatedId + "/set-new-location")
                        .param("newLocation", newLocation))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location").value(newLocation));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldThrowRentalPointNotFoundException() throws Exception {
        String newLocation = "Химки";

        RentalPoint rentalPoint = new RentalPoint();
        rentalPoint.setLocation("Москва");
        rentalPointDao.create(rentalPoint);

        mockMvc.perform(patch("/scooter-rental/rental-points/" + 999L + "/set-new-location")
                        .param("newLocation", newLocation))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("RentalPointNotFoundException"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateParentPointId() throws Exception {
        RentalPoint rentalPoint = new RentalPoint();
        rentalPoint.setLocation("Москва");
        rentalPointDao.create(rentalPoint);
        Long generatedId = rentalPoint.getRentalPointId();

        RentalPoint newParentPoint = new RentalPoint();
        newParentPoint.setLocation("Химки");
        rentalPointDao.create(newParentPoint);
        Long generatedIdParent = newParentPoint.getRentalPointId();

        mockMvc.perform(patch("/scooter-rental/rental-points/" + generatedId + "/set-new-parent-point-id")
                        .param("newParentPointId", generatedIdParent.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.parentPointId").value(generatedIdParent));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteRentalPoint() throws Exception {
        RentalPoint rentalPoint = new RentalPoint();
        rentalPoint.setLocation("Москва");
        rentalPointDao.create(rentalPoint);

        Long generatedId = rentalPoint.getRentalPointId();

        mockMvc.perform(delete("/scooter-rental/rental-points/" + generatedId + "/delete"))
                .andExpect(status().isOk())
                .andExpect(content().string("Удаление точки аренды успешно"));

        assertTrue(rentalPointDao.findRentalPointById(generatedId).isDeleted());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnDto() throws Exception {
        RentalPoint rentalPoint = new RentalPoint();
        rentalPoint.setLocation("Москва");
        rentalPointDao.create(rentalPoint);
        Long generatedId = rentalPoint.getRentalPointId();

        mockMvc.perform(get("/scooter-rental/rental-points/" + generatedId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location").value("Москва"));
    }

    @Test
    void shouldThrowUnauthorized() throws Exception {
        RentalPoint rentalPoint = new RentalPoint();
        rentalPoint.setLocation("Москва");
        rentalPointDao.create(rentalPoint);
        Long generatedId = rentalPoint.getRentalPointId();

        mockMvc.perform(get("/scooter-rental/rental-points/" + generatedId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnListDto() throws Exception {
        RentalPoint rentalPoint = new RentalPoint();
        rentalPoint.setLocation("Москва");
        rentalPointDao.create(rentalPoint);

        mockMvc.perform(get("/scooter-rental/rental-points"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].location").value("Москва"));
    }
}
