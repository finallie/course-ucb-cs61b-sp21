package gitlet;


import java.io.Serializable;
import java.util.Date;
import java.util.TreeMap;

import static gitlet.Repository.saveObject;

/**
 * Represents a gitlet commit object.
 * does at a high level.
 *
 * @author niyi
 */
public class Commit implements Serializable {

    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /**
     * The message of this Commit.
     */
    private String message;
    /**
     * The date of this Commit.
     */
    private Date date;
    private transient String id;

    public String getParent() {
        return parent;
    }

    public String getParent2() {
        return parent2;
    }

    /**
     * The parent of this Commit.
     */
    private String parent;
    private String parent2;
    /**
     * The snapshot of this Commit.
     */
    private TreeMap<String, String> snapshot;

    private Commit() {
    }


    public static Commit getInitCommit() {
        Commit commit = new Commit();
        commit.message = "initial commit";
        commit.date = new Date(0);
        commit.parent = null;
        commit.snapshot = new TreeMap<>();
        return commit;
    }

    public static Commit createCommit(String message) {
        Commit commit = new Commit();
        commit.message = message;
        commit.date = new Date();
        commit.parent = Repository.getCurrentCommitID();
        StageArea currentSnapshot = StageArea.getCurrentSnapshot();
        Commit currentCommit = Repository.getCurrentCommit();
        commit.snapshot = new TreeMap<>(currentCommit.getSnapshot());
        commit.snapshot.putAll(currentSnapshot.getAddStage());
        for (String fileName : currentSnapshot.getRemoveStage()) {
            commit.snapshot.remove(fileName);
        }
        return commit;
    }

    public static Commit createCommit(String message, String parent2) {
        Commit commit = createCommit(message);
        commit.parent2 = parent2;
        return commit;
    }

    public void commit() {
        String commitId = saveObject(this);
        Repository.reset(Repository.getCurrentBranch(), commitId);
        StageArea.getInitSnapshot().save();
    }


    public TreeMap<String, String> getSnapshot() {
        return snapshot;
    }

    public String getMessage() {
        return message;
    }

    public Date getDate() {
        return date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Commit)) {
            return false;
        }
        return id.equals(((Commit) obj).id);
    }

    public void debug() {
        System.out.println("Commit id: " + id);
        System.out.println("Commit message: " + message);
        snapshot.forEach((k, v) -> {
            if (k.endsWith(".txt") && !k.contains("wug")) {
                System.out.println(
                        k + ": " + Utils.readContentsAsString(
                                Utils.join(Repository.OBJECTS_DIR, v)));
            }
        });
        System.out.println();

    }
}
