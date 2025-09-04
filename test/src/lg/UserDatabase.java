package lg;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserDatabase {
    public static Map<String, User> userDatabase = new HashMap<>();
    private static final String FILE_NAME = "users.dat";

    public static void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(userDatabase);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static void loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            userDatabase = (Map<String, User>) ois.readObject();
        } catch (Exception e) {
            userDatabase = new HashMap<>();
        }
    }
}
