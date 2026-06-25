package model;

import java.util.*;
import java.math.*;

/*
    y = b0 + b1(x)
    y = variável dependente
    x = variável independente (fator que influência a predição)
    b0 = intercepto (valor de y quando x = 0)
    b1 = inclinação

    b0 e b1 são calculados pelo método dos mínimos quadrados
*/
public class RegressaoLinear {
    private double y; // Consumo Kwh
    private double x; // Temperatura Externa
    private double beta0;
    private double beta1;
    private int n;
    private double r2;
    private List<Medicao> medicoes;

    public RegressaoLinear() {
        this.beta0 = 0.0;
        this.beta1 = 0.0;
        this.n = 0;
    }

    /*
        Função que calcula o valor de beta0 e beta1
        Se beta1 > 0, isso indica a presença de uma relação diretamente proporcional entre temperatura e consumo.
        Se beta1 < 0, isso indica a presença de uma relação inversamente proporcional entre temperatura e consumo.
     */
    private void calcularCoeficientes() {
        double somaX = 0, somaY = 0;
        double somaX2 = 0, somaXY = 0;

        for (Medicao dado : medicoes) {
            somaX += dado.getTemperatura();
            somaY += dado.getConsumoKwh();
            somaXY += somaX * somaY;
            somaX2 += Math.pow(somaX, 2);
            n++;
        }

        double numerador = (n * somaXY) - (somaY * somaX);
        double denominador = (n * somaX2) - (Math.pow(somaX, 2));

        if (denominador != 0) {
            beta1 = numerador / denominador;
            beta0 = (somaY - (beta1 * somaY)) / n;
        }
    }

    private void calcularR2() {
        double somaY = 0;
        for (Medicao dado : medicoes)
            somaY += dado.getConsumoKwh();
        double mediaY = somaY / n;

        double somaResiduos = 0;
        double somaTotal = 0;
    }

    public double calcularConsumoPrevisto(double temperatura) {
        return beta0 + beta1 * temperatura;
    }

    // Getters. Não tem Setters porque os valores devem ser calculados pela própria classe

    public double getBeta0() {
        return beta0;
    }

    public double getBeta1() {
        return beta1;
    }

    public double getR2() {
        return r2;
    }
}
