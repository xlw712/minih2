package com.levin.mh2;

import static org.junit.Assert.assertTrue;

import com.google.common.io.Resources;
import com.levin.mh2.core.MiniHS2;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.*;

/**
 * a simple MiniH2Server TestUnit
 */
public class AppTest

{
    Connection conn;
    Statement stmt;

    @Before
    public void setup() throws SQLException, ClassNotFoundException {
        Class.forName(MiniHS2.getJdbcDriverName());
        conn = DriverManager.getConnection("jdbc:hive2://localhost:58763/default", "mi", "bar");

        conn.createStatement().execute("SET hive.support.concurrency = FALSE");
        stmt = conn.createStatement();
    }

    @Test
    public void shouldAnswerWithTrue() throws SQLException {
        stmt.execute("DROP TABLE IF EXISTS testTab1");
        stmt.execute("CREATE TABLE testTab1 (under_col INT COMMENT 'the under column', value STRING) COMMENT ' test table'");
        stmt.execute("LOAD DATA LOCAL inpath '" + Resources.getResource("examples/files/kv1.txt").getPath() + "' INTO TABLE testTab1");
        ResultSet res = stmt.executeQuery("SELECT * FROM testTab1");
        int columns = res.getMetaData().getColumnCount();
        while (res.next()) {
            for (int c = 1; c <= columns; c++) {
                String columnV=res.getString(c);
                System.out.print(columnV+"\t");
            }
            System.out.println();
        }
    }
}
