package GroupTest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupList implements Serializable {
    private List<Group> groups = new ArrayList<>();

    public void addGroup(Group group) {
        groups.add(group);
    }

    public void removeGroup(String groupId) {
        groups.removeIf(g -> g.getId().equals(groupId));
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public Group getGroupById(String id) {
        for (Group g : groups) {
            if (g.getId().equals(id)) return g;
        }
        return null;
    }

    // ★ MODIFIED: 이름으로 그룹 찾기
    public Group getGroupByName(String name) { 
        for (Group g : groups) {
            if (g.getName().equals(name)) return g;
        }
        return null;
    }

    public boolean isEmpty() {
        return groups.isEmpty();
    }
}
