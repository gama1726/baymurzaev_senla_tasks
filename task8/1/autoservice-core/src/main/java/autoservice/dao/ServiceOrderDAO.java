package autoservice.dao;

import autoservice.annotation.Component;
import autoservice.annotation.Inject;
import autoservice.database.ConnectionManager;
import autoservice.model.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO для работы с заказами в БД.
 */
@Component
public class ServiceOrderDAO implements GenericDAO<ServiceOrder, Integer> {
    
    // Константы SQL запросов
    private static final String TABLE_NAME = "service_orders";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_MECHANIC_ID = "mechanic_id";
    private static final String COLUMN_GARAGE_SLOT_ID = "garage_slot_id";
    private static final String COLUMN_TIME_SLOT_START = "time_slot_start";
    private static final String COLUMN_TIME_SLOT_END = "time_slot_end";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_SUBMITTED_AT = "submitted_at";
    private static final String COLUMN_FINISHED_AT = "finished_at";
    
    private static final String SQL_INSERT = 
        "INSERT INTO " + TABLE_NAME + " (" + COLUMN_ID + ", " + COLUMN_MECHANIC_ID + ", " + 
        COLUMN_GARAGE_SLOT_ID + ", " + COLUMN_TIME_SLOT_START + ", " + COLUMN_TIME_SLOT_END + ", " + 
        COLUMN_PRICE + ", " + COLUMN_STATUS + ", " + COLUMN_SUBMITTED_AT + ", " + COLUMN_FINISHED_AT + 
        ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String SQL_SELECT_BY_ID = 
        "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
    
    private static final String SQL_SELECT_ALL = 
        "SELECT * FROM " + TABLE_NAME;
    
    private static final String SQL_UPDATE = 
        "UPDATE " + TABLE_NAME + " SET " + COLUMN_MECHANIC_ID + " = ?, " + 
        COLUMN_GARAGE_SLOT_ID + " = ?, " + COLUMN_TIME_SLOT_START + " = ?, " + 
        COLUMN_TIME_SLOT_END + " = ?, " + COLUMN_PRICE + " = ?, " + COLUMN_STATUS + " = ?, " + 
        COLUMN_SUBMITTED_AT + " = ?, " + COLUMN_FINISHED_AT + " = ? WHERE " + COLUMN_ID + " = ?";
    
    private static final String SQL_DELETE = 
        "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
    
    @Inject
    private ConnectionManager connectionManager;
    
    @Inject
    private MechanicDAO mechanicDAO;
    
    @Inject
    private GarageSlotDAO garageSlotDAO;
    
    @Override
    public Connection getConnection() {
        return connectionManager.getConnection();
    }
    
    @Override
    public ServiceOrder save(ServiceOrder order) throws SQLException {
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {
            setInsertParameters(stmt, order);
            stmt.executeUpdate();
            return order;
        }
    }
    
    @Override
    public Optional<ServiceOrder> findById(Integer id) throws SQLException {
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToServiceOrder(rs));
                }
                return Optional.empty();
            }
        }
    }
    
    @Override
    public List<ServiceOrder> findAll() throws SQLException {
        Connection conn = getConnection();
        List<ServiceOrder> orders = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {
            while (rs.next()) {
                orders.add(mapResultSetToServiceOrder(rs));
            }
        }
        return orders;
    }
    
    @Override
    public ServiceOrder update(ServiceOrder order) throws SQLException {
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {
            setUpdateParameters(stmt, order);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Заказ с ID " + order.getId() + " не найден для обновления");
            }
            return order;
        }
    }
    
    @Override
    public boolean deleteById(Integer id) throws SQLException {
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Устанавливает параметры заказа в PreparedStatement для INSERT.
     */
    private void setInsertParameters(PreparedStatement stmt, ServiceOrder order) throws SQLException {
        stmt.setInt(1, order.getId());
        stmt.setInt(2, order.getMechanic().getId());
        stmt.setInt(3, order.getGarageSlot().getId());
        stmt.setTimestamp(4, Timestamp.valueOf(order.getTimeSlot().getStart()));
        stmt.setTimestamp(5, Timestamp.valueOf(order.getTimeSlot().getEnd()));
        stmt.setInt(6, order.getPrice());
        stmt.setString(7, order.getStatus().name());
        stmt.setTimestamp(8, order.getSubmittedAt() != null ? 
            Timestamp.valueOf(order.getSubmittedAt()) : null);
        stmt.setTimestamp(9, order.getFinishedAt() != null ? 
            Timestamp.valueOf(order.getFinishedAt()) : null);
    }
    
    /**
     * Устанавливает параметры заказа в PreparedStatement для UPDATE.
     */
    private void setUpdateParameters(PreparedStatement stmt, ServiceOrder order) throws SQLException {
        stmt.setInt(1, order.getMechanic().getId());
        stmt.setInt(2, order.getGarageSlot().getId());
        stmt.setTimestamp(3, Timestamp.valueOf(order.getTimeSlot().getStart()));
        stmt.setTimestamp(4, Timestamp.valueOf(order.getTimeSlot().getEnd()));
        stmt.setInt(5, order.getPrice());
        stmt.setString(6, order.getStatus().name());
        stmt.setTimestamp(7, order.getSubmittedAt() != null ? 
            Timestamp.valueOf(order.getSubmittedAt()) : null);
        stmt.setTimestamp(8, order.getFinishedAt() != null ? 
            Timestamp.valueOf(order.getFinishedAt()) : null);
        stmt.setInt(9, order.getId()); // для WHERE
    }
    
    /**
     * Маппит ResultSet в объект ServiceOrder с загрузкой связанных объектов.
     */
    private ServiceOrder mapResultSetToServiceOrder(ResultSet rs) throws SQLException {
        int id = rs.getInt(COLUMN_ID);
        int mechanicId = rs.getInt(COLUMN_MECHANIC_ID);
        int garageSlotId = rs.getInt(COLUMN_GARAGE_SLOT_ID);
        LocalDateTime timeSlotStart = rs.getTimestamp(COLUMN_TIME_SLOT_START).toLocalDateTime();
        LocalDateTime timeSlotEnd = rs.getTimestamp(COLUMN_TIME_SLOT_END).toLocalDateTime();
        int price = rs.getInt(COLUMN_PRICE);
        String statusStr = rs.getString(COLUMN_STATUS);
        Timestamp submittedAtTs = rs.getTimestamp(COLUMN_SUBMITTED_AT);
        Timestamp finishedAtTs = rs.getTimestamp(COLUMN_FINISHED_AT);
        
        // Загружаем связанные объекты
        Mechanic mechanic = mechanicDAO.findById(mechanicId)
            .orElseThrow(() -> new SQLException("Механик с ID " + mechanicId + " не найден"));
        GarageSlot garageSlot = garageSlotDAO.findById(garageSlotId)
            .orElseThrow(() -> new SQLException("Гаражное место с ID " + garageSlotId + " не найдено"));
        
        // Создаем заказ через Builder
        ServiceOrder order = new ServiceOrder.Builder()
            .setId(id)
            .setMechanic(mechanic)
            .setGarageSlot(garageSlot)
            .setTimeSlot(new TimeSlot(timeSlotStart, timeSlotEnd))
            .setPrice(price)
            .build();
        
        // Устанавливаем статус и даты
        OrderStatus status = OrderStatus.valueOf(statusStr);
        LocalDateTime submittedAt = submittedAtTs != null ? submittedAtTs.toLocalDateTime() : null;
        LocalDateTime finishedAt = finishedAtTs != null ? finishedAtTs.toLocalDateTime() : null;
        order.setStatusAndDates(status, submittedAt, finishedAt);
        
        return order;
    }
}
