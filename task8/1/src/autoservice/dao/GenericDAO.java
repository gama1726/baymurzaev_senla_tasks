package autoservice.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Базовый интерфейс для DAO с общими CRUD операциями.
 * Реализует паттерн GenericDAO.
 * 
 * @param <T> тип сущности
 * @param <ID> тип идентификатора
 */
public interface GenericDAO<T, ID> {
    
    /**
     * Сохраняет сущность в БД.
     * 
     * @param entity сущность для сохранения
     * @return сохраненная сущность
     * @throws SQLException если произошла ошибка при работе с БД
     */
    T save(T entity) throws SQLException;
    
    /**
     * Находит сущность по идентификатору.
     * 
     * @param id идентификатор
     * @return Optional с сущностью, если найдена
     * @throws SQLException если произошла ошибка при работе с БД
     */
    Optional<T> findById(ID id) throws SQLException;
    
    /**
     * Находит все сущности.
     * 
     * @return список всех сущностей
     * @throws SQLException если произошла ошибка при работе с БД
     */
    List<T> findAll() throws SQLException;
    
    /**
     * Обновляет сущность в БД.
     * 
     * @param entity сущность для обновления
     * @return обновленная сущность
     * @throws SQLException если произошла ошибка при работе с БД
     */
    T update(T entity) throws SQLException;
    
    /**
     * Удаляет сущность по идентификатору.
     * 
     * @param id идентификатор
     * @return true если сущность была удалена, false если не найдена
     * @throws SQLException если произошла ошибка при работе с БД
     */
    boolean deleteById(ID id) throws SQLException;
    
    /**
     * Получает соединение с БД.
     * 
     * @return соединение с БД
     */
    Connection getConnection();
}
