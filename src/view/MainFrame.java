package view;

import controller.SistemaController;

import javax.swing.*;
import java.awt.*;

//É a tela principal. Organiza o menu e os demais itens. O Controller deve ter a maior autonomia
public class MainFrame extends JFrame {

    private JTabbedPane tabbedPane;

    private MedicoesPanel medicoesPanel;
    private FiltrosPanel filtrosPanel;
    private RegressaoPanel regressaoPanel;
    private GraficoPanel graficoPanel;

    private JProgressBar progressBarR2;

    private JMenuBar menuBar;
    private JMenuItem itemCarregar;
    private JMenuItem itemExportar;
    private JMenuItem itemSair;
    private JMenuItem itemLimparFiltros;

    private SistemaController controller;

    public MainFrame() {
        configurarJanela();
        inicializarComponentes();
        criarMenuBar();

        controller = new SistemaController(this);

        setJMenuBar(menuBar);
    }

    private void configurarJanela() {
        setTitle("Sistema de Análise Energética");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void inicializarComponentes() {
        tabbedPane = new JTabbedPane();

        medicoesPanel = new MedicoesPanel();
        filtrosPanel = new FiltrosPanel();
        regressaoPanel = new RegressaoPanel();
        graficoPanel = new GraficoPanel();

        tabbedPane.addTab("Medições", medicoesPanel);
        tabbedPane.addTab("Filtros", filtrosPanel);
        tabbedPane.addTab("Regressão e Previsão", regressaoPanel);
        tabbedPane.addTab("Gráfico", graficoPanel);

        progressBarR2 = new JProgressBar(0, 100);
        progressBarR2.setStringPainted(true);
        progressBarR2.setString("R² = --");

        add(tabbedPane, BorderLayout.CENTER);
        add(progressBarR2, BorderLayout.SOUTH);
    }

    private void criarMenuBar() {
        menuBar = new JMenuBar();

        JMenu menuArquivo = new JMenu("Arquivo");

        itemCarregar = new JMenuItem("Carregar TSV");
        itemExportar = new JMenuItem("Exportar Relatório TSV");
        itemSair = new JMenuItem("Sair");

        menuArquivo.add(itemCarregar);
        menuArquivo.add(itemExportar);
        menuArquivo.addSeparator();
        menuArquivo.add(itemSair);

        JMenu menuFiltros = new JMenu("Filtros");

        itemLimparFiltros = new JMenuItem("Limpar todos filtros");

        menuFiltros.add(itemLimparFiltros);

        menuBar.add(menuArquivo);
        menuBar.add(menuFiltros);
    }

    public void atualizarProgressBarR2(double r2) {
        int valor = (int) Math.round(r2 * 100);

        if (valor < 0) {
            valor = 0;
        }

        if (valor > 100) {
            valor = 100;
        }

        progressBarR2.setValue(valor);
        progressBarR2.setString("R² = " + FormatarNumero.decimal(r2));

        if (r2 < 0.3) {
            progressBarR2.setForeground(Color.RED);
        } else if (r2 < 0.7) {
            progressBarR2.setForeground(Color.ORANGE);
        } else {
            progressBarR2.setForeground(Color.GREEN);
        }
    }

    public void limparProgressBarR2() {
        progressBarR2.setValue(0);
        progressBarR2.setString("R² = --");
        progressBarR2.setForeground(Color.GRAY);
    }

    public void exibirMensagemErro(String msg) {
        JOptionPane.showMessageDialog(
                this,
                msg,
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public void exibirMensagemInfo(String msg) {
        JOptionPane.showMessageDialog(
                this,
                msg,
                "Informação",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public MedicoesPanel getMedicoesPanel() {
        return medicoesPanel;
    }

    public FiltrosPanel getFiltrosPanel() {
        return filtrosPanel;
    }

    public RegressaoPanel getRegressaoPanel() {
        return regressaoPanel;
    }

    public GraficoPanel getGraficoPanel() {
        return graficoPanel;
    }

    public JMenuItem getItemCarregar() {
        return itemCarregar;
    }

    public JMenuItem getItemExportar() {
        return itemExportar;
    }

    public JMenuItem getItemSair() {
        return itemSair;
    }

    public JMenuItem getItemLimparFiltros() {
        return itemLimparFiltros;
    }
}