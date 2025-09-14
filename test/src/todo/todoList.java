package todo;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class todoList implements Serializable{

	private String group;
	private String work;
    private String day;
    private String time;
    private String note;
    private int importance;
    
    private final String id;
    
    public todoList(String group, String work, String day, String time, String note, int importance) {
    	this.group = group;
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
	public void setGroup(String group) {
		this.group = group;
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
	public String getGroup() {
		return group;
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
	
    @Override
    public String toString() {
        return "[할 일: " + work + ", 날짜: " + day + ", 시간: " + time +
               ", 메모: " + note + ", 중요도: " + importance + "]";
    }
}
