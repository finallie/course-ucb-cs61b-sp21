package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static gitlet.Utils.*;


/**
 * Represents a gitlet repository.
 * does at a high level.
 *
 * @author niyi
 */
public class Repository {
    /**
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");
    public static final File INDEX_FILE = join(Repository.GITLET_DIR, "index");

    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    public static final File BRANCHES_DIR = join(REFS_DIR, "heads");


    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system "
                    + "already exists in the current directory.");
            System.exit(0);
        }
        GITLET_DIR.mkdir();
        try {
            HEAD_FILE.createNewFile();
            writeContents(HEAD_FILE, "master");
            OBJECTS_DIR.mkdir();
            BRANCHES_DIR.mkdirs();
            Commit.getInitCommit().commit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getCurrentCommitID() {
        String currentBranch = getCurrentBranch();
        return readContentsAsString(join(BRANCHES_DIR, currentBranch));
    }

    public static Commit getBranchHead(String branch) {
        try {
            String id = readContentsAsString(join(BRANCHES_DIR, branch));
            return readObject(join(OBJECTS_DIR, id), Commit.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static Commit getCurrentCommit() {
        try {
            return readObject(join(OBJECTS_DIR, getCurrentCommitID()), Commit.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getCurrentBranch() {
        return readContentsAsString(HEAD_FILE);
    }

    public static void reset(String branch, String commitID) {
        writeContents(join(BRANCHES_DIR, branch), commitID);
    }

    public static void resetHead(String branch) {
        writeContents(HEAD_FILE, branch);
    }

    public static String saveObject(Serializable object) {
        byte[] bytes = serialize(object);
        String id = Utils.sha1((Object) bytes);
        File file = join(OBJECTS_DIR, id);
        if (!file.exists()) {
            writeContents(file, (Object) bytes);
        }
        return id;
    }

    public static String saveFile(File file) {
        byte[] bytes = readContents(file);
        String id = Utils.sha1((Object) bytes);
        File copy = join(OBJECTS_DIR, id);
        if (!copy.exists()) {
            writeContents(copy, (Object) bytes);
        }
        return id;
    }

    public static void add(String fileName) {
        File file = join(CWD, fileName);
        if (!file.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        String id = saveFile(file);
        StageArea currentSnapshot = StageArea.getCurrentSnapshot();
        Commit currentCommit = Repository.getCurrentCommit();
        fileName = file.toPath().normalize().toString();
        currentSnapshot.getRemoveStage().remove(fileName);
        if (!id.equals(currentCommit.getSnapshot().get(fileName))) {
            currentSnapshot.getAddStage().put(file.getAbsolutePath(), id);
        }
        currentSnapshot.save();
    }

    public static void commit(String msg) {
        if (msg.trim().isEmpty()) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        StageArea currentSnapshot = StageArea.getCurrentSnapshot();
        if (currentSnapshot.getAddStage().isEmpty()
                && currentSnapshot.getRemoveStage().isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        Commit.createCommit(msg).commit();
    }

    public static void rm(String fileName) {
        File file = join(CWD, fileName);
        if (!file.exists()) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        fileName = file.toPath().normalize().toString();
        StageArea currentSnapshot = StageArea.getCurrentSnapshot();
        Commit currentCommit = Repository.getCurrentCommit();
        if (currentSnapshot.getAddStage().containsKey(fileName)) {
            currentSnapshot.getAddStage().remove(fileName);
        } else if (currentCommit.getSnapshot().containsKey(fileName)) {
            currentSnapshot.getRemoveStage().add(fileName);
            restrictedDelete(file);
        } else {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        currentSnapshot.save();
    }

    private static String formatTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-0800"));
        return sdf.format(date);
    }

    public static void log() {
        Commit commit = Repository.getCurrentCommit();
        if (commit == null) {
            return;
        }
        commit.setId(getCurrentCommitID());
        ArrayDeque<Commit> queue = new ArrayDeque<>();
        queue.add(commit);
        getAllCommits(queue).forEach(Repository::log);
    }

    public static Commit getCommit(String id) {
        try {
            List<String> objs = plainFilenamesIn(OBJECTS_DIR);
            if (objs == null) {
                return null;
            }
            String finalId = id;
            List<String> list = objs.stream().filter(obj -> obj.startsWith(finalId)).collect(Collectors.toList());
            if (list.isEmpty()) {
                return null;
            }
            if (list.size() > 1) {
                throw new RuntimeException("more than one commit with id " + id);
            }
            id = list.get(0);
            Commit commit = readObject(join(OBJECTS_DIR, id), Commit.class);
            commit.setId(id);
            return commit;
        } catch (Exception e) {
            return null;
        }
    }

    private static List<Commit> getAllCommits(ArrayDeque<Commit> arrayDeque) {
        Set<Commit> commits = new HashSet<>();
        while (!arrayDeque.isEmpty()) {
            for (int i = 0, size = arrayDeque.size(); i < size; i++) {
                Commit poll = arrayDeque.poll();
                if (poll == null || commits.contains(poll)) {
                    continue;
                }
                commits.add(poll);
                if (poll.getParent() != null) {
                    arrayDeque.offer(getCommit(poll.getParent()));
                }
                if (poll.getParent2() != null) {
                    arrayDeque.offer(getCommit(poll.getParent2()));
                }
            }
        }
        ArrayList<Commit> list = new ArrayList<>(commits);
        list.sort((a, b) -> b.getDate().compareTo(a.getDate()));
        return list;
    }

    private static void log(Commit commit) {
        System.out.println("===");
        System.out.printf("commit %s\n", commit.getId());
        if (commit.getParent2() != null) {
            System.out.printf("Merge: %s %s\n",
                    commit.getParent().substring(0, 6), commit.getParent2().substring(0, 6));
        }
        System.out.printf("Date: %s\n", formatTime(commit.getDate()));
        System.out.println(commit.getMessage());
        System.out.println();
    }

    private static List<Commit> getGlobalCommits() {
        ArrayDeque<Commit> queue = new ArrayDeque<>();
        for (String branch : getAllBranches()) {
            Commit commit = getBranchHead(branch);
            commit.setId(readContentsAsString(join(BRANCHES_DIR, branch)));
            queue.add(commit);
        }
        return getAllCommits(queue);
    }

    private static List<String> getAllBranches() {
        List<String> list = plainFilenamesIn(BRANCHES_DIR);
        if (list == null) {
            return Collections.emptyList();
        }
        return list;
    }

    private static TreeSet<String> getAllWorkdirFiles() {
        List<String> list = plainFilenamesIn(CWD);
        if (list == null) {
            return new TreeSet<>();
        }
        return new TreeSet<>(list);
    }

    public static void globalLog() {
        getGlobalCommits().forEach(Repository::log);
    }

    public static void find(String msg) {
        getGlobalCommits().stream()
                .filter(commit -> commit.getMessage().contains(msg))
                .forEach(commit -> System.out.println(commit.getId()));
    }

    private static String getId(String fileName) {
        byte[] bytes = readContents(join(CWD, fileName));
        return sha1((Object) bytes);
    }

    public static void status() {
        StageArea currentSnapshot = StageArea.getCurrentSnapshot();
        Commit currentCommit = Repository.getCurrentCommit();

        String currentBranch = getCurrentBranch();
        List<String> allBranches = getAllBranches();
        System.out.println("=== Branches ===");
        for (String branch : allBranches) {
            if (branch.equals(currentBranch)) {
                System.out.printf("*%s\n", branch);
            } else {
                System.out.println(branch);
            }
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        currentSnapshot.getAddStage().keySet().forEach(System.out::println);
        System.out.println();

        System.out.println("=== Removed Files ===");
        currentSnapshot.getRemoveStage().forEach(System.out::println);
        System.out.println();

        TreeSet<String> untracked = new TreeSet<>();
        TreeSet<String> modified = new TreeSet<>();
        TreeSet<String> allWorkdirFiles = getAllWorkdirFiles();
        for (String fileName : allWorkdirFiles) {
            if (currentSnapshot.getRemoveStage().contains(fileName)) {
                untracked.add(fileName);
            } else if (currentSnapshot.getAddStage().containsKey(fileName)) {
                if (!currentSnapshot.getAddStage().get(fileName).equals(getId(fileName))) {
                    modified.add(fileName + (modified));
                }
            } else if (currentCommit.getSnapshot().containsKey(fileName)) {
                if (!currentCommit.getSnapshot().get(fileName).equals(getId(fileName))) {
                    modified.add(fileName + (modified));
                }
            } else {
                untracked.add(fileName);
            }
        }
        for (String added : currentSnapshot.getAddStage().keySet()) {
            if (!allWorkdirFiles.contains(added)) {
                modified.add(added + " (deleted)");
            }
        }
        for (String oldFile : currentCommit.getSnapshot().keySet()) {
            if (!allWorkdirFiles.contains(oldFile)) {
                modified.add(oldFile + " (deleted)");
            }
        }

        System.out.println("=== Modifications Not Staged For Commit ===");
        modified.forEach(System.out::println);
        System.out.println();

        System.out.println("=== Untracked Files ===");
        untracked.forEach(System.out::println);
        System.out.println();
    }

    public static void checkout(String[] args) {
        if (args.length > 3 || args.length == 0) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        String commitID = null, fileName = null, branch = null;
        if (args.length == 3) {
            if (!args[1].equals("--")) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            commitID = args[0];
            fileName = args[2];
        } else if (args.length == 2) {
            if (!args[0].equals("--")) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            commitID = getCurrentCommitID();
            fileName = args[1];
        } else {
            branch = args[0];
        }
        if (branch == null) {
            checkoutFile(commitID, fileName);
        } else {
            if (getCurrentBranch().equals(branch)) {
                System.out.println("No need to checkout the current branch.");
                System.exit(0);
            }
            checkoutBranch(branch);
        }
    }

    private static void checkoutBranch(String branch) {
        Commit commit = getBranchHead(branch);
        if (commit == null) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        if (!getUntrackedFiles().isEmpty()) {
            System.out.println("There is an untracked file in the way; delete it or add it first.");
            System.exit(0);
        }
        commit.getSnapshot().forEach((fileName, id) -> {
            byte[] bytes = readContents(join(OBJECTS_DIR, id));
            writeContents(join(CWD, fileName), (Object) bytes);
        });
        getAllWorkdirFiles().forEach(fileName -> {
            if (!commit.getSnapshot().containsKey(fileName)) {
                restrictedDelete(join(CWD, fileName));
            }
        });
        reset(branch, commit.getId());
        resetHead(branch);
        StageArea.getInitSnapshot().save();
    }

    public static TreeSet<String> getUntrackedFiles() {
        Commit currentCommit = getCurrentCommit();
        StageArea currentSnapshot = StageArea.getCurrentSnapshot();
        TreeSet<String> untracked = new TreeSet<>();
        TreeSet<String> allWorkdirFiles = getAllWorkdirFiles();
        for (String fileName : allWorkdirFiles) {
            if (currentSnapshot.getRemoveStage().contains(fileName)) {
                untracked.add(fileName);
            } else if (!currentSnapshot.getAddStage().containsKey(fileName)
                    && !currentCommit.getSnapshot().containsKey(fileName)) {
                untracked.add(fileName);
            }
        }
        return untracked;
    }

    private static void checkoutFile(String commitID, String fileName) {
        Commit commit = getCommit(commitID);
        if (commit == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        String id = commit.getSnapshot().get(fileName);
        if (id == null) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        byte[] bytes = readContents(join(OBJECTS_DIR, id));
        writeContents(join(CWD, fileName), (Object) bytes);
    }
}
