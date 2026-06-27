package view;

import model.Filtro;
import controller.SistemaController;

import java.awt.FlowLayout;
import javax.swing.*;
import java.time.*;

public class FiltrosPanel extends JPanel {
    private JSpinner spinnerDataInicio;
    private JSpinner spinnerDataFim;
    private JSlider sliderTempMin;
    private JSlider sliderTempMax;
    private JTextField latitude;
    private JTextField longitude;
    private JSpinner spinnerRaio;
    private JButton limparFiltrosBtn;
    private Filtro filtro;

    public FiltrosPanel() {
        this.filtro = new Filtro();
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Tempo
        JPanel pTempo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pTempo.setBorder(BorderFactory.createTitledBorder("Intervalo de Tempo"));

        SpinnerDateModel modelInicio = new SpinnerDateModel();
        spinnerDataInicio = new JSpinner(modelInicio);
        spinnerDataInicio.setEditor(new JSpinner.DateEditor(spinnerDataInicio, "yyyy-MM-dd HH:mm:ss"));

        SpinnerDateModel modelFim = new SpinnerDateModel();
        spinnerDataFim = new JSpinner(modelFim);
        spinnerDataFim.setEditor(new JSpinner.DateEditor(spinnerDataFim, "yyyy-MM-dd HH:mm:ss"));

        pTempo.add(new JLabel("Início: "));
        pTempo.add(spinnerDataInicio);
        pTempo.add(new JLabel("Fim: "));
        pTempo.add(spinnerDataFim);

        // Temperatura
        JPanel pTemp = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pTemp.setBorder(BorderFactory.createTitledBorder("Intervalo de Temperatura: "));

        sliderTempMin = new JSlider(-50, 60, -50);
        sliderTempMax = new JSlider(-50, 60, 60);

        sliderTempMin.setMajorTickSpacing(10);
        sliderTempMin.setPaintTicks(true);
        sliderTempMax.setMajorTickSpacing(10);
        sliderTempMax.setPaintTicks(true);

        pTemp.add(new JLabel("Min: "));
        pTemp.add(sliderTempMin);
        pTemp.add(new JLabel("Max: "));
        pTemp.add(sliderTempMax);

        // Raio
        JPanel pRaio = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pRaio.setBorder(BorderFactory.createTitledBorder("Raio a partir de coordenada: "));

        latitude = new JTextField(10);
        longitude = new JTextField(10);
        spinnerRaio = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1000.0, 10.0));

        pRaio.add(new JLabel("Latitude: "));
        pRaio.add(latitude);
        pRaio.add(new JLabel("Longitude: "));
        pRaio.add(longitude);
        pRaio.add(new JLabel("Raio: "));
        pRaio.add(spinnerRaio);

        limparFiltrosBtn = new JButton("Limpar filtros: ");
        add(pTempo);
        add(pTemp);
        add(pRaio);
        add(limparFiltrosBtn);
    }

    public Filtro getFiltro() {
        return filtro;
    }
    
    public void limparCampos() {
        filtro.limpar();
    }
    
    public double getTempMin() {
        return sliderTempMin.getValue();
    }
    
    public double getTempMax() {
        return sliderTempMax.getValue();
    }

    public JButton getLimparFiltrosBtn() {
        return limparFiltrosBtn;
    }

    public JSpinner getSpinnerDataInicio() {
        return spinnerDataInicio;
    }

    public JSpinner getSpinnerDataFim() {
        return spinnerDataFim;
    }

    public JSpinner getSpinnerRaio() {
        return spinnerRaio;
    }

    public JSlider getSliderTempMin() {
        return sliderTempMin;
    }
    
    public JSlider getSliderTempMax() {
        return sliderTempMax;
    }

    public JTextField getLongitudeTxt() {
        return longitude;
    }

    public JTextField getLatitudeTxt() {
        return latitude;
    }
}