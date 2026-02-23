package autoservice.dao;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import autoservice.entity.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

/**
 * DAO для загрузки пользователей по логину (аутентификация).
 */
@Repository
public class UserDAO {

    @PersistenceContext
    private EntityManager em;

    /**
     * Находит пользователя по имени (логину).
     */
    public Optional<UserEntity> findByUsername(String username) {
        TypedQuery<UserEntity> q = em.createQuery(
            "SELECT u FROM UserEntity u WHERE u.username = :username",
            UserEntity.class
        );
        q.setParameter("username", username);
        return q.getResultList().stream().findFirst();
    }
}
