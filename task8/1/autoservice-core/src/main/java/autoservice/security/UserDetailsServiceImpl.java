package autoservice.security;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import autoservice.dao.UserDAO;
import autoservice.entity.UserEntity;

/**
 * Загрузка пользователя из БД по логину для Spring Security.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final String ROLE_PREFIX = "ROLE_";

    @Autowired
    private UserDAO userDAO;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity entity = userDAO.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority(ROLE_PREFIX + entity.getRole())
        );
        return new User(entity.getUsername(), entity.getPasswordHash(), authorities);
    }
}
