package view;

import model.Medicao;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class OutlierTableCellRenderer
        extends DefaultTableCellRenderer {

    private static final double LIMITE_PADRAO = 10.0;
    private static final Color OUTLIER_BG = new Color(255, 200, 200);
    private static final Color OUTLIER_SELECTED_BG = new Color(255, 150, 150);
    private static final Color LINHA_PAR_BG = new Color(240, 248, 255); // para nao instanciar varios obj

    private static final int COLUNA_TEMPERATURA = 4;
    private static final int COLUNA_CONSUMO = 5;
    private static final int COLUNA_CONSUMO_PREVISTO = 6;
    private static final int COLUNA_RESIDUO = 7;

    private double limiteOutlier;

    public OutlierTableCellRenderer() {
        this.limiteOutlier = LIMITE_PADRAO;
    }

    public void setLimiteOutlier(double limite) {
        this.limiteOutlier = limite;
    }

    private void formatarValor(double valor, int coluna) {

        switch (coluna) {

            case COLUNA_TEMPERATURA:
                setText(FormatarNumero.temperatura(valor));
                break;

            case COLUNA_CONSUMO:
            case COLUNA_CONSUMO_PREVISTO:
                setText(FormatarNumero.consumo(valor));
                break;

            case COLUNA_RESIDUO:
                setText(FormatarNumero.percentual(valor));
                break;

            default:
                break;
        }
}

    @Override
    public Component getTableCellRendererComponent(
            JTable tabela,
            Object valor,
            boolean isSelected,
            boolean hasFocus,
            int linha,
            int coluna) {

        Component c = super.getTableCellRendererComponent(
                tabela,
                valor,
                isSelected,
                hasFocus,
                linha,
                coluna);

        if (!(tabela.getModel() instanceof TabelaModel model)) { // travaa de seguranca
            return c;
        }

        int linhaModel = tabela.convertRowIndexToModel(linha); // para dar a posição correta e real da llinahd entro do model

        Medicao m = model.getMedicaoAt(linhaModel); // buscar as medições

        boolean ehOutlier = m != null &&
                Math.abs(m.getResiduoPercentual()) > limiteOutlier; // colcoar na variavel, formula condição cores

        if (ehOutlier) {

            if (isSelected) {
                c.setBackground(OUTLIER_SELECTED_BG);
            } else {
                c.setBackground(OUTLIER_BG);
            }
            c.setForeground(Color.BLACK);

        } else {

            if (isSelected) {

                c.setBackground(
                        tabela.getSelectionBackground());

                c.setForeground(
                        tabela.getSelectionForeground());

            } else {

                if (linha % 2 == 0) {
                    c.setBackground(LINHA_PAR_BG);
                } else {
                    c.setBackground(Color.WHITE);
                }
                c.setForeground(Color.BLACK);

            }

        }

        if (valor instanceof Number numero) { // garante que existe e é um Number

            double v = numero.doubleValue(); // converter para primitivo

            formatarValor(v,coluna);// formatando

        }

        return c;
    }
}