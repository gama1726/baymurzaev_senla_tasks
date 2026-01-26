package autoservice.dao;

import autoservice.annotation.Component;
import autoservice.annotation.Inject;
import autoservice.database.ConnectionManager;
import autoservice.model.GarageSlot;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO для работы с гаражными местами в БД.
 */
@Component
public class GarageSlotDAO implements GenericDAO<GarageSlot, Integer> {
    
    // Константы SQL запросов
    private static final String TABLE_NAME = "garage_slots";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_IS_OCCUPIED = "is_occupied";
    
    private static final String SQL_INSERT = 
        "INSERT INTO " + TABLE_NAME + " (" + COLUMN_ID + ", " + COLUMN_IS_OCCUPIED + ") VALUES (?, ?)";
    
    private static final String SQL_SELECT_BY_ID = 
        "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
    
    private static final String SQL_SELECT_ALL = 
        "SELECT * FROM " + TABLE_NAME;
    
    private static final String SQL_UPDATE = 
        "UPDATE " + TABLE_NAME + " SET " + COLUMN_IS_OCCUPIED + " = ? WHERE " + COLUMN_ID + " = ?";
    
    private static final String SQL_DELETE = 
        "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
    
    @Inject
    private ConnectionManager connectionManager;
    
    @Override
    public Connection getConnection() {
        return connectionManager.getConnection();
    }
    
    @Override
    public GarageSlot save(GarageSlot garageSlot) throws SQLException {
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {
            stmt.setInt(1, garageSlot.getId());
            stmt.setBoolean(2, garageSlot.getStatus());
            stmt.executeUpdate();
            return garageSlot;
        }
    }
    
    @Override
    public Optional<GarageSlot> findById(Integer id) throws SQLException {
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToGarageSlot(rs));
                }
                return Optional.empty();
            }
        }
    }
    
    @Override
    public List<GarageSlot> findAll() throws SQLException {
        Connection conn = getConnection();
        List<GarageSlot> garageSlots = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {
            while (rs.next()) {
                garageSlots.add(mapResultSetToGarageSlot(rs));
            }
        }
        return garageSlots;
    }
    
    @Override
    public GarageSlot update(GarageSlot garageSlot) throws SQLException {
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {
            stmt.setBoolean(1, garageSlot.getStatus());
            stmt.setInt(2, garageSlot.getId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Гаражное место с ID " + garageSlot.getId() + " не найдено для обновления");
            }
            return garageSlot;
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
     * Маппит ResultSet в объект GarageSlot.
     */
    private GarageSlot mapResultSetToGarageSlot(ResultSet rs) throws SQLException {
        int id = rs.getInt(COLUMN_ID);
        boolean isOccupied = rs.getBoolean(COLUMN_IS_OCCUPIED);
        GarageSlot slot = new GarageSlot(id);
        if (isOccupied) {
            slot.occupy();
        }
        return slot;
    }
}
