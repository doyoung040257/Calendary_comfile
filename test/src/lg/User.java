package lg;

import java.io.Serializable;

import todo.todoListMake;

public class User implements Serializable {
    private String id;
    private String password;
    private String name;
    private String birth;
    private String gender;
    private String email;
    private todoListMake todolist = new todoListMake();

    public User(String id, String password, String name, String birth, String gender, String email) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.birth = birth;
        this.gender = gender;
        this.email = email;
    }

    public String getId() { return id; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getBirth() { return birth; }
    public String getGender() { return gender; }
    public String getEmail() { return email; }

    // Setter
    public void setName(String name) { this.name = name; }
    public void setBirth(String birth) { this.birth = birth; }
    public void setGender(String gender) { this.gender = gender; }
    public void setEmail(String email) { this.email = email; }
}



