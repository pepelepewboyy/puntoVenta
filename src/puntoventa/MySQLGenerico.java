/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package puntoventa;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 *
 * @author mac
 */
public class MySQLGenerico {

    // Método para conectar a la base de datos
    public Connection conectar(String[] configuracion) throws SQLException {
        String url = "jdbc:mysql://" + configuracion[0] + "/" + configuracion[3];
        return DriverManager.getConnection(url, configuracion[1], configuracion[2]);
    }

    // Método para cerrar la conexión y el cursor
    public void cerrar(ResultSet rs, Statement stmt, Connection conn) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Leer todos los registros de una tabla
    public List<Map<String, Object>> leer(String[] configuracion, String tabla) {
        List<Map<String, Object>> resultados = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = conectar(configuracion);
            stmt = conn.createStatement();
            String query = "SELECT * FROM " + tabla;
            rs = stmt.executeQuery(query);

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    Object value = rs.getObject(i);
                    fila.put(columnName, value);
                }
                resultados.add(fila);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al leer registros\n" + e, "ERROR", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            cerrar(rs, stmt, conn);
        }

        return resultados;
    }

    // Insertar registro
    public String crear(Map<String, Object> datos, String[] configuracion, String tabla) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = conectar(configuracion);
            String columnas = String.join(", ", datos.keySet());
            String placeholders = String.join(", ", datos.keySet().stream().map(k -> "?").toArray(String[]::new));
            String sql = "INSERT INTO " + tabla + " (" + columnas + ") VALUES (" + placeholders + ")";
            pstmt = conn.prepareStatement(sql);

            int index = 1;
            for (Object valor : datos.values()) {
                pstmt.setObject(index++, valor);
            }

            int executeUpdate = pstmt.executeUpdate();

            return "Registro insertado correctamente";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error al insertar";
        } finally {
            cerrar(null, pstmt, conn);
        }
    }

    // Borrar registro por ID
    public String borrar(int id, String[] configuracion, String tabla) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = conectar(configuracion);
            String sql = "DELETE FROM " + tabla + " WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            return "Registro borrado";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error al borrar";
        } finally {
            cerrar(null, pstmt, conn);
        }
    }

    // Actualizar registro
    public String actualizar(int id, Map<String, Object> datos, String[] configuracion, String tabla) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = conectar(configuracion);
            String setClause = String.join(", ", datos.keySet().stream().map(k -> k + " = ?").toArray(String[]::new));
            String sql = "UPDATE " + tabla + " SET " + setClause + " WHERE id = ?";
            pstmt = conn.prepareStatement(sql);

            int index = 1;
            for (Object valor : datos.values()) {
                pstmt.setObject(index++, valor);
            }
            pstmt.setInt(index, id);
            pstmt.executeUpdate();
            return "Registro actualizado correctamente";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error al actualizar";
        } finally {
            cerrar(null, pstmt, conn);
        }
    }

}
