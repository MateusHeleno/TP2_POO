package view;

import javax.swing.*;
import java.awt.*;

public class RegressaoPanel extends JPanel {

    //Intercepto
    private JLabel lblBeta0;
    //Inclinação
    private JLabel lblBeta1;
    //Qualidade da regressão
    private JLabel lblR2;
    //Quantidade de dados usados
    private JLabel lblN;

    //Controle deslizante
    private JSlider sliderOutlierPercentual;
    //Botão liga/desliga para excuir outliers da tabela
    private JToggleButton toggleExcluirOutliers;
    //Área de texto para mostrar mensagens de validação
    private JTextArea txtAreaErros;

    public RegressaoPanel() {
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));

        //Cria um painel com 1 linha e 4 colunas para os resultados
        JPanel panelResultados = new JPanel(new GridLayout(1, 4, 10, 10));
        //Coloca uma borda com título
        panelResultados.setBorder(
                BorderFactory.createTitledBorder("Resultados da Regressão")
        );

        //Cria os labels centralizados, ainda sem valores
        lblBeta0 = new JLabel("β₀: --", SwingConstants.CENTER);
        lblBeta1 = new JLabel("β₁: --", SwingConstants.CENTER);
        lblR2 = new JLabel("R²: --", SwingConstants.CENTER);
        lblN = new JLabel("N: --", SwingConstants.CENTER);

        //Adiciona os labels ao painel de resultados
        panelResultados.add(lblBeta0);
        panelResultados.add(lblBeta1);
        panelResultados.add(lblR2);
        panelResultados.add(lblN);

        //Cria o painel de outliers com elementos alinhados à esquerda
        JPanel panelOutliers = new JPanel(new FlowLayout(FlowLayout.LEFT));
        //Define a borda com título
        panelOutliers.setBorder(
                BorderFactory.createTitledBorder("Configuração de Outliers")
        );

        //Cria o slider de 1 À 100%, começando em 10%
        sliderOutlierPercentual = new JSlider(1, 100, 10);
        //Marca os valores principais de 10 em 10
        sliderOutlierPercentual.setMajorTickSpacing(10);
        //Marca os valores menores de 5 em 5
        sliderOutlierPercentual.setMinorTickSpacing(5);
        //Mostra marcações e números no slider
        sliderOutlierPercentual.setPaintTicks(true);
        sliderOutlierPercentual.setPaintLabels(true);

        //Cria botão de ativar/desativar exclusão de outliers
        toggleExcluirOutliers = new JToggleButton("Excluir outliers da tabela");
        //Adiciona texto, slider e botão ao painel de outliers
        panelOutliers.add(new JLabel("Limite de resíduo (%):"));
        panelOutliers.add(sliderOutlierPercentual);
        panelOutliers.add(toggleExcluirOutliers);

        //Cria área de texto de 5 linhas e de 40 colunas
        txtAreaErros = new JTextArea(5, 40);
        //Impede o usuário de editar as mensagens
        txtAreaErros.setEditable(false);
        //Define o texto par avermelho
        txtAreaErros.setForeground(Color.RED);
        //Quebra de linha sem cortar as palavras
        txtAreaErros.setLineWrap(true);
        txtAreaErros.setWrapStyleWord(true);

        //Coloca o texto dentro de um scroll
        JScrollPane scrollErros = new JScrollPane(txtAreaErros);
        
        scrollErros.setBorder(
                BorderFactory.createTitledBorder("Mensagens de Validação")
        );

        //Cria um painel superior
        JPanel panelTopo = new JPanel(new BorderLayout(10, 10));
        //Coloca resultados em cima e outliers embaixo
        panelTopo.add(panelResultados, BorderLayout.NORTH);
        panelTopo.add(panelOutliers, BorderLayout.CENTER);

        //Adiciona o topo e a área de mensagens no painel principal
        add(panelTopo, BorderLayout.NORTH);
        add(scrollErros, BorderLayout.CENTER);
    }

    //Chamado pelo sistemaController quando a regressão for calculada
    public void atualizarResultados(double beta0, double beta1, double r2, int n) {
        lblBeta0.setText(String.format("β₀: %.4f", beta0));
        lblBeta1.setText(String.format("β₁: %.4f", beta1));
        lblR2.setText(String.format("R²: %.4f", r2));
        lblN.setText(String.format("N: %d", n));
    }

    public void limparResultados() {
        lblBeta0.setText("β₀: --");
        lblBeta1.setText("β₁: --");
        lblR2.setText("R²: --");
        lblN.setText("N: --");
    }

    public double getLimiteOutlierPercentual() {
        return sliderOutlierPercentual.getValue();
    }

    public boolean isExcluirOutliers() {
        return toggleExcluirOutliers.isSelected();
    }

    public void exibirMensagensValidacao(String mensagem) {
        txtAreaErros.setText(mensagem);
    }

    public JSlider getSliderOutlierPercentual() {
        return sliderOutlierPercentual;
    }

    public JToggleButton getToggleExcluirOutliers() {
        return toggleExcluirOutliers;
    }
}