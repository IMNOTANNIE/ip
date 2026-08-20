/**
 * Represents a command supported by Yuki.
 */
public enum CommandType {
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete"),
    LIST("list"),
    BYE("bye");

    /** The word the user enters to select this command. */
    private final String keyword;

    /**
     * Creates a command type with its user-facing keyword.
     *
     * @param keyword the word used to enter this command
     */
    CommandType(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the word used to enter this command.
     *
     * @return this command's keyword
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Identifies the command type from the first word of the user's input.
     *
     * @param command the complete command entered by the user
     * @return the matching command type
     * @throws YukiException if the first word is not a supported command
     */
    public static CommandType fromCommand(String command) {
        String[] commandParts = command.trim().split("\\s+", 2);
        String commandWord = commandParts[0];

        for (CommandType commandType : values()) {
            if (commandType.keyword.equals(commandWord)) {
                return commandType;
            }
        }

        throw new YukiException("That command isn't familiar to me.");
    }
}
