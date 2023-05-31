package csf.iss.server.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import csf.iss.server.models.Calories;

@Repository
public class NutritionRepository {

    private static final String INSERT_NUTRITION_BYUSER_SQL = "insert into caloriestracker (username, calories, entryDate, user_id, foodName, quantity)" 
    + "values (?, ?, ?, ?, ?, ?)";
    private static final String FIND_FOOD_LIST_BY_USERID_DATE_SQL = "select cal_id, foodName, quantity, calories, entryDate from caloriestracker where user_id = ? and entryDate = ?";
    private static final String DELETE_FOOD_BY_ID = "delete from caloriestracker where cal_id = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    // Insert food and calories count to caloriestracker table
    public void insertCalories(Calories calories) throws SQLException {
        
        try (
            Connection con = dataSource.getConnection();
            PreparedStatement ps = con.prepareStatement(INSERT_NUTRITION_BYUSER_SQL);
        ) {
            ps.setString(1, calories.getUsername());
            ps.setDouble(2, calories.getCalories());
            ps.setString(3, calories.getEntryDate());
            ps.setString(4, calories.getForeignId());
            ps.setString(5, calories.getFoodName());
            ps.setDouble(6, calories.getQuantity());
            ps.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get a list of food and calories from caloriestracker table
    public Optional<List<Calories>> findCalories(String id, String entryDate) {
        List<Calories> caloriesList = jdbcTemplate.query(FIND_FOOD_LIST_BY_USERID_DATE_SQL,
                (PreparedStatement ps) -> {
                    ps.setString(1, id);
                    ps.setString(2, entryDate);
                }, new RowMapper<Calories>() {
                    @Override
                    public Calories mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Calories calories = new Calories();
                        calories.setId(rs.getInt("cal_id"));
                        calories.setFoodName(rs.getString("foodName"));
                        calories.setQuantity(rs.getDouble("quantity"));
                        calories.setCalories(rs.getDouble("calories"));
                        calories.setEntryDate(rs.getString("entryDate"));
                        return calories;
                    }
                });

        if (caloriesList != null) {
            return Optional.of(caloriesList);
        }
        else {
            return Optional.empty();
        }
    }

    public void deleteCalories(int calId) {
        jdbcTemplate.update(DELETE_FOOD_BY_ID, calId);
    }
}
