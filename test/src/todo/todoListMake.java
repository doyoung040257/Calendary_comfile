package todo;

import java.util.ArrayList;
import java.util.List;

public class todoListMake {

	private List<todoList> todolist = new ArrayList<>();
	
	public void addTodo(String work, String day, String time, String note) {
		todoList onetodo = new todoList(work, day, time, note);
		todolist.add(onetodo);
	}
	
	public List<todoList> getTodolist(){
		return todolist;
	}
	
	@Override
	public String toString() {
	    return "TodoList: " + todolist; // todolist가 ArrayList라면 요소들이 문자열로 출력됨
	}

}
