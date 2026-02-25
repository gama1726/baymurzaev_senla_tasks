package autoservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CapacityService")
class CapacityServiceTest {

    @Mock
    private MechanicService mechanicService;

    @Mock
    private GarageSlotService garageSlotService;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private CapacityService capacityService;

    private LocalDateTime when;

    @BeforeEach
    void setUp() {
        when = LocalDateTime.now();
    }

    @Nested
    @DisplayName("freeCapacityAt")
    class FreeCapacityAt {
        @Test
        @DisplayName("returns minimum of free mechanics and free garage slots")
        void positive_returnsMin() {
            when(mechanicService.getFreeMechanicsCount(any(LocalDateTime.class))).thenReturn(3);
            when(garageSlotService.getFreeGarageSlotsCount(any(LocalDateTime.class))).thenReturn(5);
            assertEquals(3, capacityService.freeCapacityAt(when));
        }

        @Test
        @DisplayName("returns zero when no free capacity")
        void positive_returnsZeroWhenNone() {
            when(mechanicService.getFreeMechanicsCount(any(LocalDateTime.class))).thenReturn(0);
            when(garageSlotService.getFreeGarageSlotsCount(any(LocalDateTime.class))).thenReturn(2);
            assertEquals(0, capacityService.freeCapacityAt(when));
        }

        @Test
        @DisplayName("throws RuntimeException when mechanicService fails")
        void negative_mechanicServiceThrows_rethrowsRuntimeException() {
            doThrow(new RuntimeException("error")).when(mechanicService).getFreeMechanicsCount(any(LocalDateTime.class));
            assertThrows(RuntimeException.class, () -> capacityService.freeCapacityAt(when));
        }
    }

    @Nested
    @DisplayName("findNextFreeDate")
    class FindNextFreeDate {
        @Test
        @DisplayName("returns first date with free capacity")
        void positive_returnsDateWhenCapacityAvailable() {
            when(mechanicService.getFreeMechanicsCount(any(LocalDateTime.class))).thenReturn(1);
            when(garageSlotService.getFreeGarageSlotsCount(any(LocalDateTime.class))).thenReturn(1);
            LocalDateTime result = capacityService.findNextFreeDate(when);
            assertEquals(when, result);
        }

        @Test
        @DisplayName("returns null when no free slot within 30 days")
        void negative_returnsNullWhenNoCapacity() {
            when(mechanicService.getFreeMechanicsCount(any(LocalDateTime.class))).thenReturn(0);
            when(garageSlotService.getFreeGarageSlotsCount(any(LocalDateTime.class))).thenReturn(0);
            LocalDateTime result = capacityService.findNextFreeDate(when);
            assertNull(result);
        }

        @Test
        @DisplayName("throws RuntimeException when freeCapacityAt fails")
        void negative_freeCapacityAtThrows_rethrowsRuntimeException() {
            doThrow(new RuntimeException("error")).when(mechanicService).getFreeMechanicsCount(any(LocalDateTime.class));
            assertThrows(RuntimeException.class, () -> capacityService.findNextFreeDate(when));
        }
    }
}
