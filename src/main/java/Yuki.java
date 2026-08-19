import java.util.Scanner;

public class Yuki {
    public static void main(String[] args) {
        String separatorLine = "❄──────❄──────❄──────❄──────❄──────❄──────❄";
        String banner = "__   __     _    _ \n"
                + "\\ \\ / /   _| | _(_)\n"
                + " \\ V / | | | |/ / |\n"
                + "  | || |_| |   <| |\n"
                + "  |_| \\__,_|_|\\_\\_|\n";

        Scanner scanner = new Scanner(System.in);
        // A list to store whatever text the user enters
        String[] commandList = new String[100];
        // A counter to record the number of commands stored in the list
        int commandCount = 0;

        // Greet the user
        System.out.println(separatorLine);
        System.out.println(banner);
        System.out.println("...Hello. This is Yuki.");
        System.out.println("What do you need?");
        System.out.println(separatorLine);

        while (true) {
            // Let the user enter a command and record the command
            String command = scanner.nextLine();
            // Print the list when the user types "list"
            if (command.equals("list")) {
                System.out.println(separatorLine);
                for (int i = 0; i < commandCount; i++) {
                    System.out.println((i + 1) + ". " + commandList[i]);
                }
                System.out.println(separatorLine);
            } else if (command.equals("bye")) {
                // Exit when the user types "bye"
                System.out.println(separatorLine);
                System.out.println("...Goodbye.");
                System.out.println(separatorLine);
                break;
            } else {
                // Store and print the command
                commandList[commandCount] = command;
                commandCount++;
                System.out.println(separatorLine);
                System.out.println("added: " + command);
                System.out.println(separatorLine);
            }
        }
    }
}
