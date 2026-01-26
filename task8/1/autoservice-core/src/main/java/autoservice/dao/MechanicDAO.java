package autoservice.dao;

import autoservice.annotation.Component;
import autoservice.annotation.Inject;
import autoservice.database.JpaEntityManagerFactory;
import autoservice.entity.MechanicEntity;
import autoservice.mapper.EntityMapper;
import autoservice.model.Mechanic;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * DAO для работы с механиками в БД через JPA.
 */
@Component
public class MechanicDAO implements GenericDAO<Mechanic, Integer> {
    
    private static final Logger logger = LogManager.getLogger(MechanicDAO.class);
    
    @Inject
    private JpaEntityManagerFactory entityManagerFactory;
    
    /**
     * Получает EntityManager из фабрики.
     */
    private EntityManager getEntityManager() {
        return entityManagerFactory.getEntityManagerFactory().createEntityManager();
    }
    
    @Override
    public Mechanic save(Mechanic mechanic) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            MechanicEntity entity = EntityMapper.toMechanicEntity(mechanic);
            em.persist(entity);
            transaction.commit();
            logger.debug("Mechanic saved: {}", mechanic);
            return mechanic;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Error saving mechanic: {}", mechanic, e);
            throw new RuntimeException("Ошибка при сохранении механика: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public Optional<Mechanic> findById(Integer id) {
        EntityManager em = getEntityManager();
        try {
            MechanicEntity entity = em.find(MechanicEntity.class, id);
            if (entity == null) {
                return Optional.empty();
            }
            return Optional.of(EntityMapper.toMechanic(entity));
        } catch (Exception e) {
            logger.error("Error finding mechanic by id: {}", id, e);
            throw new RuntimeException("Ошибка при поиске механика: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Mechanic> findAll() {
        EntityManager em = getEntityManager();
        try {
            List<MechanicEntity> entities = em.createQuery(
                "SELECT m FROM MechanicEntity m", MechanicEntity.class
            ).getResultList();
            return entities.stream()
                .map(EntityMapper::toMechanic)
                .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error finding all mechanics", e);
            throw new RuntimeException("Ошибка при получении списка механиков: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public Mechanic update(Mechanic mechanic) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            MechanicEntity entity = em.find(MechanicEntity.class, mechanic.getId());
            if (entity == null) {
                throw new RuntimeException("Механик с ID " + mechanic.getId() + " не найден для обновления");
            }
            entity.setName(mechanic.getName());
            em.merge(entity);
            transaction.commit();
            logger.debug("Mechanic updated: {}", mechanic);
            return mechanic;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Error updating mechanic: {}", mechanic, e);
            throw new RuntimeException("Ошибка при обновлении механика: " + e.getMessage(), e);
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
            MechanicEntity entity = em.find(MechanicEntity.class, id);
            if (entity == null) {
                return false;
            }
            em.remove(entity);
            transaction.commit();
            logger.debug("Mechanic deleted: id={}", id);
            return true;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Error deleting mechanic: id={}", id, e);
            throw new RuntimeException("Ошибка при удалении механика: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
