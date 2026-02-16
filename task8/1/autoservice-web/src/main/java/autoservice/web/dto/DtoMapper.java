package autoservice.web.dto;

import autoservice.model.GarageSlot;
import autoservice.model.Mechanic;
import autoservice.model.ServiceOrder;
import autoservice.model.TimeSlot;

/**
 * Преобразование доменных моделей в DTO.
 */
public final class DtoMapper {

    private DtoMapper() {
    }

    public static MechanicDto toMechanicDto(Mechanic m) {
        if (m == null) {
            return null;
        }
        return new MechanicDto(m.getId(), m.getName());
    }

    public static GarageSlotDto toGarageSlotDto(GarageSlot g) {
        if (g == null) {
            return null;
        }
        return new GarageSlotDto(g.getId(), g.getStatus());
    }

    public static TimeSlotDto toTimeSlotDto(TimeSlot t) {
        if (t == null) {
            return null;
        }
        return new TimeSlotDto(t.getStart(), t.getEnd());
    }

    public static ServiceOrderDto toServiceOrderDto(ServiceOrder o) {
        if (o == null) {
            return null;
        }
        ServiceOrderDto dto = new ServiceOrderDto();
        dto.setId(o.getId());
        dto.setMechanic(toMechanicDto(o.getMechanic()));
        dto.setGarageSlot(toGarageSlotDto(o.getGarageSlot()));
        dto.setTimeSlot(toTimeSlotDto(o.getTimeSlot()));
        dto.setPrice(o.getPrice());
        dto.setStatus(o.getStatus() != null ? o.getStatus().name() : null);
        dto.setSubmittedAt(o.getSubmittedAt());
        dto.setFinishedAt(o.getFinishedAt());
        return dto;
    }
}
