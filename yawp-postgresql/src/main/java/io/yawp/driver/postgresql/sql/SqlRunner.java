package io.yawp.driver.postgresql.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.postgresql.util.PGobject;

public class SqlRunner {
    private final static Logger logger = Logger.getLogger(SqlRunner.class.getName());

    protected String sql;

    protected Map<String, PlaceHolder> placeHolders = new HashMap<>();

    @SuppressWarnings("serial")
    private class NotImplementedException extends RuntimeException {

    }

    public SqlRunner(String sql) {
        this.sql = sql;
        init();
    }

    private void init() {
        bind();
        parsePlaceHoldersIndexes();
        removePlaceHolders();
    }

    protected void bind() {
    }

    protected void prepare(PreparedStatement ps) throws SQLException {
    }

    protected Object collect(ResultSet rs) throws SQLException {
        throw new NotImplementedException();
    }

    protected Object collectSingle(ResultSet rs) throws SQLException {
        return null;
    }

    protected void bind(String placeHolderKey, PlaceHolder placeHolderObject) {
        placeHolders.put(":" + placeHolderKey, placeHolderObject);
    }

    protected void bind(String placeHolderKey, Object value) {
        bind(placeHolderKey, new PlaceHolder(value));
    }

    private void prepareInternal(PreparedStatement ps) throws SQLException {
        prepare(ps);
        prepareBinded(ps);
    }

    private void prepareBinded(PreparedStatement ps) throws SQLException {
        for (PlaceHolder placeHolder : placeHolders.values()) {
            placeHolder.setValue(ps);
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> T executeQuery(Connection connection) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = connection.prepareStatement(sql);
            prepareInternal(ps);

            rs = ps.executeQuery();

            try {

                return (T) collect(rs);

            } catch (NotImplementedException e) {
                if (!rs.next()) {
                    return null;
                }
                return (T) collectSingle(rs);
            }

        } catch (SQLException e) {
            logger.log(Level.INFO, "problem with sql: " + sql);
            throw new RuntimeException(e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected void execute(Connection connection) {
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            prepareInternal(ps);

            ps.execute();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void parsePlaceHoldersIndexes() {
        Pattern pattern = Pattern.compile("\\:\\w+");
        Matcher matcher = pattern.matcher(sql);

        int index = 1;

        while (matcher.find()) {
            String key = matcher.group();
            PlaceHolder placeHolder = placeHolders.get(key);
            placeHolder.addIndex(index++);
        }
    }

    private void removePlaceHolders() {
        for (String placeHolderKey : placeHolders.keySet()) {
            sql = sql.replaceAll(placeHolderKey, "?");
        }
    }

    protected final PGobject createJsonObject(String json) {
        try {
            PGobject jsonObject = new PGobject();
            jsonObject.setType("jsonb");
            jsonObject.setValue(json);
            return jsonObject;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
