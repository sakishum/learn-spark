package me.migle.util.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by migle on 2016/8/16.
 */
public interface ResultMapper<T> {
    public T map(ResultSet rs) throws SQLException;
}