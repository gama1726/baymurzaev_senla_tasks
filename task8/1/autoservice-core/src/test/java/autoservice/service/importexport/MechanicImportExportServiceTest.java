package autoservice.service.importexport;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Files;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import autoservice.exception.ImportExportException;
import autoservice.model.Mechanic;
import autoservice.service.MechanicService;

@ExtendWith(MockitoExtension.class)
@DisplayName("MechanicImportExportService")
class MechanicImportExportServiceTest {

    @Mock
    private MechanicService mechanicService;

    @InjectMocks
    private MechanicImportExportService service;

    @Nested
    @DisplayName("exportToCsv")
    class ExportToCsv {
        @Test
        @DisplayName("writes CSV file successfully")
        void positive_exportsToFile() throws Exception {
            when(mechanicService.getAllMechanics()).thenReturn(List.of(new Mechanic(1, "Ivan")));
            var path = Files.createTempFile("mechanics", ".csv");
            try {
                service.exportToCsv(path.toString());
                verify(mechanicService).getAllMechanics();
                assertTrue(Files.readAllLines(path).size() >= 1);
            } finally {
                Files.deleteIfExists(path);
            }
        }

        @Test
        @DisplayName("throws when service fails")
        void negative_serviceThrows_throwsRuntimeException() {
            doThrow(new RuntimeException("DB error")).when(mechanicService).getAllMechanics();
            assertThrows(RuntimeException.class, () -> service.exportToCsv("/tmp/out.csv"));
        }
    }

    @Nested
    @DisplayName("importFromCsv")
    class ImportFromCsv {
        @Test
        @DisplayName("throws ImportExportException when file not found")
        void negative_fileNotFound_throwsImportExportException() {
            assertThrows(ImportExportException.class, () -> service.importFromCsv("/nonexistent/path.csv"));
        }
    }
}
