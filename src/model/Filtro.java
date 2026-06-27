package model;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import model.Medicao;
import util.CalculoDistancia;

public class Filtro {
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private Double tempMin;
    private Double tempMax;
    private Double latitudeCentro;
    private Double longitudeCentro;
    private Double raioKm;

    public Filtro() {
        limpar();
    };

    public List<Medicao> aplicarFiltros(List<Medicao> medicoes) {
        List<Medicao> res = new ArrayList<>(medicoes);

        res = aplicarFiltroTempo(res);
        res = aplicarFiltroTemperatura(res);
        res = aplicarFiltroRaio(res);
        
        return res;
    }

    // Mostra medições no intervalo entre dataInicio e dataFim;
    public List<Medicao> aplicarFiltroTempo(List<Medicao> medicoes) {
        if (dataInicio == null && dataFim == null)
            return medicoes;

        return medicoes.stream()
            .filter(n -> {
                LocalDateTime atual = n.getTimestamp();
                boolean dpsComeco = (dataInicio == null) || (!atual.isBefore(dataInicio)); // Usando a negação ! fazemos com que o filtro seja inclusivo
                boolean antesFim  = (dataFim == null) || (!atual.isAfter(dataFim));
                return dpsComeco && antesFim;
            })
            .collect(Collectors.toList());
    }

    public List<Medicao> aplicarFiltroTemperatura(List<Medicao> medicoes) {
        if (tempMin == null && tempMax == null)
            return medicoes;
        
        return medicoes.stream()
            .filter(n -> {
                Double temp = n.getTemperatura();
                boolean tempMaior = (tempMin == null) || (temp >= tempMin);
                boolean tempMenor = (tempMax == null) || (temp <= tempMax);
                return tempMaior && tempMenor;
            })
            .collect(Collectors.toList());
    }

    public List<Medicao> aplicarFiltroRaio(List<Medicao> medicoes) {
        if (latitudeCentro == null || longitudeCentro == null)
            return medicoes; 

        return medicoes.stream()
            .filter(n -> {
                Double raio = CalculoDistancia.calcular(
                    latitudeCentro, longitudeCentro, n.getLatitude(), n.getLongitude()
                );
                return raio <= raioKm;
            })
            .collect(Collectors.toList());
    }

    public void limpar() {
        dataInicio = null;
        dataFim = null;
        tempMin = null;
        tempMax = null;
        latitudeCentro = null;
        longitudeCentro = null;
        raioKm = null;
    }

    public LocalDateTime getDataInicio() { 
        return dataInicio;
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio; 
    }

    public LocalDateTime getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDateTime dataFim) {
        this.dataFim = dataFim;
    }

    public Double getTempMin() { 
        return tempMin;
    }

    public void setTempMin(Double tempMin) { 
        this.tempMin = tempMin;
    }

    public Double getTempMax() {
        return tempMax;     
    }

    public void setTempMax(Double tempMax) { 
        this.tempMax = tempMax;
    }
    
    public Double getLatitudeCentro() {
        return latitudeCentro;
    }

    public void setLatitudeCentro(Double latitudeCentro) { 
        this.latitudeCentro = latitudeCentro;
    }

    public Double getLongitudeCentro() {
        return longitudeCentro;
    }

    public void setLongitudeCentro(Double longitudeCentro) { 
        this.longitudeCentro = longitudeCentro; 
    }

    public Double getRaioKm() { 
        return raioKm;
    }

    public void setRaioKm(Double raioKm) {
        this.raioKm = raioKm;
    }
}