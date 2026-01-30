package autoservice.dao;

import autoservice.annotation.Component;
import autoservice.annotation.Inject;
import autoservice.database.JpaEntityManagerFactory;
import autoservice.entity.ServiceOrderEntity;
import autoservice.mapper.EntityMapper;
import autoservice.model.ServiceOrder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * DAO для работы с заказами в БД через JPA.
 */
@Component
public class ServiceOrderDAO implements GenericDAO<ServiceOrder, Integer> {
    
    private static final Logger logger = LogManager.getLogger(ServiceOrderDAO.class);
    
    @Inject
    private JpaEntityManagerFactory entityManagerFactory;
    
    /**
     * Получает EntityManager из фабрики.
     */
    private EntityManager getEntityManager() {
        return entityManagerFactory.getEntityManagerFactory().createEntityManager();
    }
    
    @Override
    public ServiceOrder save(ServiceOrder order) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            ServiceOrderEntity entity = EntityMapper.toServiceOrderEntity(order);
            em.persist(entity);
            transaction.commit();
            logger.debug("ServiceOrder saved: {}", order);
            return order;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Error saving service order: {}", order, e);
            throw new RuntimeException("Ошибка при сохранении заказа: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public Optional<ServiceOrder> findById(Integer id) {
        EntityManager em = getEntityManager();
        try {
            ServiceOrderEntity entity = em.find(ServiceOrderEntity.class, id);
            if (entity == null) {
                return Optional.empty();
            }
            return Optional.of(EntityMapper.toServiceOrder(entity));
        } catch (Exception e) {
            logger.error("Error finding service order by id: {}", id, e);
            throw new RuntimeException("Ошибка при поиске заказа: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<ServiceOrder> findAll() {
        EntityManager em = getEntityManager();
        try {
            List<ServiceOrderEntity> entities = em.createQuery(
                "SELECT o FROM ServiceOrderEntity o", ServiceOrderEntity.class
            ).getResultList();
            return entities.stream()
                .map(EntityMapper::toServiceOrder)
                .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error finding all service orders", e);
            throw new RuntimeException("Ошибка при получении списка заказов: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public ServiceOrder update(ServiceOrder order) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            ServiceOrderEntity entity = em.find(ServiceOrderEntity.class, order.getId());
            if (entity == null) {
                throw new RuntimeException("Заказ с ID " + order.getId() + " не найден для обновления");
            }
            
            // Обновляем все поля
            entity.setMechanic(EntityMapper.toMechanicEntity(order.getMechanic()));
            entity.setGarageSlot(EntityMapper.toGarageSlotEntity(order.getGarageSlot()));
            entity.setTimeSlotStart(order.getTimeSlot().getStart());
            entity.setTimeSlotEnd(order.getTimeSlot().getEnd());
            entity.setPrice(order.getPrice());
            entity.setStatus(EntityMapper.toOrderStatusEnum(order.getStatus()));
            entity.setSubmittedAt(order.getSubmittedAt());
            entity.setFinishedAt(order.getFinishedAt());
            
            em.merge(entity);
            transaction.commit();
            logger.debug("ServiceOrder updated: {}", order);
            return order;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Error updating service order: {}", order, e);
            throw new RuntimeException("Ошибка при обновлении заказа: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public boolean deleteById(Integer id) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            ServiceOrderEntity entity = em.find(ServiceOrderEntity.class, id);
            if (entity == null) {
                return false;
            }
            em.remove(entity);
            transaction.commit();
            logger.debug("ServiceOrder deleted: id={}", id);
            return true;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Error deleting service order: id={}", id, e);
            throw new RuntimeException("Ошибка при удалении заказа: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
