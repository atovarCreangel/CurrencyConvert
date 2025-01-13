import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.NumberFormat;
import java.time.Duration;
import java.util.Locale;
import java.util.Scanner;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CurrencyConvert {

    private static final String [] currencyCodeFront = {"ARS (Peso Argentino)", "BOB (Peso Boliviano)", "BRL (Real brasileño)", "CLP (Peso Chileno)", "COP (Peso Colombiano)", "EUR (Euro)", "MXN (Peso Mexicano)", "PYG (Guaraní Paraguayo)", "PEN (Sol Peruano)", "USD (Dólar estadounidense)", "UYU (Peso Uruguayo)", "VEF (Bolívar Venezolano)"};
    private static final String [] currencyCode = {"ARS", "BOB", "BRL", "CLP", "COP", "EUR", "MXN", "PYG", "PEN", "USD", "UYU", "VEF"};

    private static int typeIn = 0;
    private  static int typeOut = 0;

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static void main(String[] args) {

        Scanner s = new Scanner(System.in);

        System.out.println("##############################################");
        System.out.println("Bienvenido al sistema de conversión de monedas");
        System.out.println("##############################################\n");

        System.out.println("Por favor seleccione el tipo de moneda base...");

        for (int i = 0; i < currencyCodeFront.length; i++) {
            System.out.println((i+1)+") " + currencyCodeFront[i]);
        }

        if(s.hasNextInt()){
            typeIn = s.nextInt();
            if(!validateEntry(typeIn)){
                System.out.println("Seleccione un tipo válido...");
                main(args);
                System.exit(0);
            }
        }else {
            System.out.println("Seleccione un tipo válido...");
            main(args);
            System.exit(0);
        }

        System.out.println("Ingrese el valor a convertir");

        double valor = 0.0;
        if(s.hasNextDouble()){
            valor = s.nextDouble();
        }else{
            System.out.println("Ingrese un valor correcto...");
            main(args);
            System.exit(0);
        }

        System.out.println("Por favor seleccione el tipo de moneda al que desea convertir...");

        for (int i = 0; i < currencyCodeFront.length; i++) {
            System.out.println((i+1)+") " + currencyCodeFront[i]);
        }

        if(s.hasNextInt()){
            typeOut = s.nextInt();
            if(!validateEntry(typeOut)){
                System.out.println("Seleccione un tipo válido...");
                main(args);
                System.exit(0);
            }
        }else {
            System.out.println("Seleccione un tipo válido...");
            main(args);
            System.exit(0);
        }

        double resp = sendCurrencyConvertApi(currencyCode[(typeIn-1)], currencyCode[(typeOut-1)]);
        resp = resp * valor;
        Locale locale = getLocale(currencyCode[(typeOut-1)]);   
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);

        System.out.println("La conversión del valor es: " + currencyFormatter.format(resp) + " " + currencyCodeFront[(typeOut-1)]);

        s.close();
    }

    private static boolean validateEntry(int entry){
        boolean isValid = false;
        if(entry <= currencyCode.length){
            isValid = true;
        }

        if(typeIn != typeOut){
            isValid = true;
        }else{
            isValid = false;
        }

        return isValid;
    }

    private static double sendCurrencyConvertApi(String currCodeIn, String currCodeOut){
        //System.out.println("currCodeIn = " + currCodeIn);
        //System.out.println("currCodeOut = " + currCodeOut);
        double convert = 0.0;
        try {
            String api_key = "f4107fc3ba4b4e8ced63d444";

            String url = "https://v6.exchangerate-api.com/v6/" + api_key + "/latest/"+currCodeIn;

            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(url))
                    .setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200){
                String respStr = response.body();

                JsonElement jelement = JsonParser.parseString(respStr);
                JsonObject jobject = jelement.getAsJsonObject();
                if(!jobject.isJsonNull()){
                    if(jobject.get("result").getAsString().equals("success")){
                        //System.out.println("Success");
                        JsonObject objRates = jobject.get("conversion_rates").getAsJsonObject();
                        convert = objRates.get(currCodeOut).getAsDouble();
                    }
                }
            }
        }catch (InterruptedException | IOException ex){
            System.out.println("Error: " + ex.getMessage());
        }
        return convert;
    }

    private static Locale getLocale(String strCode) {
     
        for (Locale locale : NumberFormat.getAvailableLocales()) {
            String code = NumberFormat.getCurrencyInstance(locale).getCurrency().getCurrencyCode();
            if (strCode.equals(code)) {
                return locale;
            }
        }  
        return null;
    }
}
