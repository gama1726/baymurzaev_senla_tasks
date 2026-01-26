package autoservice.dao;

import autoservice.annotation.Component;
import autoservice.annotation.Inject;
import autoservice.database.ConnectionManager;
import autoservice.model.Mechanic;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO для работы с механиками в БД.
 */
@Component
public class MechanicDAO implements GenericDAO<Mechanic, Integer> {
    
    // Константы SQL запросов
    private static final String TABLE_NAME = "mechanics";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    
    private static final String SQL_INSERT = 
        "INSERT INTO " + TABLE_NAME + " (" + COLUMN_ID + ", " + COLUMN_NAME + ") VALUES (?, ?)";
    
    private static final String SQL_SELECT_BY_ID = 
        "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
    
    private static final String SQL_SELECT_ALL = 
        "SELECT * FROM " + TABLE_NAME;
    
    private static final String SQL_UPDATE = 
        "UPDATE " + TABLE_NAME + " SET " + COLUMN_NAME + " = ? WHERE " + COLUMN_ID + " = ?";
    
    private static final String SQL_DELETE = 
        "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
    
    @Inject
    private ConnectionManager connectionManager;
    
    @Override
    public Connection getConnection() {
        return connectionManager.getConnection();
    }
    
    @Override
    public Mechanic save(Mechanic mechanic) throws SQLException {
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {
            stmt.setInt(1, mechanic.getId());
            stmt.setString(2, mechanic.getName());
            stmt.executeUpdate();
            return mechanic;
        }
    }
    
    @Override
    public Optional<Mechanic> findById(Integer id) throws SQLException {
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMechanic(rs));
                }
                return Optional.empty();
            }
        }
    }
    
    @Override
    public List<Mechanic> findAll() throws SQLException {
        Connection conn = getConnection();
        List<Mechanic> mechanics = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {
            while (rs.next()) {
                mechanics.add(mapResultSetToMechanic(rs));
            }
        }
        return mechanics;
    }
    
    @Override
    public Mechanic update(Mechanic mechanic) throws SQLException {
        Connection conn = getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {
            stmt.setString(1, mechanic.getName());
            stmt.setInt(2, mechanic.getId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Механик с ID " + mechanic.getId() + " не найден для обновления");
            }
            return mechanic;
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
     * Маппит ResultSet в объект Mechanic.
     */
    private Mechanic mapResultSetToMechanic(ResultSet rs) throws SQLException {
        int id = rs.getInt(COLUMN_ID);
        String name = rs.getString(COLUMN_NAME);
        return new Mechanic(id, name);
    }
}
