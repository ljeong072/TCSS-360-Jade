import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

/**
 * @author Reece Hoisington
 */
public class ProfileGUI extends JPanel implements ListSelectionListener {
    private int SP_X_OFFSET = 20;
    private int MSP_Y_OFFSET = 50;
    private int DSP_Y_OFFSET = 300;
    private int SP_WIDTH = 350;
    private int SP_HEIGHT = 200;
    private int TEXT_HEIGHT = 50;

    private int SEL_X_OFFSET = 400;
    private int SEL_Y_OFFSET = 100;
    private int SEL_WIDTH = 250;

    private final Color BACKGROUND_COLOR = new Color(40, 45, 55);
    private final Color TEXT_COLOR = new Color(255, 255, 255);

    private Profile myProfile;

    private JLabel mLabel = new JLabel("Bookmarked Projects");
    private DefaultListModel<String> mList = new DefaultListModel<>();
    private JList<String> mJList = new JList<>(mList);
    private JScrollPane mSP = new JScrollPane(mJList);

    private JLabel dLabel = new JLabel("Other Projects");
    private DefaultListModel<String> dList = new DefaultListModel<>();
    private JList<String> dJList = new JList<>(dList);
    private JScrollPane dSP = new JScrollPane(dJList);

    private JLabel projectName = new JLabel("", SwingConstants.CENTER);
    private JButton selectButton = new JButton("Create");
    private JButton markButton = new JButton("Bookmark");


    boolean marked = false;
    int index = 0;
    boolean selectedAdd = true;

    ProfileGUI(Profile theProfile) {
        super();
        myProfile = theProfile;
        projectName.setText("Welcome, " + myProfile.getUserName());


        this.setLayout(null);
        setBackground(BACKGROUND_COLOR);

        mJList.addListSelectionListener(this);
        dJList.addListSelectionListener(this);

        listProjects();

        mList.addElement("Add Project");
        dList.addElement("Add Project");

        selectButton.addActionListener(theEvent -> selectPressed());
        markButton.addActionListener(theEvent -> markPressed());
        markButton.setEnabled(false);

        addSelectedPanelElements();
        addMarkedPanelElements();
        addDefaultPanelElements();

    }

    private void listProjects() {
        for (Project p : myProfile.getProjects().values()) {
            if (p.bookMarked){
                mList.addElement(p.getName());
            } else {
                dList.addElement(p.getName());
            }
        }

    }


    private void addSelectedPanelElements() {
        projectName.setBounds(SEL_X_OFFSET, SEL_Y_OFFSET, SEL_WIDTH, 100);
        projectName.setForeground(TEXT_COLOR);
        projectName.setFont(new Font("Verdana", Font.BOLD, 20));

        selectButton.setBounds(SEL_X_OFFSET, SEL_Y_OFFSET + 150, SEL_WIDTH, 50);

        markButton.setBounds(SEL_X_OFFSET, SEL_Y_OFFSET + 250, SEL_WIDTH, 50);

        this.add(projectName);
        this.add(selectButton);
        this.add(markButton);
    }

    private void addMarkedPanelElements() {
        mLabel.setForeground(TEXT_COLOR);
        mLabel.setFont(new Font("Verdana", Font.BOLD, 18));
        mLabel.setBounds(SP_X_OFFSET, MSP_Y_OFFSET - TEXT_HEIGHT, SP_WIDTH, TEXT_HEIGHT);
        mSP.setBounds(SP_X_OFFSET, MSP_Y_OFFSET, SP_WIDTH, SP_HEIGHT);
        mSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(mLabel);
        this.add(mSP);
    }

    private void addDefaultPanelElements() {
        dLabel.setForeground(TEXT_COLOR);
        dLabel.setFont(new Font("Verdana", Font.BOLD, 18));
        dLabel.setBounds(SP_X_OFFSET, DSP_Y_OFFSET - TEXT_HEIGHT, SP_WIDTH, TEXT_HEIGHT);
        dSP.setBounds(SP_X_OFFSET, DSP_Y_OFFSET, SP_WIDTH, SP_HEIGHT);
        dSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        dSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(dLabel);
        this.add(dSP);
    }

    private void selectPressed() {
        if (selectedAdd) {
            JTextField projectName = new JTextField(20);

            JPanel panel = new JPanel(new GridLayout(0, 1));

            panel.add(new JLabel("Enter project name: "));
            panel.add(projectName);

            int result = JOptionPane.showConfirmDialog(null, panel, "Create Project",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                if (!projectName.getText().isEmpty()) {
                    String newName = projectName.getText();
                    if (myProfile.getProjects().containsKey(newName)) {
                        JOptionPane.showMessageDialog(null, "Project name already in use.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        DefaultListModel<String> temp = getList(marked);
                        Project newProj = new Project(newName, marked);
                        myProfile.addProject(newProj);
                        temp.insertElementAt(newProj.getName(), temp.size()-1);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Project name cannot be empty.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            // Pull up projects menu when finished
        }
    }

    private void markPressed() {
        mJList.removeListSelectionListener(this);
        dJList.removeListSelectionListener(this);
        int tempInd = index;
        DefaultListModel<String> current = getList(marked);
        DefaultListModel<String> other = getList(!marked);


        other.insertElementAt(current.getElementAt(tempInd), other.size()-1);


        mJList.clearSelection();
        dJList.clearSelection();

        current.remove(tempInd);

        index = other.size()-2;
        marked = !marked;
        Project selectedProject = myProfile.getProjects().get(other.getElementAt(index));
        selectedProject.bookMarked = !selectedProject.bookMarked;
        mJList.addListSelectionListener(this);
        dJList.addListSelectionListener(this);
    }

    private DefaultListModel<String> getList(boolean right) {
        DefaultListModel<String> temp;
        if (right) {
            temp = mList;
        } else {
            temp = dList;
        }
        return temp;
    }

    private void updateSelected(){
        DefaultListModel<String> temp = getList(marked);
        if (marked) {
            markButton.setText("Unmark");
        } else {
            markButton.setText("Bookmark");
        }

        if (index == temp.size() - 1) {
            selectedAdd = true;
            selectButton.setText("Create");
            markButton.setEnabled(false);
        } else {
            selectedAdd = false;
            selectButton.setText("View");
            markButton.setEnabled(true);
        }

        projectName.setText(temp.get(index));
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (mJList.equals(e.getSource()) &&
            (!marked || mJList.getSelectedIndex() != index)) {
            marked = true;
            index = mJList.getSelectedIndex();

            dJList.removeListSelectionListener(this);
            dJList.clearSelection();
            dJList.addListSelectionListener(this);

            updateSelected();
        } else if (dJList.equals(e.getSource()) &&
            (marked || dJList.getSelectedIndex() != index)) {
            marked = false;
            index = dJList.getSelectedIndex();

            mJList.removeListSelectionListener(this);
            mJList.clearSelection();
            mJList.addListSelectionListener(this);

            updateSelected();
        }
    }

    // TODO allow for project removal
}
