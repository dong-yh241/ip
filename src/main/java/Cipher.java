import java.util.Scanner;

public class Cipher {
    private static final String LINE = "____________________________________________________________";
    private static final String NAME = "Cipher";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        String[] tasks = new String[100];
        int size = 0;

        System.out.println(LINE);
        System.out.println("Hello! I'm " + NAME);
        System.out.println("What can I do for you?");
        System.out.println(LINE);

        while (true) {
            String input = sc.nextLine().trim();

            if (input.equals("bye")) {
                System.out.println(LINE);
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(LINE);
                break;
            }

            if (input.equals("list")) {
                System.out.println(LINE);
                for (int i = 0; i < size; i++) {
                    System.out.println((i + 1) + ". " + tasks[i]);
                }
                System.out.println(LINE);
                continue;
            }

            tasks[size] = input;
            size++;

            System.out.println(LINE);
            System.out.println("added: " + input);
            System.out.println(LINE);
        }
        
        sc.close();
    }
}
