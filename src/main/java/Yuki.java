import java.util.Scanner;

public class Yuki {
    public static void main(String[] args) {
        String horizontalLine = "❄──────❄──────❄──────❄──────❄──────❄──────❄";
        String banner = "__   __     _    _ \n"
                + "\\ \\ / /   _| | _(_)\n"
                + " \\ V / | | | |/ / |\n"
                + "  | || |_| |   <| |\n"
                + "  |_| \\__,_|_|\\_\\_|\n";
        Scanner sc = new Scanner(System.in);

        // Greet the user
        System.out.println(horizontalLine);
        System.out.println(banner);
        System.out.println("...Hello. This is Yuki.");
        System.out.println("What do you need?");
        System.out.println(horizontalLine);

        while (true) {
            // Let the user enter a command and record the command
            String command = sc.nextLine();
            // Exit when the user types the command bye
            if (command.equals("bye")) {
                System.out.println(horizontalLine);
                System.out.println("...Goodbye.");
                System.out.println(horizontalLine);
                break;
            }
            // Echo the command
            System.out.println(horizontalLine);
            System.out.println(command);
            System.out.println(horizontalLine);
        }
    }
}
