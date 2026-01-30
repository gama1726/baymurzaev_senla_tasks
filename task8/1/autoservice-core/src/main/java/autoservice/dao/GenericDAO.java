package autoservice.dao;

import java.util.List;
import java.util.Optional;

/**
 * Базовый интерфейс для DAO с общими CRUD операциями.
 * Реализует паттерн GenericDAO.
 * 
 * @param <T> тип сущности (Domain модель)
 * @param <ID> тип идентификатора
 */
public interface GenericDAO<T, ID> {
    
    /**
     * Сохраняет сущность в БД.
     * 
     * @param entity сущность для сохранения
     * @return сохраненная сущность
     */
    T save(T entity);
    
    /**
     * Находит сущность по идентификатору.
     * 
     * @param id идентификатор
     * @return Optional с сущностью, если найдена
     */
    Optional<T> findById(ID id);
    
    /**
     * Находит все сущности.
     * 
     * @return список всех сущностей
     */
    List<T> findAll();
    
    /**
     * Обновляет сущность в БД.
     * 
     * @param entity сущность для обновления
     * @return обновленная сущность
     */
    T update(T entity);
    
    /**
     * Удаляет сущность по идентификатору.
     * 
     * @param id идентификатор
     * @return true если сущность была удалена, false если не найдена
     */
    boolean deleteById(ID id);
}
