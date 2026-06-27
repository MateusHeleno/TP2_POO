package view;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.*;

public class MedicoesPanel extends JPanel {

    private JTable tabela;
    private TabelaModel tableModel;
    private JButton btnAdicionar;
    private JButton btnRemover;
    private JScrollPane scrollPane;

    public MedicoesPanel(TabelaModel model) {
        this.tableModel = model != null ? model : new TabelaModel();
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        tabela = new JTable(tableModel);
        //Tabela ocupa a altura disponível
        tabela.setFillsViewportHeight(true);
        //Tabela ocupa toda a altura
        tabela.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        //Permite selecionar várias linhas
        tabela.setAutoCreateRowSorter(true);

        configurarRendererOutliers();

        //Para dar o scroll
        scrollPane = new JScrollPane(tabela);

        //Cria um painle para os botoes, alinhados a esquerda
        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));

        btnAdicionar = new JButton("Adicionar linha");
        btnRemover = new JButton("Remover linha(s) selecionada(s)");

        panelBotoes.add(btnAdicionar);
        panelBotoes.add(btnRemover);

        add(scrollPane, BorderLayout.CENTER);
        add(panelBotoes, BorderLayout.SOUTH);
    }

    //Configura a aparências das células na tabela. Pega o modelo de colunas, recebe ele, percorre todas e aplica o setCellRenderer
    private void configurarRendererOutliers() {
        TableColumnModel colModel = tabela.getColumnModel();

        for (int i = 0; i < tabela.getColumnCount(); i++) {
            colModel.getColumn(i).setCellRenderer(new OutlierTableCellRenderer());
        }
    }

    public void atualizarTabela() {
        tableModel.fireTableDataChanged();
    }


    public int[] getLinhasSelecionadas() {
        //Pega indicies visuais da tabela
        int[] linhasView = tabela.getSelectedRows();
        //Cria um vetor para guardar os indices reais
        int[] linhasModel = new int[linhasView.length];

        for (int i = 0; i < linhasView.length; i++) {
            //Converte indice visual para indice real(pode ser diferente do índice apresentado), isso é importante quando a tabela está ordenada
            linhasModel[i] = tabela.convertRowIndexToModel(linhasView[i]);
        }

        return linhasModel;
    }

    public void limparSelecao() {
        tabela.clearSelection();
    }

    public JTable getTabela() {
        return tabela;
    }

    public TabelaModel getTableModel() {
        return tableModel;
    }

    public JButton getBtnAdicionar() {
        return btnAdicionar;
    }

    public JButton getBtnRemover() {
        return btnRemover;
    }

    public void setTableModel(TabelaModel tableModel) {
        if (tableModel == null) {
            return;
        }

        this.tableModel = tableModel;
        this.tabela.setModel(tableModel);

        configurarRendererOutliers();

        fireTableStructureChangedSeguro();
    }

    private void fireTableStructureChangedSeguro() {
        this.tableModel.fireTableStructureChanged();
    }
}
