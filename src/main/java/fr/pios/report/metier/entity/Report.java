package fr.pios.report.metier.entity;

import java.util.Date;

/**
 *
 * @author Molzonas
 */
public class Report {
    private int id;
    private String sender;
    private String reported;
    private String reason;
    private Date date;
    private boolean resolved;
    private boolean removed;

    public Report() {
    }

    public Report(int id, String sender, String reported, String reason, Date date, boolean resolved, boolean removed) {
        this.sender = sender;
        this.reported = reported;
        this.reason = reason;
        this.date = date;
        this.resolved = resolved;
        this.removed = removed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReported() {
        return reported;
    }

    public void setReported(String reported) {
        this.reported = reported;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }
}
