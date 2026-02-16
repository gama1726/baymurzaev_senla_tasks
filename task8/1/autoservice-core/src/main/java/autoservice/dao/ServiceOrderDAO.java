package autoservice.dao;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import autoservice.entity.ServiceOrderEntity;
import autoservice.mapper.EntityMapper;
import autoservice.model.ServiceOrder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * DAO для работы с заказами в БД через JPA.
 * Транзакции управляются через @Transactional в слое сервиса.
 */
@Repository
public class ServiceOrderDAO implements GenericDAO<ServiceOrder, Integer> {

    private static final Logger logger = LogManager.getLogger(ServiceOrderDAO.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public ServiceOrder save(ServiceOrder order) {
        ServiceOrderEntity entity = EntityMapper.toServiceOrderEntity(order);
        em.persist(entity);
        logger.debug("ServiceOrder saved: {}", order);
        return order;
    }

    @Override
    public Optional<ServiceOrder> findById(Integer id) {
        ServiceOrderEntity entity = em.find(ServiceOrderEntity.class, id);
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of(EntityMapper.toServiceOrder(entity));
    }

    @Override
    public List<ServiceOrder> findAll() {
        List<ServiceOrderEntity> entities = em.createQuery(
            "SELECT o FROM ServiceOrderEntity o", ServiceOrderEntity.class
        ).getResultList();
        return entities.stream()
            .map(EntityMapper::toServiceOrder)
            .collect(Collectors.toList());
    }

    @Override
    public ServiceOrder update(ServiceOrder order) {
        ServiceOrderEntity entity = em.find(ServiceOrderEntity.class, order.getId());
        if (entity == null) {
            throw new RuntimeException("Заказ с ID " + order.getId() + " не найден для обновления");
        }
        entity.setMechanic(EntityMapper.toMechanicEntity(order.getMechanic()));
        entity.setGarageSlot(EntityMapper.toGarageSlotEntity(order.getGarageSlot()));
        entity.setTimeSlotStart(order.getTimeSlot().getStart());
        entity.setTimeSlotEnd(order.getTimeSlot().getEnd());
        entity.setPrice(order.getPrice());
        entity.setStatus(EntityMapper.toOrderStatusEnum(order.getStatus()));
        entity.setSubmittedAt(order.getSubmittedAt());
        entity.setFinishedAt(order.getFinishedAt());
        em.merge(entity);
        logger.debug("ServiceOrder updated: {}", order);
        return order;
    }

    @Override
    public boolean deleteById(Integer id) {
        ServiceOrderEntity entity = em.find(ServiceOrderEntity.class, id);
        if (entity == null) {
            return false;
        }
        em.remove(entity);
        logger.debug("ServiceOrder deleted: id={}", id);
        return true;
    }
}
