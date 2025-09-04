package todo;


public class todo_list{

	private String work;
    private String day;
    private Integer time;
    private String note;
    
    public todo_list(String work, String day, Integer time, String note) {
    	this.work = work;
    	this.day = day;
    	this.time = time;
    	this.note = note;
    }



	public void setWork(String work) {
		this.work = work;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public void setTime(Integer time) {
		this.time = time;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getWork() {
		return work;
	}
	
	public String getDay() {
		return day;
	}

	public Integer getTime() {
		return time;
	}

	public String getNote() {
		return note;
	}
	
    @Override
    public String toString() {
        return "[할 일: " + work +
               ", 날짜: " + day +
               ", 시간: " + time +
               ", 메모: " + note + "]";
    }

    
    
}



//할 일 추가 완료 후 리스트 부분에 할일 추가
//특정 갯수가 넘어가면 스크롤바 생김
//리스트에 추가된 할 일 누르면 수정창 표시
