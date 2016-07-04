package org.comparer.datamodel;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The class is used by the webservice as representation of the data that
 * it will receive.
 * @author Marijn Scholtens & Bob Reimink
 *
 */
@XmlRootElement
public class EnergyWrapper {
	
	private List<EnergyRecord> er;
	private int id;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	@XmlElementWrapper(name = "er")
	@XmlElement(name = "record")
	public List<EnergyRecord> getEr() {
		return er;
	}
	public void setEr(List<EnergyRecord> er) {
		this.er = er;
	}
}
