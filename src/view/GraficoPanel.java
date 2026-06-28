package view;
import javax.swing.*;

import model.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GraficoPanel extends JPanel {

    private static final int MARGEM_ESQUERDA = 60;
    private static final int MARGEM_DIREITA = 40;
    private static final int MARGEM_TOPO = 40;
    private static final int MARGEM_BASE = 60;

    private static final Color COR_PONTO = Color.BLACK;
    private static final Color COR_OUTLIER = Color.RED;
    private static final Color COR_RETA = Color.BLUE;

    private List<Medicao> dados;
    private RegressaoLinear regressao;
    private double limiteOutlier;

    private double minTemp;
    private double maxTemp;
    private double minConsumo;
    private double maxConsumo;

    public GraficoPanel() {
        this.dados = new ArrayList<>();
        this.limiteOutlier = 10.0;

        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 500));
    }

    public void setDados(List<Medicao> dados, RegressaoLinear regressao) {
        atualizarDados(dados, regressao, this.limiteOutlier);
    }

    public void atualizarDados(List<Medicao> dados,  RegressaoLinear regressao, double limiteOutlier) {
        this.dados = dados != null ? new ArrayList<>(dados) : new ArrayList<>();
        this.regressao = regressao;
        this.limiteOutlier = limiteOutlier;

        calcularLimites();
        repaint();
    }

    private void calcularLimites() {
        if (dados.isEmpty()) {
            return;
        }

        minTemp = dados.stream().mapToDouble(Medicao::getTemperatura).min().orElse(0);
        maxTemp = dados.stream().mapToDouble(Medicao::getTemperatura).max().orElse(1);
        minConsumo = dados.stream().mapToDouble(Medicao::getConsumoKwh).min().orElse(0);
        maxConsumo = dados.stream().mapToDouble(Medicao::getConsumoKwh).max().orElse(1);

        if (minTemp == maxTemp) {
            minTemp -= 1;
            maxTemp += 1;
        }

        if (minConsumo == maxConsumo) {
            minConsumo -= 1;
            maxConsumo += 1;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        if (dados == null || dados.isEmpty()) {
            desenharMensagemSemDados(g2);
            g2.dispose();
            return;
        }

        desenharEixos(g2);
        desenharPontos(g2);

        if (regressao != null && regressao.getN() >= 2) {
            desenharRetaRegressao(g2);
            desenharEquacao(g2);
        }

        desenharLegenda(g2);

        g2.dispose();
    }

    private void desenharEixos(Graphics2D g2) {
        int largura = getWidth();
        int altura = getHeight();

        int xInicio = MARGEM_ESQUERDA;
        int xFim = largura - MARGEM_DIREITA;
        int yInicio = MARGEM_TOPO;
        int yFim = altura - MARGEM_BASE;

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));

        g2.drawLine(xInicio, yFim, xFim, yFim);
        g2.drawLine(xInicio, yInicio, xInicio, yFim);

        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString("Temperatura (°C)", largura / 2 - 50, altura - 15);

        g2.rotate(-Math.PI / 2);
        g2.drawString("Consumo (kWh)", -altura / 2 - 40, 20);
        g2.rotate(Math.PI / 2);

        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        g2.drawString(FormatarNumero.umaCasaDecimal(minTemp),xInicio,yFim + 15);
        g2.drawString(FormatarNumero.umaCasaDecimal(maxTemp),xFim - 30,yFim + 15);

        g2.drawString(FormatarNumero.umaCasaDecimal(minConsumo),xInicio - 45,yFim);
        g2.drawString(FormatarNumero.umaCasaDecimal(maxConsumo),xInicio - 45,yInicio + 5);

    }

    private void desenharPontos(Graphics2D g2) {
        for (Medicao m : dados) {
            int x = temperaturaParaX(m.getTemperatura());
            int y = consumoParaY(m.getConsumoKwh());

            boolean outlier =
                    Math.abs(m.getResiduoPercentual()) > limiteOutlier;

            g2.setColor(outlier ? COR_OUTLIER : COR_PONTO);
            g2.fillOval(x - 4, y - 4, 8, 8);
        }
    }

    private void desenharRetaRegressao(Graphics2D g2) {
        double x1 = minTemp;
        double x2 = maxTemp;

        double y1 = regressao.preverConsumo(x1);
        double y2 = regressao.preverConsumo(x2);

        int px1 = temperaturaParaX(x1);
        int py1 = consumoParaY(y1);
        int px2 = temperaturaParaX(x2);
        int py2 = consumoParaY(y2);

        g2.setColor(COR_RETA);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(px1, py1, px2, py2);
    }

    private void desenharEquacao(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Monospaced", Font.PLAIN, 12));

        String eq = String.format(
                "y = %.4fx + %.4f",
                regressao.getBeta1(),
                regressao.getBeta0()
        );

        String r2 = "R² = " + FormatarNumero.decimal(regressao.getR2());

        g2.drawString(eq, MARGEM_ESQUERDA + 10, MARGEM_TOPO + 20);
        g2.drawString(r2, MARGEM_ESQUERDA + 10, MARGEM_TOPO + 40);
    }

    private void desenharLegenda(Graphics2D g2) {
        int x = getWidth() - MARGEM_DIREITA - 120;
        int y = MARGEM_TOPO;

        g2.setFont(new Font("Arial", Font.PLAIN, 10));

        g2.setColor(COR_PONTO);
        g2.fillOval(x, y, 8, 8);
        g2.drawString("Normal", x + 12, y + 8);

        g2.setColor(COR_OUTLIER);
        g2.fillOval(x, y + 20, 8, 8);
        g2.drawString("Outlier", x + 12, y + 28);

        g2.setColor(COR_RETA);
        g2.drawLine(x, y + 45, x + 20, y + 45);
        g2.drawString("Regressão", x + 24, y + 49);
    }

    private void desenharMensagemSemDados(Graphics2D g2) {
        g2.setColor(Color.GRAY);
        g2.setFont(new Font("Arial", Font.BOLD, 14));

        String msg = "Sem dados para exibir";

        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(msg)) / 2;
        int y = getHeight() / 2;

        g2.drawString(msg, x, y);
    }

    private int temperaturaParaX(double temperatura) {
        int larguraUtil = getWidth() - MARGEM_ESQUERDA - MARGEM_DIREITA;

        return MARGEM_ESQUERDA +
                (int) ((temperatura - minTemp) / (maxTemp - minTemp) * larguraUtil);
    }

    private int consumoParaY(double consumo) {
        int alturaUtil = getHeight() - MARGEM_TOPO - MARGEM_BASE;

        return getHeight() - MARGEM_BASE -
                (int) ((consumo - minConsumo) / (maxConsumo - minConsumo) * alturaUtil);
    }
}

