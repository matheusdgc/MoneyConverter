import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class main {
    private static final String API_KEY = "f1df2251304ecb264b53ff3c";

    // Método para retornar o código da moeda baseado na escolha do usuário
    private static String getBaseCode(int choice) {
        switch (choice) {
            case 1:
            case 3:
                return "BRL"; // Real Brasileiro
            case 2:
            case 5:
                return "USD"; // Dolar Americano
            case 4:
            case 6:
                return "JPY"; // Iene Japones
            default:
                return "null";
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));


        System.out.println("_____________________________________________");
        System.out.println("Bem Vindo/a ao Conversor de Moedas:");
        System.out.println("1. Real Brasileiro para Dolar Americano");
        System.out.println("2. Dolar Americano para Real Brasileiro");
        System.out.println("3. Real Brasileiro para Iene Japones");
        System.out.println("4. Iene Japones para Real Brasileiro");
        System.out.println("5. Dolar Americano para Iene Japones");
        System.out.println("6. Iene Japones para Dolar Americano");
        System.out.println("_____________________________________________");
        System.out.print("Escolha a opção: ");



        int choice = Integer.parseInt(inputReader.readLine());

        if (choice < 1 || choice > 6) {
            System.out.println("Opção inválida. Por favor, escolha uma opção entre 1 e 6.");
            return; // Exit
        }

        final String BASE_CODE = getBaseCode(choice);

        URL url = new URL("https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/" + BASE_CODE);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        reader.close();

        JsonParser jp = new JsonParser();
        JsonElement root = jp.parse(builder.toString());
        JsonObject jsonobj = root.getAsJsonObject();

        if (jsonobj.has("conversion_rates")) {
            JsonObject rates = jsonobj.getAsJsonObject("conversion_rates");

            System.out.println("_____________________________________________");
            System.out.print("Digite o valor a ser convertido: ");
            double amount = Double.parseDouble(inputReader.readLine());

            double result;

            switch (choice) {
                case 1:
                    result = amount * rates.get("USD").getAsDouble();
                    break;
                case 2:
                    result = amount / rates.get("USD").getAsDouble();
                    break;
                case 3:
                    result = amount * rates.get("JPY").getAsDouble();
                    break;
                case 4:
                    result = amount / rates.get("JPY").getAsDouble();
                    break;
                case 5:
                    result = amount * rates.get("JPY").getAsDouble() / rates.get("USD").getAsDouble();
                    break;
                case 6:
                    result = amount * rates.get("USD").getAsDouble() / rates.get("JPY").getAsDouble();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + choice);
            };

            // The result
            System.out.println("_____________________________________________");
            System.out.printf("O resultado é: %.2f%n", result);

            connection.disconnect();

        } else {
            System.out.println("Erro ao obter as taxas de câmbio.");
            connection.disconnect();
            return;
        }
    }
}