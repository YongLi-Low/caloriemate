package csf.iss.server.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Optional;

import javax.sql.DataSource;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import csf.iss.server.models.User;

@Repository
public class UserRepository {
    
    private static final String INSERT_USER_SQL = "insert into users (id, username, email, password) values (?, ?, ?, ?)";
    private static final String FIND_BY_USERNAME_SQL = "select username from users where username = ?";
    private static final String GET_ID_SQL = "select id from users where username = ?";
    private static final String FIND_BY_USERNAME_PASSWORD_SQL = "select * from users where username = ? and password = ?";
    // Update BMI
    private static final String UPDATE_BMI_SQL = "update users set bmi = ? where id = ?";
    // Update Calories
    private static final String UPDATE_CAL_SQL = "update users set calories = ? where id = ?";
    // Get BMI from id
    private static final String GET_BMI_SQL = "select bmi from users where id = ?";
    // Get Calories from id
    private static final String GET_CAL_SQL = "select calories from users where id = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    // Register new user
    public void insertUser(User user) throws SQLException {

        try (
            Connection con = dataSource.getConnection();
            PreparedStatement ps = con.prepareStatement(INSERT_USER_SQL);
        ) {
            ps.setString(1, user.getId());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getEmail());
            // Hashing the password
            String hashPassword = DigestUtils.md5Hex(user.getPassword());
            ps.setString(4, hashPassword);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update BMI
    public void updateBmi(String id, Float bmi) {
        jdbcTemplate.update(UPDATE_BMI_SQL, bmi, id);
    }

    // Get BMI
    public Optional<Float> getBMI(String id) {
        Object[] params = { id };
        int[] types = { Types.VARCHAR };

        try {
            Float bmi = jdbcTemplate.queryForObject(GET_BMI_SQL, params, types, Float.class);
            return Optional.ofNullable(bmi);
        }
        catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // Update Calories
    public void updateCal(String id, Integer cal) {
        jdbcTemplate.update(UPDATE_CAL_SQL, cal, id);
    }

    // Get Calories
    public Optional<Integer> getCal(String id) {
        Object[] params = { id };
        int[] types = { Types.VARCHAR };

        try {
            Integer calories = jdbcTemplate.queryForObject(GET_CAL_SQL, params, types, Integer.class);
            return Optional.ofNullable(calories);
        }
        catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // Check whether username exists. True means username exist
    public boolean checkUsername(User user) {

        String username = null;

        username = jdbcTemplate.query(FIND_BY_USERNAME_SQL,
                            (ResultSet rs) -> {
                                if (!rs.next()) {
                                    return null;
                                }
                                else {
                                    return rs.getString("username");
                                }
                            },
                             user.getUsername());
        
        if (username == null) {
            return false;
        }
        else {
            return true;
        }
    }

    public String getId(String username) {
        String id = null;

        id = jdbcTemplate.query(GET_ID_SQL,
                        (ResultSet rs) -> {
                            if (!rs.next()) {
                                return null;
                            }
                            else {
                                return rs.getString("id");
                            }
                        }, username);
                
        return id;
    }

    // Check whether username and password match/exist. True means exist
    public boolean checkUsernamePassword(User user) {

        String hashPassword = DigestUtils.md5Hex(user.getPassword());

        String username = jdbcTemplate.query(FIND_BY_USERNAME_PASSWORD_SQL,
                            (ResultSet rs) -> {
                                if (!rs.next()) {
                                    return null;
                                }
                                else {
                                    return rs.getString("username");
                                }
                            },
                            user.getUsername(), hashPassword);

        if (username == null) {
            return false;
        }
        else {
            return true;
        }
    }

}
