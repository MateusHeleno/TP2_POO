package model;

import java.util.ArrayList;
import java.util.List;

public class ErroValidacao {
    private int errosCoordenada;
    private int errosTemperatura;
    private int errosConsumo;
    private int errosFormato;
    private int linhasProcessadas;
    private int linhasValidas;
    private List<String> mensagensErro;


    public ErroValidacao(){
        this.mensagensErro = new ArrayList<>();
        resetar();
    }

    public void resetar(){
        this.errosCoordenada = 0;
        this.errosTemperatura = 0;
        this.errosConsumo = 0;
        this.errosFormato = 0;
        this.linhasProcessadas = 0;
        this.linhasValidas = 0;
        this.mensagensErro.clear();
    }

    public void registrarLinhaProcessada(){ // para usar no enuciado
        this.linhasProcessadas++;
    }

    public void registrarLinhaValida() {
        registrarLinhaProcessada();
        this.linhasValidas++;
    }

    public void registrarErroCoordenada(int numeroLinha, String detalhe) {
        registrarLinhaProcessada();
        this.errosCoordenada++;
        this.mensagensErro.add("Linha " + numeroLinha + " [Coordenada]: " + detalhe);
    }

    public void registrarErroTemperatura(int numeroLinha, String detalhe) {
        registrarLinhaProcessada();
        this.errosTemperatura++;
        this.mensagensErro.add("Linha " + numeroLinha + " [Temperatura]: " + detalhe);
    }

    public void registrarErroConsumo(int numeroLinha, String detalhe) {
        registrarLinhaProcessada();
        this.errosConsumo++;
        this.mensagensErro.add("Linha " + numeroLinha + " [Consumo]: " + detalhe);
    }

    public void registrarErroFormato(int numeroLinha, String detalhe) {
        registrarLinhaProcessada();
        this.errosFormato++;
        this.mensagensErro.add("Linha " + numeroLinha + " [Formato]: " + detalhe);
    }

    public int getLinhasProcessadas(){
        return linhasProcessadas;
    }
    
    public int getLinhasValidas(){
        return linhasValidas;
    }

    public int getLinhasRejeitadas(){
        return getLinhasProcessadas() - getLinhasValidas();
    }

    public boolean temErros() {
        return getLinhasRejeitadas() > 0;
    }

    public String getMensagemResumo() {
        StringBuilder sb = new StringBuilder();

        sb.append("== RESUMO DA IMPORTAÇÃO ==\n");
        sb.append("Linhas processadas: ").append(linhasProcessadas).append("\n");
        sb.append("Linhas válidas: ").append(linhasValidas).append("\n");
        sb.append("Linhas rejeitadas: ").append(getLinhasRejeitadas()).append("\n\n");

        if (temErros()){
            sb.append("Erros encontrados:\n");
            sb.append("- Coordenadas inválidas: ").append(errosCoordenada).append("\n");
            sb.append("- Temperaturas inválidas: ").append(errosTemperatura).append("\n");
            sb.append("- Consumos inválidos: ").append(errosConsumo).append("\n");
            sb.append("- Erros de formato: ").append(errosFormato).append("\n");
        
            sb.append("== Amostras dos Primeiros Erros ==\n");

            int tamanhoMensagem = mensagensErro.size(); // pra não percorrer mais de uma vez
            int limiteExibicao = Math.min(tamanhoMensagem, 5); // fica com o menor valor
            for (int i = 0; i< limiteExibicao; i++){
                sb.append(mensagensErro.get(i)).append("\n");
            }

            if(tamanhoMensagem > 5)
                sb.append("... e mais ").append(tamanhoMensagem - 5).append(" erro(s) omitido(s).\n");
        }

        return sb.toString();
    }
}