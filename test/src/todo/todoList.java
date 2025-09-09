package todo;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class todoList implements Serializable{

	private String work;
    private String day;
    private String time;
    private String note;
    private int importance;
    
    private final String id;
    
    public todoList(String work, String day, String time, String note, int importance) {
    	this.work = work;
    	this.day = day;
    	this.time = time;
    	this.note = note;
    	this.importance	= importance;
    	this.id = UUID.randomUUID().toString(); // 생성자마다 고유 ID 부여
    }
	public void setWork(String work) {
		this.work = work;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public void setImportance(int importance) {
		this.importance = importance;
	}
	public String getWork() {
		return work;
	}
	public String getDay() {
		return day;
	}
	public String getTime() {
		return time;
	}
	public String getNote() {
		return note;
	}
	public int getImportance() {
		return importance;
	}
	public String getId() {
		return id;
	}
	
	@Override
	public boolean equals(Object obj) {
	    if (this == obj) return true;
	    if (!(obj instanceof todoList)) return false;
	    todoList other = (todoList) obj;
	    return this.id.equals(other.id);
	}

	@Override
	public int hashCode() {
	    return Objects.hash(id);
	}
}
