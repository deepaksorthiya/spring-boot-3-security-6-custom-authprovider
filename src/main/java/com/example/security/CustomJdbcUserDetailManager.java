package com.example.security;

import com.example.usermgmt.AppUser;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        List<AppUser> users = super.getJdbcTemplate().query(
                "select username,password,enabled, firstname, lastname from users where username = ?",
                new String[]{username}, new RowMapper<AppUser>() {
                    @Override
                    public AppUser mapRow(ResultSet rs, int rowNum) throws SQLException {
                        String username = rs.getString(1);
                        String password = rs.getString(2);
                        boolean enabled = rs.getBoolean(3);
                        String fn = rs.getString(4);
                        String ln = rs.getString(5);
                        AppUser currentUser = new AppUser(username, password, enabled, true, true, true,
                                AuthorityUtils.NO_AUTHORITIES);
                        currentUser.setFirstName(fn);
                        currentUser.setLastName(ln);
                        return currentUser;
                    }

                });

        AppUser user = users.get(0); // contains no GrantedAuthority[]
        Set<GrantedAuthority> dbAuthsSet = new HashSet<>();

        dbAuthsSet.addAll(loadUserAuthorities(user.getUsername()));
        dbAuthsSet.addAll(loadGroupAuthorities(user.getUsername()));

        List<GrantedAuthority> dbAuths = new ArrayList<>(dbAuthsSet);
        addCustomAuthorities(user.getUsername(), dbAuths);
        AppUser customAppUser = new AppUser(user.getUsername(), user.getPassword(), user.isEnabled(), user.isEnabled(),
                user.isEnabled(), user.isEnabled(), dbAuths);
        customAppUser.setFirstName(user.getFirstName());
        customAppUser.setLastName(user.getLastName());
        return customAppUser;
    }


}
