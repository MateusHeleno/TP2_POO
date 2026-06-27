package controller;

import DAO.MedicaoDAO;
import model.*;
import view.*;

import java.awt.*;
import java.io.PrintWriter;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeParseException;
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

    // Define o que cada botão fará (chamando os métodos presentes em todo o sistema)
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

        if (opcao != JFileChooser.APPROVE_OPTION)
            return;

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
        // ao criar uma nova medição, não deveria pedir ao usuário os dados da medição? caso contrário os valores serão null
        JTextField latitude = new JTextField(20);
        JTextField longitude = new JTextField(20);
        JTextField temperatura = new JTextField(10);
        JTextField consumo = new JTextField(10);
        JTextField cidade = new JTextField(25);
        JTextField data = new JTextField(10);

        JPanel form = new JPanel(new GridLayout(0, 1, 5, 5));
        form.add(new JLabel("Altitude: "));
        form.add(latitude);

        form.add(new JLabel("Longitude: "));
        form.add(longitude);

        form.add(new JLabel("Temperatura: "));
        form.add(temperatura);

        form.add(new JLabel("Cidade: "));
        form.add(cidade);

        form.add(new JLabel("Consumo: "));
        form.add(consumo);

        form.add(new JLabel("Data/Hora (yyyy-MM-dd HH:mm:ss): "));
        form.add(data);

        int opcao = JOptionPane.showConfirmDialog(
                null,
                form,
                "Adicionar nova Medição",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
        if (opcao == JOptionPane.OK_OPTION) {
            try {
                Medicao nova = new Medicao();

                nova.setCidade(cidade.getText());
                nova.setLatitude(Double.parseDouble(latitude.getText().replace(',', '.')));
                nova.setLongitude(Double.parseDouble(longitude.getText().replace(',', '.')));
                nova.setTemperatura(Double.parseDouble(temperatura.getText().replace(',', '.')));
                nova.setConsumoKwh(Double.parseDouble(consumo.getText().replace(',', '.')));
                nova.setTimestamp(MedicaoDAO.parseTimestamp(data.getText()));

                tableModel.adicionarMedicao(nova);
                dadosOriginais = tableModel.getDadosOriginais();

                aplicarFiltros();
                JOptionPane.showMessageDialog(null, "Medição Adicionada com Sucesso", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(
                        null,
                        "Erro: Não digite letras nos campos numéricos",
                        "Erro de Formato",
                        JOptionPane.ERROR_MESSAGE
                );
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(
                        null,
                        "Erro: A data deve estar no formato yyyy-MM-dd HH:mm:ss",
                        "Erro de Data",
                        JOptionPane.ERROR_MESSAGE
                );
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        null,
                        "Ocorreu um erro ao adicionar a medição: " + e.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
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

        if (opcao != JFileChooser.APPROVE_OPTION)
            return;

        File arquivo = chooser.getSelectedFile();
        TSVFileManager tsvFileManager = new TSVFileManager(arquivo);

        try  {
            tsvFileManager.escreverTSV(dadosFiltrados);
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