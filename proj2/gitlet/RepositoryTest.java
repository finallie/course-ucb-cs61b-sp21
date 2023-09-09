package gitlet;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class RepositoryTest {

    @Test
    public void init() {
        Repository.init();
    }

    @Test
    public void getCurrentCommitID() {
        String currentCommitID = Repository.getCurrentCommitID();
        Commit commit = Utils.readObject(Utils.join(Repository.OBJECTS_DIR, currentCommitID), Commit.class);
        StageArea currentSnapshot = StageArea.getCurrentSnapshot();
        System.out.println("");
    }

    @Test
    public void getCurrentBranch() {
        assertEquals(Repository.getCurrentBranch(), "master");
    }

    @Test
    public void reset() {
    }

    @Test
    public void saveObject() {
        String id = Repository.saveObject("hello");
        assertEquals(Utils.readObject(Utils.join(Repository.OBJECTS_DIR, id), String.class), "hello");
    }

    @Test
    public void saveFile() {
        try {
            File file = new File("test.txt");
            file.createNewFile();
            Utils.writeContents(file, "hello");
            String id = Repository.saveFile(file);
            System.out.println(id);
            assertEquals(Utils.readContentsAsString(Utils.join(Repository.OBJECTS_DIR, id)), "hello");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void add() {
        Repository.add("pom.xml");
    }

    @Test
    public void commit() {
        Repository.commit("test");
    }

    @Test
    public void rm() {
    }

    @Test
    public void log() {
        Repository.log();
    }

    @Test
    public void globalLog() {
        Repository.globalLog();
    }

    @Test
    public void find() {
        Repository.find("tes");
    }

    @Test
    public void getBranchHead() {
    }

    @Test
    public void getCurrentCommit() {
    }

    @Test
    public void resetHead() {
    }

    @Test
    public void getCommit() {
    }

    @Test
    public void status() {
        Repository.status();
    }

    @Test
    public void checkout() {
    }

    @Test
    public void getUntrackedFiles() {
    }

    @Test
    public void branch() {
    }

    @Test
    public void rmBranch() {
    }
}