package autoservice.web.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import autoservice.service.CapacityService;

@ExtendWith(MockitoExtension.class)
class CapacityControllerTest {

    @Mock
    private CapacityService capacityService;

    @InjectMocks
    private CapacityController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void freeAt_returnsCount() throws Exception {
        when(capacityService.freeCapacityAt(any())).thenReturn(3);
        mockMvc.perform(get("/api/capacity/free")).andExpect(status().isOk()).andExpect(content().string(containsString("3")));
    }

    @Test
    void nextFree_returnsOkWhenDateFound() throws Exception {
        when(capacityService.findNextFreeDate(any())).thenReturn(LocalDateTime.now());
        mockMvc.perform(get("/api/capacity/next-free")).andExpect(status().isOk());
    }

    @Test
    void nextFree_returns404WhenNoDate() throws Exception {
        when(capacityService.findNextFreeDate(any())).thenReturn(null);
        mockMvc.perform(get("/api/capacity/next-free")).andExpect(status().isNotFound());
    }
}
