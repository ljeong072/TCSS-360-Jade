import java.util.ArrayList;
import java.util.List;

public class Project {
    private String name;
    boolean bookMarked = false;

    public Project(String name) {
        this.name = name;
    }
    public Project(String name, boolean bookMarked) {
        this.name = name;
        this.bookMarked = bookMarked;
    }

    public String getName() {
        return name;
    }
}