package todo;

import java.util.ArrayList;
import java.util.List;

public class makeList {

	private List<todo_list> todolist = new ArrayList<>();
	
	public void addTodo(String work, String day, Integer time, String note) {
		todo_list onetodo = new todo_list(work, day, time, note);
		todolist.add(onetodo);
	}
	
	public List<todo_list> getTodolist(){
		return todolist;
	}
	
	@Override
	public String toString() {
	    return "TodoList: " + todolist; // todolist가 ArrayList라면 요소들이 문자열로 출력됨
	}

}
