package model;

import java.util.*;

/*
    y = b0 + b1(x)
    y = variável dependente
    x = variável independente (fator que influência a predição)
    b0 = intercepto (valor de y quando x = 0)
    b1 = inclinação

    b0 e b1 são calculados pelo método dos mínimos quadrados
*/
public class RegressaoLinear {
    private double beta0;
    private double beta1;
    private int n;
    private double r2;
    private List<Medicao> medicoes;

    public RegressaoLinear() {
        this.beta0 = 0.0;
        this.beta1 = 0.0;
        this.r2 = 0.0;
        this.n = 0;
    }

    // Essa função seria semelhante ao .fit() presente na biblioteca sklearn do Python, onde o modelo calcula as variáveis internas minimizando os erros
    public boolean treinar(List<Medicao> medicoes) {
        this.medicoes = medicoes;
        this.n = medicoes.size();

        if (n < 2)
            return false;

        calcularCoeficientes();
        calcularR2();
        calcularResiduoPercentual();

        return true;
    }

    // Todas as funções abaixo são private pois servem como funções auxiliares à função pública. Elas são privadas pois não devem ser chamadas fora do escopo da classe

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
            somaXY += dado.getTemperatura() * dado.getConsumoKwh();
            somaX2 += Math.pow(dado.getTemperatura(), 2);
        }

        double numerador = (n * somaXY) - (somaY * somaX);
        double denominador = (n * somaX2) - (Math.pow(somaX, 2));

        if (denominador != 0) {
            beta1 = numerador / denominador;
            beta0 = (somaY - (beta1 * somaX)) / n;
        }
    }

    private void calcularR2() {
        double somaY = 0;
        for (Medicao dado : medicoes)
            somaY += dado.getConsumoKwh();
        double mediaY = somaY / n;

        double somaResiduos = 0;
        double somaTotal = 0;

        for (Medicao dado : medicoes) {
            double consumoPrevisto = preverConsumo(dado.getTemperatura());
            somaResiduos += Math.pow(dado.getConsumoKwh() - consumoPrevisto, 2);
            somaTotal += Math.pow(dado.getConsumoKwh() - mediaY, 2);
        }

        if (somaTotal != 0) {
            r2 = 1 - (somaResiduos / somaTotal);
        }
        else
            r2 = 0;
    }

    private void calcularResiduoPercentual() {
        for (Medicao dado : medicoes) {
            double consumoPrevisto = preverConsumo(dado.getTemperatura());
            if (consumoPrevisto != 0) {
                double resPercentual = ((dado.getConsumoKwh() - consumoPrevisto) / consumoPrevisto) * 100;
                dado.setResiduoPercentual(resPercentual);
            }
            else
                dado.setResiduoPercentual(0);
        }
    }

    public double preverConsumo(double temperatura) {
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

    public int getN() {
        return  n;
    }
}
