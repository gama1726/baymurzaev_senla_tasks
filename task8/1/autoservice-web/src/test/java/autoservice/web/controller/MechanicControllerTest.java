package autoservice.web.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import autoservice.model.Mechanic;
import autoservice.model.MechanicSort;
import autoservice.service.MechanicService;
import autoservice.service.OrderService;

@ExtendWith(MockitoExtension.class)
@DisplayName("MechanicController")
class MechanicControllerTest {

    @Mock
    private MechanicService mechanicService;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private MechanicController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .defaultRequest(get("/").accept(MediaType.APPLICATION_JSON))
                .build();
    }

    @Nested
    @DisplayName("POST create")
    class Create {
        @Test
        void positive_createsMechanic() throws Exception {
            when(mechanicService.getAllMechanics()).thenReturn(List.of());
            mockMvc.perform(post("/api/mechanics")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"Ivan\"}"))
                    .andExpect(status().isCreated())
                    .andExpect(content().json("{\"id\":1,\"name\":\"Ivan\"}"));
        }

        @Test
        void negative_emptyBody_usesDefaultName() throws Exception {
            when(mechanicService.getAllMechanics()).thenReturn(List.of());
            mockMvc.perform(post("/api/mechanics").contentType(MediaType.APPLICATION_JSON).content("{}"))
                    .andExpect(status().isCreated())
                    .andExpect(content().json("{\"name\":\"\"}"));
        }
    }

    @Nested
    @DisplayName("GET by id")
    class GetById {
        @Test
        void positive_returnsMechanic() throws Exception {
            Mechanic m = new Mechanic(1, "Ivan");
            when(mechanicService.findMechanicById(1)).thenReturn(Optional.of(m));
            mockMvc.perform(get("/api/mechanics/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().json("{\"id\":1,\"name\":\"Ivan\"}"));
        }

        @Test
        void negative_notFound_returns404() throws Exception {
            when(mechanicService.findMechanicById(999)).thenReturn(Optional.empty());
            mockMvc.perform(get("/api/mechanics/999")).andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET list")
    class ListAll {
        @Test
        void positive_returnsList() throws Exception {
            when(mechanicService.getAllMechanics()).thenReturn(List.of(new Mechanic(1, "Ivan")));
            mockMvc.perform(get("/api/mechanics"))
                    .andExpect(status().isOk())
                    .andExpect(content().json("[{\"id\":1,\"name\":\"Ivan\"}]"));
        }

        @Test
        void positive_withSort_returnsSorted() throws Exception {
            Mechanic m = new Mechanic(1, "Ivan");
            when(mechanicService.getMechanicSorted(MechanicSort.BY_NAME)).thenReturn(List.of(m));
            mockMvc.perform(get("/api/mechanics").param("sort", "BY_NAME"))
                    .andExpect(status().isOk())
                    .andExpect(content().json("[{\"id\":1,\"name\":\"Ivan\"}]"));
        }
    }

    @Nested
    @DisplayName("GET free-count")
    class FreeCount {
        @Test
        void positive_returnsCount() throws Exception {
            when(mechanicService.getFreeMechanicsCount(any())).thenReturn(2);
            mockMvc.perform(get("/api/mechanics/free-count")).andExpect(status().isOk()).andExpect(content().string(containsString("2")));
        }
    }

    @Nested
    @DisplayName("GET by-order")
    class MechanicByOrder {
        @Test
        void positive_returnsMechanic() throws Exception {
            Mechanic m = new Mechanic(1, "Ivan");
            when(orderService.getMechanicByOrderId(1)).thenReturn(Optional.of(m));
            mockMvc.perform(get("/api/mechanics/by-order/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().json("{\"id\":1,\"name\":\"Ivan\"}"));
        }

        @Test
        void negative_notFound_returns404() throws Exception {
            when(orderService.getMechanicByOrderId(999)).thenReturn(Optional.empty());
            mockMvc.perform(get("/api/mechanics/by-order/999")).andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE")
    class Delete {
        @Test
        void positive_returnsNoContent() throws Exception {
            when(mechanicService.removeMechanicById(1)).thenReturn(true);
            mockMvc.perform(delete("/api/mechanics/1")).andExpect(status().isNoContent());
        }

        @Test
        void negative_notFound_returns404() throws Exception {
            when(mechanicService.removeMechanicById(999)).thenReturn(false);
            mockMvc.perform(delete("/api/mechanics/999")).andExpect(status().isNotFound());
        }
    }
}
