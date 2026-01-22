import java.util.Scanner;

public class Cipher {
    private static final String LINE = "____________________________________________________________";
    private static final String NAME = "Cipher";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        String[] tasks = new String[100];
        boolean[] done = new boolean[100];
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
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < size; i++) {
                    String icon = done[i] ? "X" : " ";
                    System.out.println((i + 1) + ".[" + icon + "] " + tasks[i]);
                }
                System.out.println(LINE);
                continue;
            }

            if (input.startsWith("mark ")) {
                int n = Integer.parseInt(input.substring(5).trim());
                int idx = n - 1;
                done[idx] = true;

                System.out.println(LINE);
                System.out.println("Nice! I've marked this task as done:");
                System.out.println((idx + 1) + ".[X] " + tasks[idx]); // improved format
                System.out.println(LINE);
                continue;
            }

            if (input.startsWith("unmark ")) {
                int n = Integer.parseInt(input.substring(7).trim());
                int idx = n - 1;
                done[idx] = false;

                System.out.println(LINE);
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println((idx + 1) + ".[ ] " + tasks[idx]); // improved format
                System.out.println(LINE);
                continue;
            }

            tasks[size] = input;
            done[size] = false;
            size++;

            System.out.println(LINE);
            System.out.println("added: " + input);
            System.out.println(LINE);
        }

        sc.close();
    }
}
