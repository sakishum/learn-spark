package com.asiainfo.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by migle on 2016/8/16.
 */
public class DbUtil {
    private String url;
    private String username;
    private String pwd;
    private Connection connection;

    static {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public DbUtil(String url, String username, String pwd) {
        this.url = url;
        this.username = username;
        this.pwd = pwd;
    }


    public Connection getConnection() throws SQLException {
        if (this.connection == null) {
            this.connection = DriverManager.getConnection(url, username, pwd);
        }
        return this.connection;
    }


    public <T> List<T> query(String sql, ResultMapper<T> mapper) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<T> result = new ArrayList<T>();
        try {
            stmt = getConnection().prepareStatement(sql);
            rs = stmt.executeQuery();
            while(rs.next()){
                T t = mapper.map(rs);
                result.add(t);
            }

        } catch (SQLException e) {
            this.rethrow(e, sql, "");
        } finally {
            try {
                close(rs);
            } finally {
                close(stmt);
            }
        }
        return result;
    }


    public int update(String sql) throws SQLException {
        PreparedStatement stmt = null;
        int rows = 0;

        try {
            stmt = getConnection().prepareStatement(sql);
            rows = stmt.executeUpdate();

        } catch (SQLException e) {
            this.rethrow(e, sql, "");
        } finally {
            close(stmt);
        }
        return rows;
    }

    protected void rethrow(SQLException cause, String sql, Object... params)
            throws SQLException {

        String causeMessage = cause.getMessage();
        if (causeMessage == null) {
            causeMessage = "";
        }
        StringBuffer msg = new StringBuffer(causeMessage);

        msg.append(" Query: ");
        msg.append(sql);
        msg.append(" Parameters: ");

        if (params == null) {
            msg.append("[]");
        } else {
            msg.append(Arrays.deepToString(params));
        }

        SQLException e = new SQLException(msg.toString(), cause.getSQLState(),
                cause.getErrorCode());
        e.setNextException(cause);

        throw e;
    }

    protected void close(Connection conn) throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

    protected void close(Statement stmt) throws SQLException {
        if (stmt != null) {
            stmt.close();
        }
    }

    protected void close(ResultSet rs) throws SQLException {
        if (rs != null) {
            rs.close();
        }
    }

    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
