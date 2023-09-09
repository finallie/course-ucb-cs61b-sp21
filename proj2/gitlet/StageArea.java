package gitlet;

import java.io.Serializable;
import java.util.TreeMap;
import java.util.TreeSet;

import static gitlet.Repository.INDEX_FILE;
import static gitlet.Utils.*;

public class StageArea implements Serializable {

    private final TreeMap<String, String> addStage;
    private final TreeSet<String> removeStage;


    private StageArea() {
        addStage = new TreeMap<>();
        removeStage = new TreeSet<>();
    }

    public static StageArea getInitSnapshot() {
        return new StageArea();
    }

    public static StageArea getCurrentSnapshot() {
        return readObject(INDEX_FILE, StageArea.class);
    }

    public void save() {
        writeObject(INDEX_FILE, this);
    }

    public TreeMap<String, String> getAddStage() {
        return addStage;
    }

    public TreeSet<String> getRemoveStage() {
        return removeStage;
    }
}
