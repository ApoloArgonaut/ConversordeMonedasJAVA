import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ConversorMonedaAvanzado {
    private static final Map<String, String> monedas = new HashMap<>();
    private static final String API_KEY = "3228f06ae76f9d6295ddaa14";

    public static void main(String[] args) {
        inicializarMonedas();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                mostrarMenu();
                System.out.print("\nSeleccione una opción (0 para salir): ");
                int opcion = scanner.nextInt();

                if (opcion == 0) {
                    System.out.println("¡Gracias por usar el conversor!");
                    break;
                }

                realizarConversion(scanner);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                scanner.nextLine(); // Limpiar el buffer
            }
        }
        scanner.close();
    }

    private static void inicializarMonedas() {
        monedas.put("USD", "Dólar estadounidense");
        monedas.put("EUR", "Euro");
        monedas.put("GBP", "Libra esterlina");
        monedas.put("JPY", "Yen japonés");
        monedas.put("MXN", "Peso mexicano");
        monedas.put("ARS", "Peso argentino");
        monedas.put("CLP", "Peso chileno");
        monedas.put("PEN", "Sol peruano");
    }

    private static void mostrarMenu() {
        System.out.println("\n=== CONVERSOR DE MONEDAS ===");
        System.out.println("Monedas disponibles:");
        for (Map.Entry<String, String> entrada : monedas.entrySet()) {
            System.out.printf("%s - %s%n", entrada.getKey(), entrada.getValue());
        }
    }

    private static void realizarConversion(Scanner scanner) {
        try {
            System.out.print("Ingrese la moneda de origen: ");
            String monedaOrigen = scanner.next().toUpperCase();
            validarMoneda(monedaOrigen);

            System.out.print("Ingrese la moneda de destino: ");
            String monedaDestino = scanner.next().toUpperCase();
            validarMoneda(monedaDestino);

            System.out.print("Ingrese la cantidad a convertir: ");
            double cantidad = scanner.nextDouble();
            if (cantidad < 0) {
                throw new IllegalArgumentException("La cantidad no puede ser negativa");
            }

            double tasaCambio = obtenerTasaCambio(monedaOrigen, monedaDestino);
            double resultado = cantidad * tasaCambio;

            System.out.printf("%.2f %s = %.2f %s%n",
                    cantidad, monedaOrigen, resultado, monedaDestino);
            System.out.printf("Tasa de cambio: 1 %s = %.4f %s%n",
                    monedaOrigen, tasaCambio, monedaDestino);

        } catch (Exception e) {
            throw new RuntimeException("Error en la conversión: " + e.getMessage());
        }
    }

    private static void validarMoneda(String moneda) {
        if (!monedas.containsKey(moneda)) {
            throw new IllegalArgumentException("Moneda no válida: " + moneda);
        }
    }

    private static double obtenerTasaCambio(String monedaOrigen, String monedaDestino) {
        try {
            String urlStr = String.format(
                    "https://v6.exchangerate-api.com/v6/%s/pair/%s/%s",
                    API_KEY, monedaOrigen, monedaDestino);

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            return jsonResponse.getDouble("conversion_rate");

        } catch (Exception e) {
            throw new RuntimeException("Error al obtener la tasa de cambio: " + e.getMessage());
        }
    }
}