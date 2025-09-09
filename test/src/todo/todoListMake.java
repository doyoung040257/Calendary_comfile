package todo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class todoListMake implements Serializable {

	private List<todoList> todolist = new ArrayList<>();
	
	public void addTodo(String work, String day, String time, String note, int importance) {
		todoList onetodo = new todoList(work, day, time, note, importance);
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
