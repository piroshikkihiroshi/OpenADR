package com.avob.openadr.server.oadr20b.vtn.models.venpoll;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.avob.openadr.server.common.vtn.models.ven.Ven;

/**
 * 
 * @author bertrand
 *
 */
@Entity
@Table(name = "venpoll")
public class VenPoll {

	/**
	 * Autogenerated unique id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	/**
	 * Related ven
	 */
	@ManyToOne
	@JoinColumn(name = "ven_id")
	private Ven ven;

	private Long createdTimestamp;

	@Lob
	private String message;

	public VenPoll() {
	}

	public VenPoll(Ven ven, String message) {
		this.ven = ven;
		this.message = message;
		this.createdTimestamp = System.currentTimeMillis();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Ven getVen() {
		return ven;
	}
	
	public String getMessage() {
		return message;
	}

	public Long getCreatedTimestamp() {
		return createdTimestamp;
	}

}
