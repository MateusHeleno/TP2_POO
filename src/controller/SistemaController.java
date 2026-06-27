package controller;

import DAO.MedicaoDAO;
import model.*;
import view.*;
import java.io.PrintWriter;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SistemaController {

    private MainFrame view;
    private MedicaoDAO dao;
    private Filtro filtroAtual;
    private RegressaoLinear regressaoAtual;
    private TabelaModel tableModel;

    private List<Medicao> dadosOriginais;
    private List<Medicao> dadosFiltrados;

    public SistemaController(MainFrame view) {
        this.view = view;
        this.dao = new MedicaoDAO();
        this.filtroAtual = new Filtro();
        this.regressaoAtual = new RegressaoLinear();
        this.tableModel = view.getMedicoesPanel().getTableModel();

        this.dadosOriginais = new ArrayList<>();
        this.dadosFiltrados = new ArrayList<>();

        configurarEventos();
    }

    //Define o que cada botão fará(chamando os métodos presentes em todo o sistema)
    private void configurarEventos() {
        view.getItemCarregar().addActionListener(e -> {
            try {
                carregarArquivoTSV();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });
        view.getItemExportar().addActionListener(e -> exportarRelatorioTSV());
        view.getItemSair().addActionListener(e -> sair());
        view.getItemLimparFiltros().addActionListener(e -> limparTodosFiltros());

        view.getMedicoesPanel()
                .getBtnAdicionar()
                .addActionListener(e -> adicionarMedicao());

        view.getMedicoesPanel()
                .getBtnRemover()
                .addActionListener(e -> removerMedicoesSelecionadas());

        view.getRegressaoPanel()
                .getSliderOutlierPercentual()
                .addChangeListener(e -> atualizarOutliers());

        view.getRegressaoPanel()
                .getToggleExcluirOutliers()
                .addActionListener(e -> atualizarOutliers());
    }

    public void carregarArquivoTSV() throws IOException {
        JFileChooser chooser = new JFileChooser();
        File pastaInicial = new File("./ArquivosTeste/");
        chooser.setCurrentDirectory(pastaInicial);
        int opcao = chooser.showOpenDialog(view);

        if (opcao != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File arquivo = chooser.getSelectedFile();

        dadosOriginais = dao.carregarDeTSV(arquivo);

        if (dadosOriginais.isEmpty()) {
            view.exibirMensagemErro("Nenhuma medição válida foi carregada.");
            return;
        }

        tableModel.setDadosOriginais(dadosOriginais);

        view.getRegressaoPanel().exibirMensagensValidacao(
                dao.getErros().getMensagemResumo()
        );

        aplicarFiltros();

        view.exibirMensagemInfo("Arquivo carregado com sucesso.");
    }
    
    public void aplicarFiltros() {
        dadosFiltrados = filtroAtual.aplicarFiltros(dadosOriginais);

        tableModel.setDadosFiltrados(dadosFiltrados);

        recalcularRegressao();
    }

    public void recalcularRegressao() {
        boolean sucesso = regressaoAtual.treinar(dadosFiltrados);

        if (!sucesso) {
            view.getRegressaoPanel().limparResultados();
            view.limparProgressBarR2();
            return;
        }

        view.getRegressaoPanel().atualizarResultados(
                regressaoAtual.getBeta0(),
                regressaoAtual.getBeta1(),
                regressaoAtual.getR2(),
                regressaoAtual.getN()
        );

        view.atualizarProgressBarR2(regressaoAtual.getR2());

        tableModel.fireTableDataChanged();

        view.getGraficoPanel().setDados(dadosFiltrados, regressaoAtual);
    }

    public void atualizarOutliers() {
        double limite = view.getRegressaoPanel().getLimiteOutlierPercentual();
        boolean excluir = view.getRegressaoPanel().isExcluirOutliers();

        tableModel.setLimiteOutlier(limite);
        tableModel.setExcluirOutliers(excluir);

        dadosFiltrados = tableModel.getDadosFiltrados();

        recalcularRegressao();
    }

    public void adicionarMedicao() {
        Medicao nova = new Medicao();

        tableModel.adicionarMedicao(nova);
        dadosOriginais = tableModel.getDadosOriginais();

        aplicarFiltros();
    }

    public void removerMedicoesSelecionadas() {
        int[] linhas = view.getMedicoesPanel().getLinhasSelecionadas();

        if (linhas.length == 0) {
            view.exibirMensagemErro("Selecione pelo menos uma linha para remover.");
            return;
        }

        tableModel.removerMedicoes(linhas);
        dadosOriginais = tableModel.getDadosOriginais();

        view.getMedicoesPanel().limparSelecao();

        aplicarFiltros();
    }

    public void limparTodosFiltros() {
        filtroAtual.limpar();

        dadosFiltrados = new ArrayList<>(dadosOriginais);

        tableModel.setDadosFiltrados(dadosFiltrados);

        recalcularRegressao();
    }

    public void exportarRelatorioTSV() {
        if (dadosFiltrados == null || dadosFiltrados.isEmpty()) {
            view.exibirMensagemErro("Não há dados para exportar.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Exportar relatório TSV");
        chooser.setSelectedFile(new File("relatorio_medições.tsv"));

        int opcao = chooser.showSaveDialog(view);

        if (opcao != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File arquivo = chooser.getSelectedFile();

        try (PrintWriter writer = new PrintWriter(arquivo)) {

            writer.println(
                "timestamp\tcidade\tlatitude\tlongitude\ttemperatura\tconsumoKwh\tconsumoPrevisto\tresiduoPercentual"
            );

            for (Medicao m : dadosFiltrados) {
                writer.printf(
                    "%s\t%s\t%.6f\t%.6f\t%.2f\t%.2f\t%.2f\t%.2f%n",
                    m.getTimestamp(),
                    m.getCidade(),
                    m.getLatitude(),
                    m.getLongitude(),
                    m.getTemperatura(),
                    m.getConsumoKwh(),
                    m.getConsumoPrevisto(),
                    m.getResiduoPercentual()
                );
            }

            view.exibirMensagemInfo("Relatório exportado com sucesso.");

        } catch (Exception e) {
            view.exibirMensagemErro("Erro ao exportar relatório: " + e.getMessage());
        }
    }

    public void sair() {
        int resposta = JOptionPane.showConfirmDialog(
                view,
                "Deseja realmente sair?",
                "Confirmar saída",
                JOptionPane.YES_NO_OPTION
        );

        if (resposta == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}