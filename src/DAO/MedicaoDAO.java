package DAO;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        List<Medicao> medicacoes = new ArrayList<>();
        erros.resetar();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            boolean primeiraLinha = true;
            
            while ((linha = reader.readLine()) != null) {

                if (primeiraLinha) {   
                    erros.registrarLinhaProcessada();
                    primeiraLinha = false;
                    continue; // pula cabeçalho
                }
                
                Medicao m = parseLinha(linha);
                if (m != null) {
                    medicacoes.add(m);
                    erros.registrarLinhaValida();
                }
            }
        }
        
        return medicacoes;
    }
    
    private Medicao parseLinha(String linha) {
        String[] campos = linha.split("\t");
        if (campos.length < 6) {
            erros.registrarErroFormato(erros.getLinhasProcessadas(), "Incompleto");
            return null;
        }
        
        try {
            LocalDateTime timestamp = LocalDateTime.parse(campos[0].trim(), FORMATTER);
            String cidade = campos[1].trim();
            double latitude = Double.parseDouble(campos[2].trim());
            double longitude = Double.parseDouble(campos[3].trim());
            double temperatura = Double.parseDouble(campos[4].trim());
            double consumoKwh = Double.parseDouble(campos[5].trim());
            
            // Validações
            if (!validarCoordenada(latitude, longitude)) {
                erros.registrarErroCoordenada(erros.getLinhasProcessadas(), "Coordenadas inválidas");
                return null;
            }
            
            if (!validarTemperatura(temperatura)) {
                erros.registrarErroTemperatura(erros.getLinhasProcessadas(), "Temperaturas inválidas");
                return null;
            }
            
            if (!validarConsumo(consumoKwh)) {
                erros.registrarErroConsumo(erros.getLinhasProcessadas(), "Consumo inválido");
                return null;
            }
            
            return new Medicao(timestamp, cidade, latitude, longitude, temperatura, consumoKwh);
            
        } catch (Exception e) {
            erros.registrarErroFormato(erros.getLinhasProcessadas(), "Erro de formato");
            return null;
        }
    }
    
    public boolean validarCoordenada(double lat, double lon) {
        return lat >= LAT_MIN && lat <= LAT_MAX && lon >= LON_MIN && lon <= LON_MAX;
    }
    
    public boolean validarTemperatura(double temp) {
        return temp >= TEMP_MIN && temp <= TEMP_MAX;
    }
    
    public boolean validarConsumo(double consumo) {
        return consumo >= 0;
    }
    
    public ErroValidacao getErros() {
        return erros;
    }

    public LocalDateTime parseTimestamp(String str) {
        try {
            return LocalDateTime.parse(str);
        } catch (Exception e) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US);
            return LocalDateTime.parse(str, formatter);
        }
    }

}