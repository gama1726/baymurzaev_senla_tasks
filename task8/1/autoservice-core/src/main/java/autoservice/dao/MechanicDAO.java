package autoservice.dao;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import autoservice.entity.MechanicEntity;
import autoservice.mapper.EntityMapper;
import autoservice.model.Mechanic;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * DAO для работы с механиками в БД через JPA.
 * Транзакции управляются через @Transactional в слое сервиса.
 */
@Repository
public class MechanicDAO implements GenericDAO<Mechanic, Integer> {

    private static final Logger logger = LogManager.getLogger(MechanicDAO.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public Mechanic save(Mechanic mechanic) {
        MechanicEntity entity = EntityMapper.toMechanicEntity(mechanic);
        em.persist(entity);
        logger.debug("Mechanic saved: {}", mechanic);
        return mechanic;
    }

    @Override
    public Optional<Mechanic> findById(Integer id) {
        MechanicEntity entity = em.find(MechanicEntity.class, id);
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of(EntityMapper.toMechanic(entity));
    }

    @Override
    public List<Mechanic> findAll() {
        List<MechanicEntity> entities = em.createQuery(
            "SELECT m FROM MechanicEntity m", MechanicEntity.class
        ).getResultList();
        return entities.stream()
            .map(EntityMapper::toMechanic)
            .collect(Collectors.toList());
    }

    @Override
    public Mechanic update(Mechanic mechanic) {
        MechanicEntity entity = em.find(MechanicEntity.class, mechanic.getId());
        if (entity == null) {
            throw new RuntimeException("Механик с ID " + mechanic.getId() + " не найден для обновления");
        }
        entity.setName(mechanic.getName());
        em.merge(entity);
        logger.debug("Mechanic updated: {}", mechanic);
        return mechanic;
    }

    @Override
    public boolean deleteById(Integer id) {
        MechanicEntity entity = em.find(MechanicEntity.class, id);
        if (entity == null) {
            return false;
        }
        em.remove(entity);
        logger.debug("Mechanic deleted: id={}", id);
        return true;
    }
}
