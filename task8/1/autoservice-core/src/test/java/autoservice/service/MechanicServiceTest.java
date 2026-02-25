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

import autoservice.dao.MechanicDAO;
import autoservice.model.Mechanic;
import autoservice.model.MechanicSort;

@ExtendWith(MockitoExtension.class)
@DisplayName("MechanicService")
class MechanicServiceTest {

    @Mock
    private MechanicDAO mechanicDAO;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private MechanicService mechanicService;

    private Mechanic mechanic;

    @BeforeEach
    void setUp() {
        mechanic = new Mechanic(1, "Ivan");
    }

    @Nested
    @DisplayName("addMechanic")
    class AddMechanic {
        @Test
        @DisplayName("adds mechanic successfully")
        void positive_addsMechanic() {
            mechanicService.addMechanic(mechanic);
            verify(mechanicDAO).save(mechanic);
        }

        @Test
        @DisplayName("throws RuntimeException when DAO fails")
        void negative_daoThrows_rethrowsRuntimeException() {
            doThrow(new RuntimeException("DB error")).when(mechanicDAO).save(any(Mechanic.class));
            RuntimeException ex = assertThrows(RuntimeException.class, () -> mechanicService.addMechanic(mechanic));
            assertTrue(ex.getMessage().contains("Ошибка при добавлении механика"));
        }
    }

    @Nested
    @DisplayName("removeMechanicById")
    class RemoveMechanicById {
        @Test
        @DisplayName("returns true when deleted")
        void positive_returnsTrueWhenDeleted() {
            when(mechanicDAO.deleteById(1)).thenReturn(true);
            assertTrue(mechanicService.removeMechanicById(1));
            verify(mechanicDAO).deleteById(1);
        }

        @Test
        @DisplayName("returns false when not found")
        void negative_returnsFalseWhenNotFound() {
            when(mechanicDAO.deleteById(999)).thenReturn(false);
            assertTrue(!mechanicService.removeMechanicById(999));
        }

        @Test
        @DisplayName("throws RuntimeException when DAO fails")
        void negative_daoThrows_rethrowsRuntimeException() {
            doThrow(new RuntimeException("DB error")).when(mechanicDAO).deleteById(eq(1));
            assertThrows(RuntimeException.class, () -> mechanicService.removeMechanicById(1));
        }
    }

    @Nested
    @DisplayName("findMechanicById")
    class FindMechanicById {
        @Test
        @DisplayName("returns Optional with mechanic")
        void positive_returnsMechanic() {
            when(mechanicDAO.findById(1)).thenReturn(Optional.of(mechanic));
            assertEquals(Optional.of(mechanic), mechanicService.findMechanicById(1));
        }

        @Test
        @DisplayName("returns empty when not found")
        void negative_returnsEmptyWhenNotFound() {
            when(mechanicDAO.findById(999)).thenReturn(Optional.empty());
            assertTrue(mechanicService.findMechanicById(999).isEmpty());
        }

        @Test
        @DisplayName("throws RuntimeException when DAO fails")
        void negative_daoThrows_rethrowsRuntimeException() {
            doThrow(new RuntimeException("DB error")).when(mechanicDAO).findById(1);
            assertThrows(RuntimeException.class, () -> mechanicService.findMechanicById(1));
        }
    }

    @Nested
    @DisplayName("getAllMechanics")
    class GetAllMechanics {
        @Test
        @DisplayName("returns list of mechanics")
        void positive_returnsList() {
            List<Mechanic> list = List.of(mechanic);
            when(mechanicDAO.findAll()).thenReturn(list);
            assertEquals(list, mechanicService.getAllMechanics());
        }

        @Test
        @DisplayName("throws RuntimeException when DAO fails")
        void negative_daoThrows_rethrowsRuntimeException() {
            doThrow(new RuntimeException("DB error")).when(mechanicDAO).findAll();
            assertThrows(RuntimeException.class, () -> mechanicService.getAllMechanics());
        }
    }

    @Nested
    @DisplayName("getMechanicSorted")
    class GetMechanicSorted {
        @Test
        @DisplayName("returns sorted list BY_NAME")
        void positive_byName_returnsSorted() {
            Mechanic a = new Mechanic(1, "Alex");
            Mechanic b = new Mechanic(2, "Boris");
            when(mechanicDAO.findAll()).thenReturn(List.of(b, a));
            List<Mechanic> result = mechanicService.getMechanicSorted(MechanicSort.BY_NAME);
            assertEquals(List.of(a, b), result);
        }

        @Test
        @DisplayName("throws RuntimeException when DAO fails")
        void negative_daoThrows_rethrowsRuntimeException() {
            doThrow(new RuntimeException("DB error")).when(mechanicDAO).findAll();
            assertThrows(RuntimeException.class,
                () -> mechanicService.getMechanicSorted(MechanicSort.BY_NAME));
        }
    }

    @Nested
    @DisplayName("getFreeMechanicsCount")
    class GetFreeMechanicsCount {
        @Test
        @DisplayName("returns count of free mechanics")
        void positive_returnsCount() {
            when(mechanicDAO.findAll()).thenReturn(List.of(mechanic));
            when(orderService.getOrders()).thenReturn(Collections.emptyList());
            assertEquals(1, mechanicService.getFreeMechanicsCount(LocalDateTime.now()));
        }

        @Test
        @DisplayName("throws RuntimeException when DAO fails")
        void negative_daoThrows_rethrowsRuntimeException() {
            doThrow(new RuntimeException("DB error")).when(mechanicDAO).findAll();
            assertThrows(RuntimeException.class,
                () -> mechanicService.getFreeMechanicsCount(LocalDateTime.now()));
        }
    }
}
