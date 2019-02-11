package com.avob.openadr.server.oadr20b.vtn.models.venreport.request;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import com.avob.openadr.model.oadr20b.ei.ReadingTypeEnumeratedType;

/**
 * @author bzanni
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class ReportRequest {

	/**
	 * Autogenerated unique id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;

	private String reportRequestId;

	private String granularity;

	private String reportBackDuration;

	private Long start;

	private Long end;

	private ReadingTypeEnumeratedType readingType;

	private boolean acked = false;

	public String getReportRequestId() {
		return reportRequestId;
	}

	public void setReportRequestId(String reportRequestId) {
		this.reportRequestId = reportRequestId;
	}

	public String getGranularity() {
		return granularity;
	}

	public void setGranularity(String granularity) {
		this.granularity = granularity;
	}

	public String getReportBackDuration() {
		return reportBackDuration;
	}

	public void setReportBackDuration(String reportBackDuration) {
		this.reportBackDuration = reportBackDuration;
	}

	public Long getStart() {
		return start;
	}

	public void setStart(Long start) {
		this.start = start;
	}

	public Long getEnd() {
		return end;
	}

	public void setEnd(Long end) {
		this.end = end;
	}

	public ReadingTypeEnumeratedType getReadingType() {
		return readingType;
	}

	public void setReadingType(ReadingTypeEnumeratedType readingType) {
		this.readingType = readingType;
	}

	public boolean isAcked() {
		return acked;
	}

	public void setAcked(boolean acked) {
		this.acked = acked;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
