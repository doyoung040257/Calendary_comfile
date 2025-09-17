package lg;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import todo.todoList;

public class UserDatabase {
    public static Map<String, User> userDatabase = new HashMap<>();
    private static final String FILE_NAME = "users.dat";
    private static final long serialVersionUID = 1L;

    public static void addUser(User user) {
        userDatabase.put(user.getId(), user);
        saveUsers();
    }

    public static User getUser(String id) {
        loadUsers(); // 최신 데이터 불러오기
        return userDatabase.get(id);
    }
    
    public static void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(userDatabase);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static void loadUsers() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            userDatabase = new HashMap<>();
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            userDatabase = (Map<String, User>) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            userDatabase = new HashMap<>();
        }
    }

}

