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

    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public void setLatitude(double latitude) {
        if (latitude < -90 || latitude > 90)
            throw new IllegalArgumentException("Latitude tem que estar entre -90 e 90");
        else
            this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        if (longitude < -180 || longitude > 180)
            throw new IllegalArgumentException("Longitude tem que estar entre -180 e 180");
        else
            this.longitude = longitude;
    }

    public void setTemperatura(double temperatura) {
        if (temperatura < -50 || temperatura > 60)
            throw new IllegalArgumentException("Temperatura tem que estar entre -50 e 60");
        else
            this.temperatura = temperatura;
    }

    public void setConsumoKwh(double consumoKwh) {
        if (consumoKwh < 0)
            throw new IllegalArgumentException("Consumo não pode ser negativo");
        else
            this.consumoKwh = consumoKwh;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getCidade() {return cidade; }

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
}