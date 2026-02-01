public class ConsoleUI {

    public static void header(String title) {
        System.out.println("\n==============================");
        System.out.println("[ " + title + " ]");
        System.out.println("==============================\n");
    }

    public static void divider() {
        System.out.println("------------------------------");
    }

    public static void option(String text) {
        System.out.println(text);
    }
}
