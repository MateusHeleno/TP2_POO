package model;

import java.time.LocalDateTime;

public class Medicao {
    private LocalDateTime timestamp;
    private String cidade;
    private double latitude;
    private double longitude;
    private double temperatura;
    private double consumoKwh;
    private double consumoPrevisto; // consumo previsto = b0 + b1 * temperatura
    private double residuoPercentual;

    public Medicao(LocalDateTime timestamp, String cidade, double latitude, double longitude, double temperatura, double consumoKwh) {
        setTimestamp(timestamp);
        setCidade(cidade);
        setLatitude(latitude);
        setLongitude(longitude);
        setTemperatura(temperatura);
        setConsumoKwh(consumoKwh);
        this.consumoPrevisto = 0.0;
    }

    public Medicao() {}

    // Getters e Setters

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp; 
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setTemperatura(double temperatura) {
        this.temperatura = temperatura;
    }

    public void setConsumoKwh(double consumoKwh) {
        this.consumoKwh = consumoKwh;
    }

    public void setConsumoPrevisto(double consumoPrevisto) {
        this.consumoPrevisto = consumoPrevisto;
    }

    public void setResiduoPercentual(double residuoPercentual) {
        this.residuoPercentual = residuoPercentual;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getCidade() {
        return cidade; 
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getTemperatura() {
        return temperatura;
    }

    public double getConsumoKwh() {
        return consumoKwh;
    }

    public double getConsumoPrevisto() {
        return consumoPrevisto;
    }

    public double getResiduoPercentual() {
        return residuoPercentual;
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %.4f | %.4f | %.2f°C | %.2fkWh",
                timestamp, cidade, latitude, longitude, temperatura, consumoKwh);
    }
}