package autoservice.web.controller;

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

import autoservice.model.GarageSlot;
import autoservice.service.GarageSlotService;

@ExtendWith(MockitoExtension.class)
@DisplayName("GarageSlotController")
class GarageSlotControllerTest {

    @Mock
    private GarageSlotService garageSlotService;

    @InjectMocks
    private GarageSlotController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .defaultRequest(get("/").accept(MediaType.APPLICATION_JSON))
                .build();
    }

    @Nested
    @DisplayName("POST /api/garage-slots (create)")
    class Create {
        @Test
        @DisplayName("returns 201 and body when created")
        void positive_createsSlot() throws Exception {
            mockMvc.perform(post("/api/garage-slots")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"id\":1}"))
                    .andExpect(status().isCreated())
                    .andExpect(content().json("{\"id\":1,\"occupied\":false}"));
        }
    }

    @Nested
    @DisplayName("GET /api/garage-slots/{id}")
    class GetById {
        @Test
        @DisplayName("returns 200 and slot when found")
        void positive_returnsSlot() throws Exception {
            GarageSlot slot = new GarageSlot(1);
            when(garageSlotService.findGarageSlotById(1)).thenReturn(Optional.of(slot));
            mockMvc.perform(get("/api/garage-slots/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().json("{\"id\":1,\"occupied\":false}"));
        }

        @Test
        @DisplayName("returns 404 when not found")
        void negative_notFound_returns404() throws Exception {
            when(garageSlotService.findGarageSlotById(999)).thenReturn(Optional.empty());
            mockMvc.perform(get("/api/garage-slots/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/garage-slots (list)")
    class ListAll {
        @Test
        @DisplayName("returns 200 and list")
        void positive_returnsList() throws Exception {
            GarageSlot slot = new GarageSlot(1);
            when(garageSlotService.getGarageSlots()).thenReturn(List.of(slot));
            mockMvc.perform(get("/api/garage-slots"))
                    .andExpect(status().isOk())
                    .andExpect(content().json("[{\"id\":1,\"occupied\":false}]"));
        }
    }

    @Nested
    @DisplayName("GET /api/garage-slots/free")
    class Free {
        @Test
        @DisplayName("returns 200 and free slots list")
        void positive_returnsFreeSlots() throws Exception {
            GarageSlot slot = new GarageSlot(1);
            when(garageSlotService.getFreeGarageSlotsAt(any())).thenReturn(List.of(slot));
            mockMvc.perform(get("/api/garage-slots/free"))
                    .andExpect(status().isOk())
                    .andExpect(content().json("[{\"id\":1,\"occupied\":false}]"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/garage-slots/{id}")
    class Delete {
        @Test
        @DisplayName("returns 204 when deleted")
        void positive_returnsNoContent() throws Exception {
            when(garageSlotService.removeGarageSlotById(1)).thenReturn(true);
            mockMvc.perform(delete("/api/garage-slots/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("returns 404 when not found")
        void negative_notFound_returns404() throws Exception {
            when(garageSlotService.removeGarageSlotById(999)).thenReturn(false);
            mockMvc.perform(delete("/api/garage-slots/999"))
                    .andExpect(status().isNotFound());
        }
    }
}
