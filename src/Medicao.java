import java.util.*;
import java.time.*;

public class Medicao {
    private LocalDateTime timestamp;
    private String cidade;
    private double latitude;
    private double longitude;
    private double temperatura;
    private double consumoKwh;

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public void setLatitude(double latitude) {
        if (latitude < -90 || latitude > 90) {
            System.out.println("Latitude tem que estar entre -90 e 90. Salvando valor como 0");
            this.latitude = 0;
        }
        else
            this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        if (longitude < -180 || longitude > 180) {
            System.out.println("Longitude tem que estar entre -180 e 180. Salvando valor como 0");
            this.longitude = 0;
        }
        else
            this.longitude = longitude;
    }

    public void setTemperatura(double temperatura) {
        if (temperatura < -50 || temperatura > 60) {
            System.out.println("Valor fora da faixa razoável de temperatura. Salvando como 0");
            this.temperatura = 0;
        }
        else
            this.temperatura = temperatura;
    }

    public void setConsumoKwh(double consumoKwh) {
        if (consumoKwh < 0) {
            System.out.println("Consumo não pode ser negativo. Salvando como 0");
            this.consumoKwh = 0;
        }
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