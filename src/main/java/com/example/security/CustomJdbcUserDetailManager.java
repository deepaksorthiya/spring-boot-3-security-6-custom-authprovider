package com.example.security;

import com.example.usermgmt.AppUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomJdbcUserDetailManager extends JdbcUserDetailsManager {

    public CustomJdbcUserDetailManager(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        assert super.getJdbcTemplate() != null;
        List<AppUser> users = super.getJdbcTemplate().query(
                "select username,password,enabled, firstname, lastname from users where username = ?",
                (rs, rowNum) -> {
                    String username1 = rs.getString(1);
                    String password = rs.getString(2);
                    boolean enabled = rs.getBoolean(3);
                    String fn = rs.getString(4);
                    String ln = rs.getString(5);
                    return new AppUser(username1, password, enabled, true, true, true,
                            AuthorityUtils.NO_AUTHORITIES, fn, ln);
                }, username);

        if (!users.isEmpty()) {
            AppUser user = users.get(0);
            // contains no GrantedAuthority[]
            Set<GrantedAuthority> dbAuthsSet = new HashSet<>();

            dbAuthsSet.addAll(loadUserAuthorities(user.getUsername()));
            dbAuthsSet.addAll(loadGroupAuthorities(user.getUsername()));

            List<GrantedAuthority> dbAuths = new ArrayList<>(dbAuthsSet);
            addCustomAuthorities(user.getUsername(), dbAuths);
            return new AppUser(user.getUsername(), user.getPassword(), user.isEnabled(), user.isEnabled(),
                    user.isEnabled(), user.isEnabled(), dbAuths, user.getFirstName(), user.getFirstName());
        }
        return null;
    }
}
