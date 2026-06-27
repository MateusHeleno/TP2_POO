package model;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

            for (Medicao m : medicoes) {
                writer.printf("%s\t%s\t%.6f\t%.6f\t%.2f\t%.2f\t%.4f\t%.2f%n",
                        m.getTimestamp(), m.getCidade(), m.getLatitude(), m.getLongitude(),
                        m.getTemperatura(), m.getConsumoKwh(),
                        m.getConsumoPrevisto(), m.getResiduoPercentual());
            }
        }
    }

    // Quando estiver lendo do arquivo, faz o parse de cada linha do arquivo e transforma em um objeto Medicao
    private Medicao parseLinha(String linha, DateTimeFormatter FORMATTER) {
        String[] campos = linha.split("\t");
        if (campos.length < 6) {
            // incrementar erros
            return null;
        }

        try {
            LocalDateTime timestamp = LocalDateTime.parse(campos[0].trim(), FORMATTER);
            String cidade = campos[1].trim();
            double latitude = Double.parseDouble(campos[2].trim());
            double longitude = Double.parseDouble(campos[3].trim());
            double temperatura = Double.parseDouble(campos[4].trim());
            double consumoKwh = Double.parseDouble(campos[5].trim());

            // validações...

            return new Medicao(timestamp, cidade, latitude, longitude, temperatura, consumoKwh);
        } catch (Exception e) {
            // incrementar erros
            return null;
        }
    }
}

