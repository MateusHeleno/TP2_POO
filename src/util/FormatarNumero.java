package util;

public final class FormatarNumero {

    private FormatarNumero() {
        // Impede instanciação
    }

    public static String temperatura(double valor) {
        return String.format("%.2f °C", valor);
    }

    public static String consumo(double valor) {
        return String.format("%.2f", valor);
    }

    public static String percentual(double valor) {
        return String.format("%.2f%%", valor);
    }

    public static String decimal(double valor) {
        return String.format("%.4f", valor);
    }

    public static String umaCasaDecimal(double valor) {
        return String.format("%.1f", valor);
    }

}