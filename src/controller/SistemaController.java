package controller;

import model.*;
import view.*;
import DAO.MedicaoDAO;
import view.MainFrame;
import view.OutlierTableCellRenderer;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.*;

public class SistemaController {
    private MedicaoDAO dao;
    private TabelaModel tableModel;
    private Filtro filtroAtual;
    private RegressaoLinear regressaoAtual;
    private List<Medicao> dadosOriginais;
    private List<Medicao> dadosFiltrados;
    private MainFrame view; 

    public SistemaController(MainFrame view){
        this.view = view;
        //configurarEventos();
    }
}
