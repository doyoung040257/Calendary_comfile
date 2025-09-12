package GroupTest;

import javax.swing.*;
import java.awt.*;
import lg.User;
import lg.UserDatabase;

public class GroupListMake extends JFrame {

    private final GroupList groupList;
    private final User user;

    public GroupListMake(User user, GroupList groupList) {
        this.user = user;
        this.groupList = groupList;
        initUI();
    }

    private void initUI() {
        setTitle("그룹 생성");
        setSize(350, 200);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel label = new JLabel("그룹 이름 입력:", JLabel.CENTER);
        label.setBounds(50, 20, 250, 30);
        add(label);

        JTextField groupNameField = new JTextField();
        groupNameField.setBounds(50, 60, 250, 30);
        add(groupNameField);

        JButton createButton = new JButton("생성");
        createButton.setBounds(50, 110, 100, 30);
        add(createButton);

        JButton cancelButton = new JButton("취소");
        cancelButton.setBounds(200, 110, 100, 30);
        add(cancelButton);

        createButton.addActionListener(e -> {
            String groupName = groupNameField.getText().trim();
            if (groupName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "그룹 이름을 입력하세요.");
                return;
            }
            
            // ★ MODIFIED: 그룹 생성 시 그룹장 지정
            Group newGroup = new Group(groupName, user.getId());
            groupList.addGroup(newGroup);

            // ★ MODIFIED: User/UserDatabase 저장 시 setGroups → setGroupList로 변경
            user.setGroupList(groupList); // User 클래스에 추가한 setter 사용
            UserDatabase.userDatabase.put(user.getId(), user);
            UserDatabase.saveUsers();

            JOptionPane.showMessageDialog(this, "그룹 생성 완료: " + groupName);
            dispose();
        });

        cancelButton.addActionListener(e -> dispose());

        setVisible(true);
    }
}
