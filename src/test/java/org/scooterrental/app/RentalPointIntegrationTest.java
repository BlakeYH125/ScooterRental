package org.scooterrental.app;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.scooterrental.model.entity.RentalPoint;
import org.scooterrental.model.enums.RentalPointType;
import org.scooterrental.repository.daointerface.RentalPointDao;
import org.scooterrental.service.serviceimpl.simulation.BatterySimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Autowired
    private SessionFactory sessionFactory;

    @MockBean
    private BatterySimulationService batterySimulationService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateRentalPoint() throws Exception {
        String body = """
                {
                    "location": "Москва",
                    "rentalPointType": "CITY"
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
                    "location": "Москва",
                    "rentalPointType": "CITY"
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
        rentalPoint.setRentalPointType(RentalPointType.CITY);
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
        rentalPoint.setRentalPointType(RentalPointType.CITY);
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
        rentalPoint.setLocation("ЦАО");
        rentalPoint.setRentalPointType(RentalPointType.DISTRICT);
        rentalPointDao.create(rentalPoint);
        Long generatedId = rentalPoint.getRentalPointId();

        RentalPoint newParentPoint = new RentalPoint();
        newParentPoint.setLocation("Москва");
        newParentPoint.setRentalPointType(RentalPointType.CITY);
        rentalPointDao.create(newParentPoint);
        Long generatedIdParent = newParentPoint.getRentalPointId();

        mockMvc.perform(patch("/scooter-rental/rental-points/" + generatedId + "/set-new-parent-point-id")
                        .param("newParentPointId", generatedIdParent.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.parentPointId").value(generatedIdParent));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldThrowInvalidHierarchyException() throws Exception {
        RentalPoint rentalPoint = new RentalPoint();
        rentalPoint.setLocation("Химки");
        rentalPoint.setRentalPointType(RentalPointType.CITY);
        rentalPointDao.create(rentalPoint);
        Long generatedId = rentalPoint.getRentalPointId();

        RentalPoint newParentPoint = new RentalPoint();
        newParentPoint.setLocation("Москва");
        newParentPoint.setRentalPointType(RentalPointType.CITY);
        rentalPointDao.create(newParentPoint);
        Long generatedIdParent = newParentPoint.getRentalPointId();

        mockMvc.perform(patch("/scooter-rental/rental-points/" + generatedId + "/set-new-parent-point-id")
                        .param("newParentPointId", generatedIdParent.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("InvalidHierarchyException"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteRentalPoint() throws Exception {
        RentalPoint rentalPoint = new RentalPoint();
        rentalPoint.setLocation("Москва");
        rentalPoint.setRentalPointType(RentalPointType.CITY);
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
        rentalPoint.setRentalPointType(RentalPointType.CITY);
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
        rentalPoint.setRentalPointType(RentalPointType.CITY);
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
        rentalPoint.setRentalPointType(RentalPointType.CITY);
        rentalPointDao.create(rentalPoint);

        mockMvc.perform(get("/scooter-rental/rental-points"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].location").value("Москва"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturnRentalStationsByParentId() throws Exception {
        RentalPoint city = new RentalPoint();
        city.setLocation("Москва");
        city.setRentalPointType(RentalPointType.CITY);
        rentalPointDao.create(city);

        RentalPoint district = new RentalPoint();
        district.setLocation("ЦАО");
        district.setRentalPointType(RentalPointType.DISTRICT);
        district.setParentPoint(city);
        rentalPointDao.create(district);
        city.getChildPoints().add(district);

        RentalPoint street = new RentalPoint();
        street.setLocation("ул. Пушкина");
        street.setRentalPointType(RentalPointType.STREET);
        street.setParentPoint(district);
        rentalPointDao.create(street);
        district.getChildPoints().add(street);

        RentalPoint building = new RentalPoint();
        building.setLocation("д. 10");
        building.setRentalPointType(RentalPointType.BUILDING);
        building.setParentPoint(street);
        rentalPointDao.create(building);
        street.getChildPoints().add(building);

        mockMvc.perform(get("/scooter-rental/rental-points/" + city.getRentalPointId() + "/rental-stations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].location").value("ул. Пушкина, д. 10"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturnRentalStationsInPagesWithoutDeletedBuildings() throws Exception {
        RentalPoint city = new RentalPoint();
        city.setLocation("Москва");
        city.setRentalPointType(RentalPointType.CITY);
        rentalPointDao.create(city);

        RentalPoint district = new RentalPoint();
        district.setLocation("ЦАО");
        district.setRentalPointType(RentalPointType.DISTRICT);
        district.setParentPoint(city);
        rentalPointDao.create(district);

        RentalPoint street = new RentalPoint();
        street.setLocation("ул. Пушкина");
        street.setRentalPointType(RentalPointType.STREET);
        street.setParentPoint(district);
        rentalPointDao.create(street);

        List<Long> buildingIds = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            RentalPoint building = new RentalPoint();
            building.setLocation(String.valueOf(i));
            building.setRentalPointType(RentalPointType.BUILDING);
            building.setParentPoint(street);
            building.setDeleted(i == 4);
            rentalPointDao.create(building);
            buildingIds.add(building.getRentalPointId());
        }

        Session session = sessionFactory.getCurrentSession();
        session.flush();
        session.clear();

        Statistics statistics = sessionFactory.getStatistics();
        boolean wasEnabled = statistics.isStatisticsEnabled();
        statistics.setStatisticsEnabled(true);
        statistics.clear();

        try {
            mockMvc.perform(get("/scooter-rental/rental-points/{id}/rental-stations", city.getRentalPointId())
                            .param("page", "0").param("size", "2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].rentalPointId").value(buildingIds.get(0)))
                    .andExpect(jsonPath("$[1].rentalPointId").value(buildingIds.get(1)))
                    .andExpect(jsonPath("$[0].location").value("ул. Пушкина, 1"));

            assertEquals(2, statistics.getPrepareStatementCount());
        } finally {
            statistics.setStatisticsEnabled(wasEnabled);
        }

        mockMvc.perform(get("/scooter-rental/rental-points/{id}/rental-stations", city.getRentalPointId())
                        .param("page", "1").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].rentalPointId").value(buildingIds.get(2)));

        mockMvc.perform(get("/scooter-rental/rental-points/{id}/rental-stations", city.getRentalPointId())
                        .param("page", "2").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturn400WhenPageIsNegative() throws Exception {
        mockMvc.perform(get("/scooter-rental/rental-points/1/rental-stations")
                        .param("page", "-1").param("size", "50"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturn400WhenPageSizeIsZero() throws Exception {
        mockMvc.perform(get("/scooter-rental/rental-points/1/rental-stations")
                        .param("page", "0").param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturn400WhenPageSizeIsTooLarge() throws Exception {
        mockMvc.perform(get("/scooter-rental/rental-points/1/rental-stations")
                        .param("page", "0").param("size", "101"))
                .andExpect(status().isBadRequest());
    }
}
