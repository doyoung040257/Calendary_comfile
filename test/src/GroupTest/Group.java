package GroupTest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Group implements Serializable {
    private final String id;
    private String name;
    private List<String> members;

    // ★ MODIFIED: 이벤트 관련 필드 추가
    private List<String> events; // 그룹 전체 이벤트
    private Map<String, List<String>> memberEvents; // 멤버별 이벤트

    public Group(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.members = new ArrayList<>();
        this.events = new ArrayList<>(); // ★ MODIFIED 초기화
        this.memberEvents = new HashMap<>(); // ★ MODIFIED 초기화
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getMembers() { return members; }
    public void addMember(String member) { 
        members.add(member); 
        memberEvents.putIfAbsent(member, new ArrayList<>()); // ★ MODIFIED 멤버별 이벤트 초기화
    }
    public void removeMember(String member) { members.remove(member); }

    // ★ MODIFIED: 그룹 전체 이벤트 getter
    public List<String> getEvents() { return events; }

    // ★ MODIFIED: 멤버별 이벤트 getter
    public List<String> getMemberEvents(String member) {
        return memberEvents.getOrDefault(member, new ArrayList<>());
    }

    @Override
    public String toString() {
        return "[그룹명: " + name + ", 멤버수: " + members.size() + "]";
    }
}
