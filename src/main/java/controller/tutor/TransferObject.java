package controller.tutor;

import java.io.Serializable;
import models.Map;

@SuppressWarnings("serial")
public class TransferObject implements Serializable {

	private Map map;
	private String code;
	private int id;
	private boolean parsed;
	
	public TransferObject(String text, Map map) {
		this.code = text;
		this.map = map;
		this.parsed = false;
	}
	
	public Map getMap() {
		return this.map;
	}
	
	public String getCode() {
		return this.code;
	}
	
	public void setCode(String s) {
		this.code = s;
	}
	
	public void setMap(Map m) {
		this.map = m;
	}
	
	public void setID(int id) {
		this.id = id;
	}
	
	public int getID() {
		return this.id;
	}
	
	public boolean isParsed() {
		return this.parsed;
	}
	
	public void parse() {
		this.parsed = true;
	}
	
}