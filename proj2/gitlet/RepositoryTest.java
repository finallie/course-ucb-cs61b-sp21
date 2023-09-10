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
        Commit commit = Utils.readObject(
                Utils.join(Repository.OBJECTS_DIR, currentCommitID), Commit.class);
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
        assertEquals(Utils.readObject(
                Utils.join(Repository.OBJECTS_DIR, id), String.class), "hello");
    }

    @Test
    public void saveFile() {
        try {
            File file = new File("test.txt");
            file.createNewFile();
            Utils.writeContents(file, "hello");
            String id = Repository.saveFile(file);
            System.out.println(id);
            assertEquals(Utils.readContentsAsString(
                    Utils.join(Repository.OBJECTS_DIR, id)), "hello");
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
        Repository.getCurrentCommit().debug();
    }

    @Test
    public void resetHead() {
    }

    @Test
    public void getCommit() {
    }

    @Test
    public void status() {
        Repository.merge("c1");
        Repository.status();
    }

    @Test
    public void checkout() {
        Repository.checkout(new String[]{"--", "1.txt"});
    }

    @Test
    public void getUntrackedFiles() {
    }

    @Test
    public void branch() {
        System.out.println("b1:");
        Repository.getBranchHead("b1").debug();
        System.out.println("b2:");
        Repository.getBranchHead("b2").debug();
        System.out.println("c1:");
        Repository.getBranchHead("c1").debug();
    }

    @Test
    public void rmBranch() {
    }
}
