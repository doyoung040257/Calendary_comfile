package lg;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import todo.todoListMake;
import frame.CalendarFrame01;

public class User implements Serializable {
    private String id;
    private String password;
    private String name;
    private String birth;
    private String gender;
    private String email;
    private todoListMake todolist;
    private Map<LocalDate, List<CalendarFrame01.TodoEntry>> dailyTasks = new HashMap<>();
    private Map<LocalDate, String> dailyReviews = new HashMap<>();

    private boolean notificationsEnabled = true; // ✅ 추가
    
	public User(String id, String password, String name, String birth, String gender, String email) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.birth = birth;
        this.gender = gender;
        this.email = email;
        this.todolist = new todoListMake();
    }
	
	

    public String getId() { return id; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getBirth() { return birth; }
    public String getGender() { return gender; }
    public String getEmail() { return email; }
    public todoListMake getTodolist() { return todolist; }
    public Map<LocalDate, List<CalendarFrame01.TodoEntry>> getDailyTasks() {
    	return dailyTasks;
    }
    

    public boolean isNotificationsEnabled() { return notificationsEnabled; }  // ✅ 추가
    public void setNotificationsEnabled(boolean enabled) { this.notificationsEnabled = enabled; } // ✅ 추가


    // Setter
    public void setName(String name) { this.name = name; }
    public void setBirth(String birth) { this.birth = birth; }
    public void setGender(String gender) { this.gender = gender; }
    public void setEmail(String email) { this.email = email; }
	public void setTodolist(todoListMake todolist) { this.todolist = todolist; }
	public Map<LocalDate, String> getDailyReviews() {
	    return dailyReviews;
	}


}


