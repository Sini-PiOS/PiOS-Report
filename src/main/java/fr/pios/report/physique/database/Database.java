package fr.pios.report.physique.database;

import fr.pios.report.client.Main;
import fr.pios.report.metier.entity.Report;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;


public abstract class Database {

    Connection connection;
    public String table = "report";

    public Database() {
    }

    public abstract Connection getSQLConnection();

    public abstract void load();

    public void initialize() {
        connection = getSQLConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + table + " LIMIT 0,1;");
            ResultSet rs = ps.executeQuery();
            close(ps, rs);
        } catch (SQLException ex) {
            Main.logger.log(Level.SEVERE, "Unable to retreive connection", ex);
        }
    }
    
        public int getCountReport(boolean removed, boolean resolved) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int nb = 0;
        try {
            conn = getSQLConnection();
            String sql = "SELECT COUNT(*) FROM " + table + " ";
            if(!(removed) || !(resolved)) {
                sql += "WHERE ";
                if(!(removed)) {
                    sql += "removed = false";
                }
                if(!(removed) && !(resolved)) {
                    sql += " AND ";
                }
                if(!resolved) {
                    sql += "resolved = false";
                }
            }
            ps = conn.prepareStatement(sql + ";");
            rs = ps.executeQuery();
            nb = rs.getInt("COUNT(*)");
        } catch (SQLException ex) {
            Main.logger.log(Level.SEVERE, ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Main.logger.log(Level.SEVERE, ex.getMessage());
            }
        }
        return nb;
    }
        
    public int getCountReportByPseudo(String pseudo, boolean removed, boolean resolved) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int nb = 0;
        try {
            conn = getSQLConnection();
            String sql = "SELECT COUNT(*) FROM " + table + " WHERE UPPER(sender) = ? ";
            if(!(removed) || !(resolved)) {
                sql += "AND ";
                if(!(removed)) {
                    sql += "removed = false";
                }
                if(!(removed) && !(resolved)) {
                    sql += " AND ";
                }
                if(!resolved) {
                    sql += "resolved = false";
                }
            }
            ps = conn.prepareStatement(sql + ";");
            ps.setString(1, pseudo.toUpperCase());
            rs = ps.executeQuery();
            nb = rs.getInt("COUNT(*)");
        } catch (SQLException ex) {
            Main.logger.log(Level.SEVERE, ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Main.logger.log(Level.SEVERE, ex.getMessage());
            }
        }
        return nb;
    }
        
    public int getCountReportByReported(String reported, boolean removed, boolean resolved) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int nb = 0;
        try {
            conn = getSQLConnection();
            String sql = "SELECT COUNT(*) FROM " + table + " WHERE UPPER(reported) = ? ";
            if(!(removed) || !(resolved)) {
                sql += "AND ";
                if(!(removed)) {
                    sql += "removed = false";
                }
                if(!(removed) && !(resolved)) {
                    sql += " AND ";
                }
                if(!resolved) {
                    sql += "resolved = false";
                }
            }
            ps = conn.prepareStatement(sql + ";");
            ps.setString(1, reported.toUpperCase());
            rs = ps.executeQuery();
            nb = rs.getInt("COUNT(*)");
        } catch (SQLException ex) {
            Main.logger.log(Level.SEVERE, ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Main.logger.log(Level.SEVERE, ex.getMessage());
            }
        }
        return nb;
    }


    public int InsertReport(Report report) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("INSERT INTO " + table + " VALUES(null,?,?,?,?,?,?);");
            ps.setString(1, report.getSender());
            ps.setString(2, report.getReported());
            ps.setString(3, report.getReason());
            ps.setDate(4, new java.sql.Date(report.getDate().getTime()));
            ps.setBoolean(5, report.isResolved());
            ps.setBoolean(6, report.isRemoved());
            ps.execute();
        } catch (SQLException ex) {
            Main.logger.log(Level.SEVERE, ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Main.logger.log(Level.SEVERE, ex.getMessage());
            }
        }
        return 0;
    }
    
    public int UpdateReport(Report report) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("UPDATE " + table + " SET "
                    + "sender=?,"
                    + "reported=?,"
                    + "reason=?,"
                    + "date=?,"
                    + "resolved=?,"
                    + "removed=? WHERE id = ?;");
            ps.setString(1, report.getSender());
            ps.setString(2, report.getReported());
            ps.setString(3, report.getReason());
            ps.setDate(4, new java.sql.Date(report.getDate().getTime()));
            ps.setBoolean(5, report.isResolved());
            ps.setBoolean(6, report.isRemoved());
            ps.setInt(7, report.getId());
            ps.execute();
        } catch (SQLException ex) {
            Main.logger.log(Level.SEVERE, ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Main.logger.log(Level.SEVERE, ex.getMessage());
            }
        }
        return 0;
    }
    
    public int DeleteReport(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Report report = this.getReportById(id);
        if(report == null) {
            return -1;
        }
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("DELETE FROM  " + table +
                    " WHERE id = ?;");
            ps.setString(1, report.getSender());
            ps.setString(2, report.getReported());
            ps.setString(3, report.getReason());
            ps.setDate(4, new java.sql.Date(report.getDate().getTime()));
            ps.setBoolean(5, report.isResolved());
            ps.setBoolean(6, report.isRemoved());
            ps.setInt(7, report.getId());
            ps.execute();
        } catch (SQLException ex) {
            Main.logger.log(Level.SEVERE, ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Main.logger.log(Level.SEVERE, ex.getMessage());
            }
        }
        return 0;
    }
    
    public Report getReportById(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Report report = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE id = ?;");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getInt("id") == id) {
                    Report result = new Report ();
                    result.setId(rs.getInt("id"));
                    result.setSender(rs.getString("sender"));
                    result.setReported(rs.getString("reported"));
                    result.setReason(rs.getString("reason"));
                    result.setDate(new Date(rs.getDate("date").getTime()));
                    result.setResolved(rs.getBoolean("resolved"));
                    result.setRemoved(rs.getBoolean("removed"));
                    report = result;
                }
            }
        } catch (SQLException ex) {
            Main.logger.log(Level.SEVERE, ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Main.logger.log(Level.SEVERE, ex.getMessage());
            }
        }
        return report;
    }
    
    public int toggleResolved(int id) {Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Report report = this.getReportById(id);
        if(report == null) {
            return -1;
        }
        report.setResolved(!report.isResolved());
        UpdateReport(report);
        return 0;
    }
    
    public int toggleRemoved(int id) {Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Report report = this.getReportById(id);
        if(report == null) {
            return -1;
        }
        report.setRemoved(!report.isRemoved());
        UpdateReport(report);
        return 0;
    }
    
    public List<Report> GetReportByPseudo(String pseudo, int start, int limit, boolean removed, boolean resolved) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Report> report = new ArrayList<>();
        try {
            conn = getSQLConnection();
            String sql = "SELECT * FROM " + table + " WHERE UPPER(sender) = ? ";
            if((!(removed) || !(resolved))) {
                sql += "AND ";
                if(!removed) {
                    sql += "removed = false";
                }
                if((!(removed) && !(resolved))) {
                    sql += " AND ";
                }
                if(!resolved) {
                    sql += "resolved = false";
                }
            }
            ps = conn.prepareStatement(sql + " LIMIT " + start + "," + limit + ";");
            ps.setString(1, pseudo.toUpperCase());
            rs = ps.executeQuery();
            List<Report> reports = new ArrayList<>();
            while (rs.next()) {
                Report result = new Report ();
                result.setId(rs.getInt("id"));
                result.setSender(rs.getString("sender"));
                result.setReported(rs.getString("reported"));
                result.setReason(rs.getString("reason"));
                    result.setDate(new Date(rs.getDate("date").getTime()));
                result.setResolved(rs.getBoolean("resolved"));
                result.setRemoved(rs.getBoolean("removed"));
                reports.add(result);
            }
            report = reports;
        } catch (SQLException ex) {
            Main.logger.log(Level.SEVERE, ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Main.logger.log(Level.SEVERE, ex.getMessage());
            }
        }
        return report;
    }
    
    public List<Report> GetReportByReported(String reported, int start, int limit, boolean removed, boolean resolved) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Report> report = new ArrayList<>();
        try {
            conn = getSQLConnection();
            String sql = "SELECT * FROM " + table + " WHERE UPPER(reported) = ? ";
            if((!(removed) || !(resolved))) {
                sql += "AND ";
                if(!removed) {
                    sql += "removed = false";
                }
                if((!(removed) && !(resolved))) {
                    sql += " AND ";
                }
                if(!resolved) {
                    sql += "resolved = false";
                }
            }
            ps = conn.prepareStatement(sql + " LIMIT " + start + "," + limit + ";");
            ps.setString(1, reported.toUpperCase());
            rs = ps.executeQuery();
            List<Report> reports = new ArrayList<>();
            while (rs.next()) {
                Report result = new Report ();
                result.setId(rs.getInt("id"));
                result.setSender(rs.getString("sender"));
                result.setReported(rs.getString("reported"));
                result.setReason(rs.getString("reason"));
                    result.setDate(new Date(rs.getDate("date").getTime()));
                result.setResolved(rs.getBoolean("resolved"));
                result.setRemoved(rs.getBoolean("removed"));
                reports.add(result);
            }
            report = reports;
        } catch (SQLException ex) {
            Main.logger.log(Level.SEVERE, ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Main.logger.log(Level.SEVERE, ex.getMessage());
            }
        }
        return report;
    }
    
    public List<Report> getAllReports(int start, int limit, boolean removed, boolean resolved) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Report> report = new ArrayList<>();
        try {
            conn = getSQLConnection();
            String sql = "SELECT * FROM " + table + " ";
            if((!(removed) || !(resolved))) {
                sql += "WHERE ";
                if(!removed) {
                    sql += "removed = false";
                }
                if((!(removed) && !(resolved))) {
                    sql += " AND ";
                }
                if(!resolved) {
                    sql += "resolved = false";
                }
            }
            ps = conn.prepareStatement(sql + " LIMIT " + start + "," + limit + ";");
            rs = ps.executeQuery();
            List<Report> reports = new ArrayList<>();
            while (rs.next()) {
                Report result = new Report ();
                result.setId(rs.getInt("id"));
                result.setSender(rs.getString("sender"));
                result.setReported(rs.getString("reported"));
                result.setReason(rs.getString("reason"));
                    result.setDate(new Date(rs.getDate("date").getTime()));
                result.setResolved(rs.getBoolean("resolved"));
                result.setRemoved(rs.getBoolean("removed"));
                reports.add(result);
            }
            report = reports;
        } catch (SQLException ex) {
            Main.logger.log(Level.SEVERE, ex.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Main.logger.log(Level.SEVERE, ex.getMessage());
            }
        }
        return report;
    }


    public void close(PreparedStatement ps, ResultSet rs) {
        try {
            if (ps != null) {
                ps.close();
            }
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException ex) {
            Main.logger.log(Level.SEVERE, ex.getMessage());
        }
    }
}
