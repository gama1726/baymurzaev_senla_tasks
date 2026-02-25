package autoservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
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

import autoservice.dao.GarageSlotDAO;
import autoservice.model.GarageSlot;

@ExtendWith(MockitoExtension.class)
@DisplayName("GarageSlotService")
class GarageSlotServiceTest {

    @Mock
    private GarageSlotDAO garageSlotDAO;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private GarageSlotService garageSlotService;

    private GarageSlot slot;

    @BeforeEach
    void setUp() {
        slot = new GarageSlot(1);
    }

    @Nested
    @DisplayName("addGarageSlot")
    class AddGarageSlot {
        @Test
        @DisplayName("успешно добавляет место (позитивный)")
        void positive_addsSlot() {
            garageSlotService.addGarageSlot(slot);
            verify(garageSlotDAO).save(slot);
        }

        @Test
        @DisplayName("при ошибке DAO выбрасывает RuntimeException (негативный)")
        void negative_daoThrows_rethrowsRuntimeException() {
            doThrow(new RuntimeException("DB error")).when(garageSlotDAO).save(any(GarageSlot.class));
            assertThrows(RuntimeException.class, () -> garageSlotService.addGarageSlot(slot));
        }
    }

    @Nested
    @DisplayName("removeGarageSlotById")
    class RemoveGarageSlotById {
        @Test
        @DisplayName("возвращает true при успешном удалении (позитивный)")
        void positive_returnsTrueWhenDeleted() {
            when(garageSlotDAO.deleteById(1)).thenReturn(true);
            assertTrue(garageSlotService.removeGarageSlotById(1));
            verify(garageSlotDAO).deleteById(1);
        }

        @Test
        @DisplayName("возвращает false если место не найдено (негативный)")
        void negative_returnsFalseWhenNotFound() {
            when(garageSlotDAO.deleteById(999)).thenReturn(false);
            assertTrue(!garageSlotService.removeGarageSlotById(999));
        }

        @Test
        @DisplayName("при ошибке DAO выбрасывает RuntimeException (негативный)")
        void negative_daoThrows_rethrowsRuntimeException() {
            doThrow(new RuntimeException("DB error")).when(garageSlotDAO).deleteById(eq(1));
            assertThrows(RuntimeException.class, () -> garageSlotService.removeGarageSlotById(1));
        }
    }

    @Nested
    @DisplayName("findGarageSlotById")
    class FindGarageSlotById {
        @Test
        @DisplayName("возвращает Optional с местом (позитивный)")
        void positive_returnsSlot() {
            when(garageSlotDAO.findById(1)).thenReturn(Optional.of(slot));
            assertEquals(Optional.of(slot), garageSlotService.findGarageSlotById(1));
        }

        @Test
        @DisplayName("возвращает empty если не найдено (негативный)")
        void negative_returnsEmptyWhenNotFound() {
            when(garageSlotDAO.findById(999)).thenReturn(Optional.empty());
            assertTrue(garageSlotService.findGarageSlotById(999).isEmpty());
        }

        @Test
        @DisplayName("при ошибке DAO выбрасывает RuntimeException (негативный)")
        void negative_daoThrows_rethrowsRuntimeException() {
            doThrow(new RuntimeException("DB error")).when(garageSlotDAO).findById(1);
            assertThrows(RuntimeException.class, () -> garageSlotService.findGarageSlotById(1));
        }
    }

    @Nested
    @DisplayName("getGarageSlots")
    class GetGarageSlots {
        @Test
        @DisplayName("возвращает список мест (позитивный)")
        void positive_returnsList() {
            List<GarageSlot> list = List.of(slot);
            when(garageSlotDAO.findAll()).thenReturn(list);
            assertEquals(list, garageSlotService.getGarageSlots());
        }

        @Test
        @DisplayName("при ошибке DAO выбрасывает RuntimeException (негативный)")
        void negative_daoThrows_rethrowsRuntimeException() {
            doThrow(new RuntimeException("DB error")).when(garageSlotDAO).findAll();
            assertThrows(RuntimeException.class, () -> garageSlotService.getGarageSlots());
        }
    }

    @Nested
    @DisplayName("getFreeGarageSlotsAt")
    class GetFreeGarageSlotsAt {
        @Test
        @DisplayName("возвращает свободные места на момент времени (позитивный)")
        void positive_returnsFreeSlots() {
            when(garageSlotDAO.findAll()).thenReturn(List.of(slot));
            when(orderService.getOrders()).thenReturn(Collections.emptyList());
            List<GarageSlot> result = garageSlotService.getFreeGarageSlotsAt(LocalDateTime.now());
            assertEquals(1, result.size());
            assertEquals(slot, result.get(0));
        }

        @Test
        @DisplayName("при ошибке выбрасывает RuntimeException (негативный)")
        void negative_daoThrows_rethrowsRuntimeException() {
            doThrow(new RuntimeException("DB error")).when(garageSlotDAO).findAll();
            assertThrows(RuntimeException.class,
                () -> garageSlotService.getFreeGarageSlotsAt(LocalDateTime.now()));
        }
    }

    @Nested
    @DisplayName("getFreeGarageSlotsCount")
    class GetFreeGarageSlotsCount {
        @Test
        @DisplayName("возвращает количество свободных мест (позитивный)")
        void positive_returnsCount() {
            when(garageSlotDAO.findAll()).thenReturn(List.of(slot));
            when(orderService.getOrders()).thenReturn(Collections.emptyList());
            assertEquals(1, garageSlotService.getFreeGarageSlotsCount(LocalDateTime.now()));
        }
    }

    @Nested
    @DisplayName("updateGarageSlot")
    class UpdateGarageSlot {
        @Test
        @DisplayName("успешно обновляет место (позитивный)")
        void positive_updatesSlot() {
            garageSlotService.updateGarageSlot(slot);
            verify(garageSlotDAO).update(slot);
        }

        @Test
        @DisplayName("при ошибке DAO выбрасывает RuntimeException (негативный)")
        void negative_daoThrows_rethrowsRuntimeException() {
            doThrow(new RuntimeException("DB error")).when(garageSlotDAO).update(any(GarageSlot.class));
            assertThrows(RuntimeException.class, () -> garageSlotService.updateGarageSlot(slot));
        }
    }
}
