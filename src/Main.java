import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Properties;
import java.util.Map;

/**
 * This class is the start class of the program.
 *
 * @version 1.2
 */
public class Main {
    private static final String VERSION = "1.2";
    private static Profile currentProfile;
    private static JFrame frame;
    private static JMenuBar menuBar;

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> startGUI());
    }

    private static JMenu buildAboutMenu() {
        final JMenu aboutMenu = new JMenu("Info");
        aboutMenu.setMnemonic(KeyEvent.VK_A);

        final JMenuItem aboutItem = new JMenuItem("About");

        aboutMenu.add(aboutItem);

        aboutItem.addActionListener(theEvent ->
                JOptionPane.showMessageDialog(null, """
                        This app is registered to: """ + " " + (currentProfile != null ? currentProfile.getUserName() : "No profile selected") +
                        "\n" + """
                        This app provided by: TeamOfFive \n
                        Members:
                        Tyler Cairney
                        Lucas Jeong
                        Reece Hoisington
                        Hanqing Tao
                        Sage Robbins
                        Version: """ + VERSION));

        return aboutMenu;
    }

    private static JMenu buildProfileMenu() {
        final JMenu profileMenu = new JMenu("Profiles");
        profileMenu.setMnemonic(KeyEvent.VK_P);

        final JMenuItem existingProfileItem = new JMenuItem("Select a profile");
        final JMenuItem addProfileItem = new JMenuItem("Add a profile");
        final JMenuItem deleteProfileItem = new JMenuItem("Delete a profile");
        final JMenuItem importProfileItem = new JMenuItem("Import Profiles");
        final JMenuItem exportProfileItem = new JMenuItem("Export Profiles");

        addProfileItem.addActionListener(theEvent -> {
            JTextField username = new JTextField(20);
            JTextField email = new JTextField(20);
            JRadioButton publicOption = new JRadioButton("Public", true);
            JRadioButton privateOption = new JRadioButton("Private");
            ButtonGroup group = new ButtonGroup();
            group.add(publicOption);
            group.add(privateOption);
            JTextField pin = new JTextField(4);
            pin.setEnabled(false);

            privateOption.addItemListener(e -> pin.setEnabled(privateOption.isSelected()));

            JPanel panel = new JPanel(new GridLayout(0, 1));

            panel.add(new JLabel("Enter Username: "));
            panel.add(username);
            panel.add(new JLabel("Enter Email: "));
            panel.add(email);
            panel.add(new JLabel("Select Profile Type: "));
            panel.add(publicOption);
            panel.add(privateOption);
            panel.add(new JLabel("Enter PIN (for private profiles): "));
            panel.add(pin);

            int result = JOptionPane.showConfirmDialog(null, panel, "Create Profile",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                if (!username.getText().isEmpty() && !email.getText().isEmpty()) {
                    boolean isPublic = publicOption.isSelected();
                    String pinValue = isPublic ? "" : pin.getText();
                    Profile profile = new Profile(username.getText(), email.getText(), isPublic, pinValue);
                    Profile.addProfile(username.getText(), profile);
                    currentProfile = profile;  // Update the current profile
                    System.out.println("Profiles: " + Profile.getProfiles());
                    updateMenuBar(); // Show the projects menu
                } else {
                    JOptionPane.showMessageDialog(null, "Username and Email cannot be empty.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        existingProfileItem.addActionListener(theEvent -> {
            Map<String, Profile> profiles = Profile.getProfiles();
            if (profiles.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No profiles available.\n" +
                                "\nPlease create a new profile or import existing profiles.",
                        "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String[] profileNames = profiles.keySet().toArray(new String[0]);
            String selectedProfile = (String) JOptionPane.showInputDialog(null, "Select a profile",
                    "Profiles", JOptionPane.PLAIN_MESSAGE, null, profileNames, profileNames[0]);

            if (selectedProfile != null) {
                Profile profile = profiles.get(selectedProfile);
                if (!profile.isPublic()) {
                    String pin = JOptionPane.showInputDialog("Enter PIN for private profile:");
                    if (profile.checkPin(pin)) {
                        currentProfile = profile;
                        JOptionPane.showMessageDialog(null, "Profile changed to: "
                                        + currentProfile.getUserName(),
                                "Info", JOptionPane.INFORMATION_MESSAGE);
                        updateMenuBar();
                    } else {
                        JOptionPane.showMessageDialog(null, "Incorrect PIN.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    currentProfile = profile;
                    JOptionPane.showMessageDialog(null, "Profile changed to: "
                                    + currentProfile.getUserName(),
                            "Info", JOptionPane.INFORMATION_MESSAGE);
                    updateMenuBar();
                }
            }
        });

        deleteProfileItem.addActionListener(theEvent -> {
            Map<String, Profile> profiles = Profile.getProfiles();
            if (profiles.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No profiles available to delete.",
                        "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String[] profileNames = profiles.keySet().toArray(new String[0]);
            String selectedProfile = (String) JOptionPane.showInputDialog(null, "Select a profile to delete",
                    "Delete Profile", JOptionPane.PLAIN_MESSAGE, null, profileNames, profileNames[0]);

            if (selectedProfile != null) {
                int confirm = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to delete the profile: " + selectedProfile + "?",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    Profile.deleteProfile(selectedProfile);
                    JOptionPane.showMessageDialog(null, "Profile deleted.");
                    if (currentProfile != null && currentProfile.getUserName().equals(selectedProfile)) {
                        currentProfile = null;
                    }
                    updateMenuBar();
                }
            }
        });

        importProfileItem.addActionListener(theEvent -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    Profile.importProfiles(file);
                    JOptionPane.showMessageDialog(null, "Profiles imported");
                    currentProfile = null;
                    updateMenuBar();
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Failed to import profiles.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        exportProfileItem.addActionListener(theEvent -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showSaveDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    Profile.exportProfiles(file);
                    JOptionPane.showMessageDialog(null, "Profiles exported");
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Failed to export profiles.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        profileMenu.add(existingProfileItem);
        profileMenu.addSeparator();
        profileMenu.add(addProfileItem);
        profileMenu.addSeparator();
        profileMenu.add(deleteProfileItem);
        profileMenu.addSeparator();
        profileMenu.add(importProfileItem);
        profileMenu.addSeparator();
        profileMenu.add(exportProfileItem);

        return profileMenu;
    }

    private static JMenu buildProjectsMenu() {
        final JMenu projectsMenu = new JMenu("Projects");
        projectsMenu.setMnemonic(KeyEvent.VK_R);

        final JMenuItem addProjectItem = new JMenuItem("Add Project");
        final JMenuItem viewProjectsItem = new JMenuItem("View Projects");

        addProjectItem.addActionListener(theEvent -> {
            // TODO Implement
        });

        viewProjectsItem.addActionListener(theEvent -> {
            // TODO Implement
        });

        projectsMenu.add(addProjectItem);
        projectsMenu.addSeparator();
        projectsMenu.add(viewProjectsItem);

        return projectsMenu;
    }

    private static void startGUI() {
        frame = new JFrame("Iteration 2");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 600);

        menuBar = new JMenuBar();
        menuBar.add(buildAboutMenu());
        menuBar.add(buildProfileMenu());
        frame.setJMenuBar(menuBar);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(0, 0, 0));

        JLabel welcomeLabel = new JLabel("DIY Project Manager", SwingConstants.CENTER);
        welcomeLabel.setForeground(Color.ORANGE);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 36));
        mainPanel.add(welcomeLabel, BorderLayout.CENTER);

        frame.add(mainPanel, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void updateMenuBar() {
        menuBar.removeAll();
        menuBar.add(buildAboutMenu());
        menuBar.add(buildProfileMenu());
        if (currentProfile != null) {
            menuBar.add(buildProjectsMenu());
        }
        menuBar.revalidate();
        menuBar.repaint();
    }
}