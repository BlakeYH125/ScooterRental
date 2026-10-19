package org.scooterrental.app;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.scooterrental.model.entity.RentalPoint;
import org.scooterrental.model.entity.Scooter;
import org.scooterrental.model.enums.RentalPointType;
import org.scooterrental.model.enums.ScooterStatus;
import org.scooterrental.repository.daointerface.RentalPointDao;
import org.scooterrental.repository.daointerface.ScooterDao;
import org.scooterrental.service.serviceimpl.simulation.BatterySimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

public class ScooterIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ScooterDao scooterDao;

    @Autowired
    private RentalPointDao rentalPointDao;

    @Autowired
    private SessionFactory sessionFactory;

    @MockBean
    private BatterySimulationService batterySimulationService;


    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateScooter() throws Exception {
        String body = """
                {
                    "model": "Ninebot Max"
                }
                """;
        mockMvc.perform(post("/scooter-rental/scooters/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model").value("Ninebot Max"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldThrowForbiddenStatusWhenAddScooter() throws Exception {
        String body = """
                {
                    "model": "Ninebot"
                }
                """;
        mockMvc.perform(post("/scooter-rental/scooters/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldThrowArgumentNotValidExceptionWhenAddScooter() throws Exception {
        String body = "{}";
        mockMvc.perform(post("/scooter-rental/scooters/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("MethodArgumentNotValidException"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateScooterModel() throws Exception {
        String newModel = "Xiaomi Pro 2";

        Scooter scooter = new Scooter();
        scooter.setModel("Old Model");
        scooterDao.create(scooter);
        Long generatedId = scooter.getScooterId();

        mockMvc.perform(patch("/scooter-rental/scooters/" + generatedId + "/set-new-model")
                        .param("newScooterModel", newModel))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model").value(newModel));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldThrowScooterNotFoundException() throws Exception {
        mockMvc.perform(patch("/scooter-rental/scooters/" + 9999L + "/set-new-model")
                        .param("newScooterModel", "New Model"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("ScooterNotFoundException"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldPutScooterInUse() throws Exception {
        RentalPoint rentalPoint = new RentalPoint();
        rentalPoint.setLocation("Парк Горького");
        rentalPoint.setRentalPointType(RentalPointType.BUILDING);
        rentalPointDao.create(rentalPoint);
        Long pointId = rentalPoint.getRentalPointId();

        Scooter scooter = new Scooter();
        scooter.setModel("Ninebot");
        scooter.setScooterStatus(ScooterStatus.IN_WAREHOUSE);
        scooterDao.create(scooter);
        Long scooterId = scooter.getScooterId();

        mockMvc.perform(patch("/scooter-rental/scooters/" + scooterId + "/put-in-use")
                        .param("rentalPointId", pointId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scooterStatus").value("AVAILABLE"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldPutScooterInWarehouse() throws Exception {
        Scooter scooter = new Scooter();
        scooter.setModel("Ninebot");
        scooter.setScooterStatus(ScooterStatus.AVAILABLE);
        scooterDao.create(scooter);
        Long generatedId = scooter.getScooterId();

        mockMvc.perform(patch("/scooter-rental/scooters/" + generatedId + "/put-in-warehouse"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scooterStatus").value("IN_WAREHOUSE"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldSetNewBatteryLevel() throws Exception {
        Scooter scooter = new Scooter();
        scooter.setModel("Ninebot");
        scooter.setBatteryLevel(50);
        scooterDao.create(scooter);
        Long generatedId = scooter.getScooterId();

        mockMvc.perform(patch("/scooter-rental/scooters/" + generatedId + "/set-new-battery-level")
                        .param("newBatteryLevel", "80"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.batteryLevel").value(80));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldRechargeBattery() throws Exception {
        Scooter scooter = new Scooter();
        scooter.setModel("Ninebot");
        scooter.setBatteryLevel(15);
        scooterDao.create(scooter);
        Long generatedId = scooter.getScooterId();

        mockMvc.perform(patch("/scooter-rental/scooters/" + generatedId + "/recharge-battery"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.batteryLevel").value(100));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldSetNewScooterStatus() throws Exception {
        Scooter scooter = new Scooter();
        scooter.setModel("Ninebot");
        scooter.setScooterStatus(ScooterStatus.AVAILABLE);
        scooterDao.create(scooter);
        Long generatedId = scooter.getScooterId();

        mockMvc.perform(patch("/scooter-rental/scooters/" + generatedId + "/set-new-scooter-status")
                        .param("newScooterStatus", ScooterStatus.IN_SERVICE.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scooterStatus").value("IN_SERVICE"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteScooter() throws Exception {
        Scooter scooter = new Scooter();
        scooter.setModel("Ninebot");
        scooterDao.create(scooter);
        Long generatedId = scooter.getScooterId();

        mockMvc.perform(delete("/scooter-rental/scooters/" + generatedId + "/delete"))
                .andExpect(status().isOk())
                .andExpect(content().string("Удаление самоката успешно"));

        assertTrue(scooterDao.findScooter(generatedId).isDeleted());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturnDto() throws Exception {
        Scooter scooter = new Scooter();
        scooter.setModel("Ninebot Max");
        scooterDao.create(scooter);
        Long generatedId = scooter.getScooterId();

        mockMvc.perform(get("/scooter-rental/scooters/" + generatedId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model").value("Ninebot Max"));
    }

    @Test
    void shouldThrowUnauthorized() throws Exception {
        mockMvc.perform(get("/scooter-rental/scooters/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturnListDto() throws Exception {
        Scooter scooter = new Scooter();
        scooter.setModel("Ninebot Max");
        scooterDao.create(scooter);

        mockMvc.perform(get("/scooter-rental/scooters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].model").value("Ninebot Max"));
    }

    @Test
    void shouldRejectScooterWithOldVersion() {
        Scooter scooter = new Scooter();
        scooter.setModel("Ninebot");
        scooter.setBatteryLevel(100);
        scooterDao.create(scooter);

        Session session = sessionFactory.getCurrentSession();
        session.flush();
        long oldVersion = scooter.getVersion();
        session.evict(scooter);

        Scooter currentScooter = scooterDao.findScooter(scooter.getScooterId());
        currentScooter.setBatteryLevel(90);
        scooterDao.update(currentScooter);
        session.flush();

        assertEquals(oldVersion + 1, currentScooter.getVersion());

        scooter.setBatteryLevel(80);
        assertThrows(OptimisticLockingFailureException.class, () -> scooterDao.update(scooter));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturnScooterListWithOneSql() throws Exception {
        for (int i = 0; i < 3; i++) {
            RentalPoint point = new RentalPoint();
            point.setLocation("Здание " + i);
            point.setRentalPointType(RentalPointType.BUILDING);
            rentalPointDao.create(point);

            Scooter scooter = new Scooter();
            scooter.setModel("Ninebot " + i);
            scooter.setRentalPoint(point);
            scooterDao.create(scooter);
        }

        Session session = sessionFactory.getCurrentSession();
        session.flush();
        session.clear();

        Statistics statistics = sessionFactory.getStatistics();
        boolean wasEnabled = statistics.isStatisticsEnabled();
        statistics.setStatisticsEnabled(true);
        statistics.clear();

        try {
            mockMvc.perform(get("/scooter-rental/scooters"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(3))
                    .andExpect(jsonPath("$[0].rentalPointId").exists());

            assertEquals(1, statistics.getPrepareStatementCount());
        } finally {
            statistics.setStatisticsEnabled(wasEnabled);
        }
    }

}