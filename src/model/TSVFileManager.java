package model;

import model.ErroValidacao;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import DAO.MedicaoDAO;

public class TSVFileManager {
    private final File file;

    public TSVFileManager(String filepath) {
        if (!filepath.endsWith(".tsv"))
            filepath += ".tsv";

        this.file = new File(filepath);
    }

    public void escreverTSV(List<Medicao> medicoes) throws IOException {
        try (PrintWriter writer = new PrintWriter(file)) {
            // Escreve o header do arquivo TSV
            writer.println("timestamp\tcidade\tlatitude\tlongitude\ttemperatura\tconsumoKwh\tconsumoPrevisto\tresiduoPercentual");

            for (Medicao m : medicoes) 
                writer.printf("%s\t%s\t%.6f\t%.6f\t%.2f\t%.2f\t%.4f\t%.2f%n",
                        m.getTimestamp(), m.getCidade(), m.getLatitude(), m.getLongitude(),
                        m.getTemperatura(), m.getConsumoKwh(),
                        m.getConsumoPrevisto(), m.getResiduoPercentual());
        }
    }
}