package at.woodstick.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name=HistoryLog.TABLE_NAME)
@SequenceGenerator(name="hgen", sequenceName="history_seq", allocationSize=1)
public class HistoryLog {

	public static final String TABLE_NAME = "history_log";
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="hgen")
	private long id;
	
	@Column
	private String logValue;
	
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;
	
	public HistoryLog() {
	}

	public HistoryLog(String logValue, Date createdAt) {
		this.logValue = logValue;
		this.createdAt = createdAt;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLogValue() {
		return logValue;
	}

	public void setLogValue(String logValue) {
		this.logValue = logValue;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public static HistoryLog newEntry(String logValue, Date createdAt) {
		return new HistoryLog(logValue, createdAt);
	}
}
