package gitlet;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *
 * @author niyi
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch (firstArg) {
            case "init":
                validateNumArgs(args, 1);
                Repository.init();
                break;
            case "add":
                validateNumArgs(args, 2);
                Repository.add(args[1]);
                break;
            case "commit":
                validateNumArgs(args, 2);
                Repository.commit(args[1]);
                break;
            case "rm":
                validateNumArgs(args, 2);
                Repository.rm(args[1]);
                break;
            case "log":
                validateNumArgs(args, 1);
                Repository.log();
                break;
            case "global-log":
                validateNumArgs(args, 1);
                Repository.globalLog();
                break;
            case "find":
                validateNumArgs(args, 2);
                Repository.find(args[1]);
                break;
            case "status":
                validateNumArgs(args, 1);
                Repository.status();
                break;
            case "checkout":
                Repository.checkout(args);
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }

    private static void validateNumArgs(String[] args, int num) {
        if (args.length != num) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }
}
