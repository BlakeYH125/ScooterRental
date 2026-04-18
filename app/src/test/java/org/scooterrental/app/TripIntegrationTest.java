package org.scooterrental.app;

import org.junit.jupiter.api.Test;
import org.scooterrental.model.entity.RentalPoint;
import org.scooterrental.model.entity.Scooter;
import org.scooterrental.model.entity.Tariff;
import org.scooterrental.model.entity.Trip;
import org.scooterrental.model.entity.User;
import org.scooterrental.model.enums.BanReason;
import org.scooterrental.model.enums.PaymentType;
import org.scooterrental.model.enums.Role;
import org.scooterrental.model.enums.ScooterStatus;
import org.scooterrental.model.enums.TripStatus;
import org.scooterrental.repository.daointerface.RentalPointDao;
import org.scooterrental.repository.daointerface.ScooterDao;
import org.scooterrental.repository.daointerface.TariffDao;
import org.scooterrental.repository.daointerface.TripDao;
import org.scooterrental.repository.daointerface.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TripIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TripDao tripDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ScooterDao scooterDao;

    @Autowired
    private TariffDao tariffDao;

    @Autowired
    private RentalPointDao rentalPointDao;

    @Test
    void shouldStartTrip() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setAge(20);
        user.setBalance(new BigDecimal(1000));
        user.setBanReason(BanReason.NONE);
        user.setRole(Role.ROLE_USER);
        userDao.create(user);

        RentalPoint point = new RentalPoint();
        point.setLocation("Москва");
        rentalPointDao.create(point);

        Scooter scooter = new Scooter();
        scooter.setModel("Ninebot");
        scooter.setBatteryLevel(100);
        scooter.setScooterStatus(ScooterStatus.AVAILABLE);
        scooter.setRentalPoint(point);
        scooterDao.create(scooter);

        Tariff tariff = new Tariff();
        tariff.setPaymentType(PaymentType.HOURLY);
        tariff.setPrice(new BigDecimal(500));
        tariff.setDiscount(0);
        tariffDao.create(tariff);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        String body = String.format("""
                {
                    "userId": %d,
                    "scooterId": %d,
                    "tariffId": %d
                }
                """, user.getUserId(), scooter.getScooterId(), tariff.getTariffId());

        mockMvc.perform(post("/scooter-rental/trips/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripStatus").value("ACTIVE"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldThrowForbiddenStatusWhenStartTrip() throws Exception {
        String body = """
                {
                    "userId": 1,
                    "scooterId": 1,
                    "tariffId": 1
                }
                """;
        mockMvc.perform(post("/scooter-rental/trips/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));
    }

    @Test
    void shouldThrowArgumentNotValidExceptionWhenStartTrip() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setAge(20);
        user.setBalance(new BigDecimal(1000));
        user.setBanReason(BanReason.NONE);
        user.setRole(Role.ROLE_USER);
        userDao.create(user);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        String body = "{}";
        mockMvc.perform(post("/scooter-rental/trips/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(authentication(auth)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("MethodArgumentNotValidException"));
    }

    @Test
    void shouldFinishTrip() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setAge(20);
        user.setBalance(new BigDecimal(1000));
        user.setBanReason(BanReason.NONE);
        user.setRole(Role.ROLE_USER);
        userDao.create(user);

        RentalPoint point = new RentalPoint();
        point.setLocation("Москва");
        rentalPointDao.create(point);

        Scooter scooter = new Scooter();
        scooter.setModel("Ninebot");
        scooter.setBatteryLevel(100);
        scooter.setScooterStatus(ScooterStatus.IN_RENT);
        scooterDao.create(scooter);

        Tariff tariff = new Tariff();
        tariff.setPaymentType(PaymentType.HOURLY);
        tariff.setPrice(new BigDecimal(500));
        tariff.setDiscount(0);
        tariffDao.create(tariff);

        Trip trip = new Trip();
        trip.setUser(user);
        trip.setScooter(scooter);
        trip.setStartPoint(point);
        trip.setStartTime(LocalDateTime.now().minusMinutes(30));
        trip.setTariff(tariff);
        trip.setTripStatus(TripStatus.ACTIVE);
        tripDao.create(trip);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        mockMvc.perform(patch("/scooter-rental/trips/" + trip.getTripId() + "/finish")
                        .param("endRentalPointId", point.getRentalPointId().toString())
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripStatus").value("COMPLETED"));
    }

    @Test
    void shouldThrowTripNotFoundException() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setAge(20);
        user.setBalance(new BigDecimal(1000));
        user.setBanReason(BanReason.NONE);
        user.setRole(Role.ROLE_USER);
        userDao.create(user);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        mockMvc.perform(patch("/scooter-rental/trips/" + 9999L + "/finish")
                        .param("endRentalPointId", "1")
                        .with(authentication(auth)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("TripNotFoundException"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldEmergencyFinishTrip() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setAge(20);
        user.setBalance(new BigDecimal(1000));
        user.setBanReason(BanReason.NONE);
        user.setRole(Role.ROLE_USER);
        userDao.create(user);

        RentalPoint point = new RentalPoint();
        point.setLocation("Москва");
        rentalPointDao.create(point);

        Scooter scooter = new Scooter();
        scooter.setModel("Ninebot");
        scooter.setBatteryLevel(100);
        scooter.setScooterStatus(ScooterStatus.IN_RENT);
        scooterDao.create(scooter);

        Tariff tariff = new Tariff();
        tariff.setPaymentType(PaymentType.HOURLY);
        tariff.setPrice(new BigDecimal(500));
        tariff.setDiscount(0);
        tariffDao.create(tariff);

        Trip trip = new Trip();
        trip.setUser(user);
        trip.setScooter(scooter);
        trip.setStartPoint(point);
        trip.setStartTime(LocalDateTime.now().minusMinutes(30));
        trip.setTariff(tariff);
        trip.setTripStatus(TripStatus.ACTIVE);
        tripDao.create(trip);

        mockMvc.perform(patch("/scooter-rental/trips/" + trip.getTripId() + "/emergency-finish"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripStatus").value("COMPLETED"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldThrowForbiddenStatusWhenEmergencyFinishTrip() throws Exception {
        mockMvc.perform(patch("/scooter-rental/trips/" + 1L + "/emergency-finish"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("AccessDeniedException"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnTripDto() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setAge(20);
        user.setBalance(new BigDecimal(1000));
        user.setBanReason(BanReason.NONE);
        user.setRole(Role.ROLE_USER);
        userDao.create(user);

        RentalPoint point = new RentalPoint();
        point.setLocation("Москва");
        rentalPointDao.create(point);

        Scooter scooter = new Scooter();
        scooter.setModel("Ninebot");
        scooter.setBatteryLevel(100);
        scooter.setScooterStatus(ScooterStatus.IN_RENT);
        scooterDao.create(scooter);

        Tariff tariff = new Tariff();
        tariff.setPaymentType(PaymentType.HOURLY);
        tariff.setPrice(new BigDecimal(500));
        tariff.setDiscount(0);
        tariffDao.create(tariff);

        Trip trip = new Trip();
        trip.setUser(user);
        trip.setScooter(scooter);
        trip.setStartPoint(point);
        trip.setStartTime(LocalDateTime.now().minusMinutes(30));
        trip.setTariff(tariff);
        trip.setTripStatus(TripStatus.ACTIVE);
        tripDao.create(trip);

        mockMvc.perform(get("/scooter-rental/trips/" + trip.getTripId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripStatus").value("ACTIVE"));
    }

    @Test
    void shouldThrowUnauthorized() throws Exception {
        mockMvc.perform(get("/scooter-rental/trips/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnTripListDto() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setAge(20);
        user.setBalance(new BigDecimal(1000));
        user.setBanReason(BanReason.NONE);
        user.setRole(Role.ROLE_USER);
        userDao.create(user);

        RentalPoint point = new RentalPoint();
        point.setLocation("Москва");
        rentalPointDao.create(point);

        Scooter scooter = new Scooter();
        scooter.setModel("Ninebot");
        scooter.setBatteryLevel(100);
        scooter.setScooterStatus(ScooterStatus.IN_RENT);
        scooterDao.create(scooter);

        Tariff tariff = new Tariff();
        tariff.setPaymentType(PaymentType.HOURLY);
        tariff.setPrice(new BigDecimal(500));
        tariff.setDiscount(0);
        tariffDao.create(tariff);

        Trip trip = new Trip();
        trip.setUser(user);
        trip.setScooter(scooter);
        trip.setStartPoint(point);
        trip.setStartTime(LocalDateTime.now().minusMinutes(30));
        trip.setTariff(tariff);
        trip.setTripStatus(TripStatus.ACTIVE);
        tripDao.create(trip);

        mockMvc.perform(get("/scooter-rental/trips"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tripStatus").value("ACTIVE"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnUserHistory() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setAge(20);
        user.setBalance(new BigDecimal(1000));
        user.setBanReason(BanReason.NONE);
        user.setRole(Role.ROLE_USER);
        userDao.create(user);

        RentalPoint point = new RentalPoint();
        point.setLocation("Москва");
        rentalPointDao.create(point);

        Scooter scooter = new Scooter();
        scooter.setModel("Ninebot");
        scooter.setBatteryLevel(100);
        scooter.setScooterStatus(ScooterStatus.IN_RENT);
        scooterDao.create(scooter);

        Tariff tariff = new Tariff();
        tariff.setPaymentType(PaymentType.HOURLY);
        tariff.setPrice(new BigDecimal(500));
        tariff.setDiscount(0);
        tariffDao.create(tariff);

        Trip trip = new Trip();
        trip.setUser(user);
        trip.setScooter(scooter);
        trip.setStartPoint(point);
        trip.setStartTime(LocalDateTime.now().minusMinutes(30));
        trip.setTariff(tariff);
        trip.setTripStatus(TripStatus.ACTIVE);
        tripDao.create(trip);

        mockMvc.perform(get("/scooter-rental/trips/user-history/" + user.getUserId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tripStatus").value("ACTIVE"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnScooterHistory() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setAge(20);
        user.setBalance(new BigDecimal(1000));
        user.setBanReason(BanReason.NONE);
        user.setRole(Role.ROLE_USER);
        userDao.create(user);

        RentalPoint point = new RentalPoint();
        point.setLocation("Москва");
        rentalPointDao.create(point);

        Scooter scooter = new Scooter();
        scooter.setModel("Ninebot");
        scooter.setBatteryLevel(100);
        scooter.setScooterStatus(ScooterStatus.IN_RENT);
        scooterDao.create(scooter);

        Tariff tariff = new Tariff();
        tariff.setPaymentType(PaymentType.HOURLY);
        tariff.setPrice(new BigDecimal(500));
        tariff.setDiscount(0);
        tariffDao.create(tariff);

        Trip trip = new Trip();
        trip.setUser(user);
        trip.setScooter(scooter);
        trip.setStartPoint(point);
        trip.setStartTime(LocalDateTime.now().minusMinutes(30));
        trip.setTariff(tariff);
        trip.setTripStatus(TripStatus.ACTIVE);
        tripDao.create(trip);

        mockMvc.perform(get("/scooter-rental/trips/scooter-history/" + scooter.getScooterId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tripStatus").value("ACTIVE"));
    }
}