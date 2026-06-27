import java.util.*;

import view.MainFrame;
import javax.swing.SwingUtilities;



public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            MainFrame janela = new MainFrame();
            //Define a visibilidade do mainFrame aqui na main
            janela.setVisible(true);
        });

    }
}