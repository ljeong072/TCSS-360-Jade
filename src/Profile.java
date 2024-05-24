import java.io.*;
import java.util.*;
/**
 * This class implements profile functionalities by able to implement import/export on User data/settings.
 *
 * @version 1.2
 */
public class Profile {
    private static Map<String, Profile> myProfiles = new HashMap<>();
    private Map<String, Project> myProjects = new HashMap<>();
    private String myUserName = "Default user";
    private String myEmail = "None";
    private boolean isPublic = true;
    private String pin = "";

    /**
     * The Profile class' constructor with set fields.
     *
     * @param theUsername the user's username
     * @param theEmail    the user's email
     */
    Profile(String theUsername, String theEmail, boolean theIsPublic, String thePin) {
        myUserName = theUsername;
        myEmail = theEmail;
        isPublic = theIsPublic;
        pin = thePin;
    }

    /**
     * Get method that returns the current user's name.
     *
     * @return this Profile's name
     */
    String getUserName() {
        return myUserName;
    }

    /**
     * Get method that returns the current user's email.
     *
     * @return this Profile's email
     */
    String getEmail() {
        return myEmail;
    }

    /**
     * Get method that returns the current profile's accessibility (public).
     *
     * @return this Profile's access
     */
    boolean isPublic() {
        return isPublic;
    }

    /**
     * Get method that returns the current profile's pin (private).
     *
     * @return this Profile's pin
     */
    boolean checkPin(String inputPin) {
        return pin.equals(inputPin);
    }


    /**
     * Stores a new profile into the map of the profile class.
     *
     * @param theUserName the name of the profile, which acts as the map's key
     * @param theProfile  the profile to be stored in the map
     */
    static void addProfile(String theUserName, Profile theProfile) {
        myProfiles.put(theUserName, theProfile);
    }

    /**
     * Deletes a profile from the map of the profile class.
     *
     * @param theUserName the name of the profile to be deleted
     */
    static void deleteProfile(String theUserName) {
        myProfiles.remove(theUserName);
    }

    static Map<String, Profile> getProfiles() {
        return myProfiles;
    }

    /**
     * Import settings to this profile from an input file.
     *
     * @param theFile the input settings.
     */
    public static void importProfiles(final File theFile) {
        Properties properties = new Properties();

        try (FileInputStream input = new FileInputStream(theFile)) {
            properties.load(input);
            myProfiles.clear();

            for (String key : properties.stringPropertyNames()) {
                String[] userProfile = properties.getProperty(key).split(",");
                String userName = userProfile[0];
                String email = userProfile[1];
                boolean isPublic = Boolean.parseBoolean(userProfile[2]);
                String pin = "";

                if (userProfile.length > 3) {
                    pin = userProfile[3];
                }

                myProfiles.put(key, new Profile(userName, email, isPublic, pin));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void exportProfiles(final File theFile) {
        Properties properties = new Properties();
        for (Map.Entry<String, Profile> entry : myProfiles.entrySet()) {
            Profile profile = entry.getValue();
            properties.setProperty(entry.getKey(), profile.getUserName() + "," +
                profile.getEmail() + "," + profile.isPublic() + "," + profile.pin);
        }
        try (FileOutputStream output = new FileOutputStream(theFile)) {
            properties.store(output, "Profiles");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Project management methods (use later)

    public void addProject(Project project) {
        myProjects.put(project.getName(), project);
    }

    public Map<String, Project> getProjects() {
        return myProjects;
    }

    /**
     * @return a String representation of the Profile
     */
    @Override
    public String toString() {
        return "User: " + myUserName + "\nEmail: " + myEmail;
    }
}