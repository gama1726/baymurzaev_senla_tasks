package autoservice.dao;

import autoservice.database.JpaEntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import autoservice.entity.GarageSlotEntity;
import autoservice.mapper.EntityMapper;
import autoservice.model.GarageSlot;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * DAO для работы с гаражными местами в БД через JPA.
 */
@Repository
public class GarageSlotDAO implements GenericDAO<GarageSlot, Integer> {

    private static final Logger logger = LogManager.getLogger(GarageSlotDAO.class);

    @Autowired
    private JpaEntityManagerFactory entityManagerFactory;
    
    /**
     * Получает EntityManager из фабрики.
     */
    private EntityManager getEntityManager() {
        return entityManagerFactory.getEntityManagerFactory().createEntityManager();
    }
    
    @Override
    public GarageSlot save(GarageSlot garageSlot) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            GarageSlotEntity entity = EntityMapper.toGarageSlotEntity(garageSlot);
            em.persist(entity);
            transaction.commit();
            logger.debug("GarageSlot saved: {}", garageSlot);
            return garageSlot;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Error saving garage slot: {}", garageSlot, e);
            throw new RuntimeException("Ошибка при сохранении гаражного места: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public Optional<GarageSlot> findById(Integer id) {
        EntityManager em = getEntityManager();
        try {
            GarageSlotEntity entity = em.find(GarageSlotEntity.class, id);
            if (entity == null) {
                return Optional.empty();
            }
            return Optional.of(EntityMapper.toGarageSlot(entity));
        } catch (Exception e) {
            logger.error("Error finding garage slot by id: {}", id, e);
            throw new RuntimeException("Ошибка при поиске гаражного места: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<GarageSlot> findAll() {
        EntityManager em = getEntityManager();
        try {
            List<GarageSlotEntity> entities = em.createQuery(
                "SELECT g FROM GarageSlotEntity g", GarageSlotEntity.class
            ).getResultList();
            return entities.stream()
                .map(EntityMapper::toGarageSlot)
                .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error finding all garage slots", e);
            throw new RuntimeException("Ошибка при получении списка гаражных мест: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public GarageSlot update(GarageSlot garageSlot) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            GarageSlotEntity entity = em.find(GarageSlotEntity.class, garageSlot.getId());
            if (entity == null) {
                throw new RuntimeException("Гаражное место с ID " + garageSlot.getId() + " не найдено для обновления");
            }
            entity.setOccupied(garageSlot.getStatus());
            em.merge(entity);
            transaction.commit();
            logger.debug("GarageSlot updated: {}", garageSlot);
            return garageSlot;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Error updating garage slot: {}", garageSlot, e);
            throw new RuntimeException("Ошибка при обновлении гаражного места: " + e.getMessage(), e);
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
            GarageSlotEntity entity = em.find(GarageSlotEntity.class, id);
            if (entity == null) {
                return false;
            }
            em.remove(entity);
            transaction.commit();
            logger.debug("GarageSlot deleted: id={}", id);
            return true;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Error deleting garage slot: id={}", id, e);
            throw new RuntimeException("Ошибка при удалении гаражного места: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
