package autoservice.web.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import autoservice.model.GarageSlot;
import autoservice.model.Mechanic;
import autoservice.service.GarageSlotService;
import autoservice.service.MechanicService;
import autoservice.service.OrderService;
import autoservice.service.importexport.GarageSlotImportExportService;
import autoservice.service.importexport.MechanicImportExportService;
import autoservice.service.importexport.OrderImportExportService;

@ExtendWith(MockitoExtension.class)
@DisplayName("ImportExportController")
class ImportExportControllerTest {

    @Mock
    private MechanicService mechanicService;

    @Mock
    private GarageSlotService garageSlotService;

    @Mock
    private OrderService orderService;

    @Mock
    private MechanicImportExportService mechanicImportExportService;

    @Mock
    private GarageSlotImportExportService garageSlotImportExportService;

    @Mock
    private OrderImportExportService orderImportExportService;

    @InjectMocks
    private ImportExportController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    @DisplayName("GET export/mechanics")
    class ExportMechanics {
        @Test
        void positive_returnsList() throws Exception {
            when(mechanicService.getAllMechanics()).thenReturn(List.of(new Mechanic(1, "Ivan")));
            mockMvc.perform(get("/api/export/mechanics")).andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("GET export/garage-slots")
    class ExportGarageSlots {
        @Test
        void positive_returnsList() throws Exception {
            when(garageSlotService.getGarageSlots()).thenReturn(List.of(new GarageSlot(1)));
            mockMvc.perform(get("/api/export/garage-slots")).andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("GET export/orders")
    class ExportOrders {
        @Test
        void positive_returnsList() throws Exception {
            when(orderService.getOrders()).thenReturn(List.of());
            mockMvc.perform(get("/api/export/orders")).andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("GET export/mechanics/csv")
    class ExportMechanicsCsv {
        @Test
        void positive_returns200() throws Exception {
            mockMvc.perform(get("/api/export/mechanics/csv").param("path", "/tmp/out.csv")).andExpect(status().isOk());
        }

        @Test
        void negative_exception_returns400() throws Exception {
            doThrow(new RuntimeException("IO error")).when(mechanicImportExportService).exportToCsv("/tmp/out.csv");
            mockMvc.perform(get("/api/export/mechanics/csv").param("path", "/tmp/out.csv"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET export/garage-slots/csv")
    class ExportGarageSlotsCsv {
        @Test
        void positive_returns200() throws Exception {
            mockMvc.perform(get("/api/export/garage-slots/csv").param("path", "/tmp/out.csv")).andExpect(status().isOk());
        }

        @Test
        void negative_exception_returns400() throws Exception {
            doThrow(new RuntimeException("IO error")).when(garageSlotImportExportService).exportToCsv("/tmp/out.csv");
            mockMvc.perform(get("/api/export/garage-slots/csv").param("path", "/tmp/out.csv"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET export/orders/csv")
    class ExportOrdersCsv {
        @Test
        void positive_returns200() throws Exception {
            mockMvc.perform(get("/api/export/orders/csv").param("path", "/tmp/out.csv")).andExpect(status().isOk());
        }

        @Test
        void negative_exception_returns400() throws Exception {
            doThrow(new RuntimeException("IO error")).when(orderImportExportService).exportToCsv("/tmp/out.csv");
            mockMvc.perform(get("/api/export/orders/csv").param("path", "/tmp/out.csv"))
                    .andExpect(status().isBadRequest());
        }
    }
}
