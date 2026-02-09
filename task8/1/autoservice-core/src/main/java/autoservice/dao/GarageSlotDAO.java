package autoservice.dao;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import autoservice.entity.GarageSlotEntity;
import autoservice.mapper.EntityMapper;
import autoservice.model.GarageSlot;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * DAO для работы с гаражными местами в БД через JPA.
 * Транзакции управляются через @Transactional в слое сервиса.
 */
@Repository
public class GarageSlotDAO implements GenericDAO<GarageSlot, Integer> {

    private static final Logger logger = LogManager.getLogger(GarageSlotDAO.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public GarageSlot save(GarageSlot garageSlot) {
        GarageSlotEntity entity = EntityMapper.toGarageSlotEntity(garageSlot);
        em.persist(entity);
        logger.debug("GarageSlot saved: {}", garageSlot);
        return garageSlot;
    }

    @Override
    public Optional<GarageSlot> findById(Integer id) {
        GarageSlotEntity entity = em.find(GarageSlotEntity.class, id);
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of(EntityMapper.toGarageSlot(entity));
    }

    @Override
    public List<GarageSlot> findAll() {
        List<GarageSlotEntity> entities = em.createQuery(
            "SELECT g FROM GarageSlotEntity g", GarageSlotEntity.class
        ).getResultList();
        return entities.stream()
            .map(EntityMapper::toGarageSlot)
            .collect(Collectors.toList());
    }

    @Override
    public GarageSlot update(GarageSlot garageSlot) {
        GarageSlotEntity entity = em.find(GarageSlotEntity.class, garageSlot.getId());
        if (entity == null) {
            throw new RuntimeException("Гаражное место с ID " + garageSlot.getId() + " не найдено для обновления");
        }
        entity.setOccupied(garageSlot.getStatus());
        em.merge(entity);
        logger.debug("GarageSlot updated: {}", garageSlot);
        return garageSlot;
    }

    @Override
    public boolean deleteById(Integer id) {
        GarageSlotEntity entity = em.find(GarageSlotEntity.class, id);
        if (entity == null) {
            return false;
        }
        em.remove(entity);
        logger.debug("GarageSlot deleted: id={}", id);
        return true;
    }
}
