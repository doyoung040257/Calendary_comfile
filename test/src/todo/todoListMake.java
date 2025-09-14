package todo;

import java.io.Serializable;
import java.util.ArrayList;

public class todoListMake implements Serializable {

	private ArrayList<todoList> todolist = new ArrayList<>();
	
	public void addTodo(String group, String work, String day, String time, String note, int importance) {
		todoList onetodo = new todoList(group, work, day, time, note, importance);
		todolist.add(onetodo);
	}
	
	
    public void setTodolist(ArrayList<todoList> list) {
        todolist = list;
    }
    
	
	public ArrayList<todoList> getTodolist() {
		return todolist;
	}


	//리스트 초기화
	public void clearAllTodos() {
	    todolist.clear();
	}
	
	@Override
	public String toString() {
	    return "TodoList: " + todolist; // todolist가 ArrayList라면 요소들이 문자열로 출력됨
	}
}
