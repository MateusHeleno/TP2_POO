package DAO;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


import java.util.ArrayList;
import java.util.List;

import model.ErroValidacao;
import model.Medicao;

public class MedicaoDAO {
    private ErroValidacao erros;
    private static final DateTimeFormatter FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // Limites de validação
    private static final double LAT_MIN = -90.0;
    private static final double LAT_MAX = 90.0;
    private static final double LON_MIN = -180.0;
    private static final double LON_MAX = 180.0;
    private static final double TEMP_MIN = -50.0;
    private static final double TEMP_MAX = 60.0;
    
    //Crio um objeto via construtor e crio um objeto de erros de validação
    public MedicaoDAO() {
        this.erros = new ErroValidacao();
    }
    
    public List<Medicao> carregarDeTSV(File arquivo) throws IOException {
        List<Medicao> medicoes = new ArrayList<>();
        erros.resetar();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            int numeroLinha = 1;
            
            while ((linha = reader.readLine()) != null) {
                if (numeroLinha == 1) {   
                    numeroLinha++;
                    continue; // pula cabeçalho
                }
                erros.registrarLinhaProcessada();
                
                Medicao m = parseLinha(linha,numeroLinha);
                if (m != null) {
                    medicoes.add(m);
                    erros.registrarLinhaValida();
                }

                numeroLinha++;
            }
        }
        
        return medicoes;
    }
    
    private Medicao parseLinha(String linha, int numeroLinha) {
        String[] campos = linha.split("\t");
        if (campos.length < 6) {
            erros.registrarErroFormato(numeroLinha, "Incompleto");
            return null;
        }
        
        try {
            LocalDateTime timestamp = parseTimestamp(campos[0].trim());
            String cidade = campos[1].trim();
            double latitude = Double.parseDouble(campos[2].trim());
            double longitude = Double.parseDouble(campos[3].trim());
            double temperatura = Double.parseDouble(campos[4].trim());
            double consumoKwh = Double.parseDouble(campos[5].trim());
            
            // Validações
            if (!validarCoordenada(latitude, longitude)) {
                erros.registrarErroCoordenada(numeroLinha, "Coordenadas inválidas");
                return null;
            }
            
            if (!validarTemperatura(temperatura)) {
                erros.registrarErroTemperatura(numeroLinha, "Temperaturas inválidas");
                return null;
            }
            
            if (!validarConsumo(consumoKwh)) {
                erros.registrarErroConsumo(numeroLinha, "Consumo inválido");
                return null;
            }
            
            return new Medicao(timestamp, cidade, latitude, longitude, temperatura, consumoKwh);
            
        } catch (NumberFormatException e) {
            erros.registrarErroFormato(numeroLinha,"Número inválido: " + e.getMessage());
            return null;
        }catch (DateTimeParseException e) {
            erros.registrarErroFormato(numeroLinha,"Data inválida: " + e.getMessage());
            return null;
        }catch (Exception e) {
            erros.registrarErroFormato(numeroLinha, "Erro inesperado: " + e.getClass().getSimpleName());
            return null;
        }
    }
    
    private boolean validarCoordenada(double lat, double lon) {
        return lat >= LAT_MIN && lat <= LAT_MAX && lon >= LON_MIN && lon <= LON_MAX;
    }
    
    private boolean validarTemperatura(double temp) {
        return temp >= TEMP_MIN && temp <= TEMP_MAX;
    }
    
    private boolean validarConsumo(double consumo) {
        return consumo >= 0;
    }
    
    public ErroValidacao getErros() {
        return erros;
    }

    public static LocalDateTime parseTimestamp(String str) {
        return LocalDateTime.parse(str, FORMATTER);
    }

}