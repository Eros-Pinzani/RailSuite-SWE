//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

/*import dao.ConsoleDAO;
import service.ConsoleService;
import domain.Console;*/

public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.print("Hello and welcome with my friends Devs and Eros!\n");

        for (int i = 1; i <= 5; i++) {
            //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
            // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
            System.out.println("i = " + i);
        }
        /*var consoles = ConsoleDAO.getInstance().getAllConsoles();
        for (Console c : consoles) {
            System.out.println(c.getId() + " - " + c.getNome());
        }
        ConsoleService service = new ConsoleService();
        String nomeConsole = "PlayStation 5";
        var consolesByNome = service.getConsolesByNome(nomeConsole);
        System.out.println("Console con nome '" + nomeConsole + "':");
        for (Console c : consolesByNome) {
            System.out.println(c.getId() + " - " + c.getNome());
        }*/
    }
}