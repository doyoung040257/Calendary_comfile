package todo;

public class todoList{

	private String work;
    private String day;
    private String time;
    private String note;
    private int importance;
    
    public todoList(String work, String day, String time, String note, int importance) {
    	this.work = work;
    	this.day = day;
    	this.time = time;
    	this.note = note;
    	this.importance	= importance;
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
}
