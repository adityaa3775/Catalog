import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ShamirSecretSharing {

    public static void main(String[] args) {
        String filename = "testcase.json";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File(filename));

            int n = rootNode.get("keys").get("n").asInt();
            int k = rootNode.get("keys").get("k").asInt();

            List<BigInteger> xValues = new ArrayList<>();
            List<BigInteger> yValues = new ArrayList<>();
            for (int i = 1; i <= n; i++) {
                JsonNode node = rootNode.get(String.valueOf(i));
                int base = node.get("base").asInt();
                String value = node.get("value").asText();
                BigInteger decodedValue = decodeValue(value, base);
                xValues.add(BigInteger.valueOf(i));
                yValues.add(decodedValue);
            }
            BigInteger c = lagrangeInterpolation(xValues, yValues, BigInteger.ZERO);
            System.out.println("Secret (c): " + c);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static BigInteger decodeValue(String value, int base) {
        BigInteger decodedValue = BigInteger.ZERO;
        BigInteger baseValue = BigInteger.valueOf(base);
        for (int i = 0; i < value.length(); i++) {
            char digitChar = value.charAt(value.length() - 1 - i);
            int digit = Character.digit(digitChar, base);
            decodedValue = decodedValue.add(BigInteger.valueOf(digit).multiply(baseValue.pow(i)));
        }
        return decodedValue;
    }

    private static BigInteger lagrangeInterpolation(List<BigInteger> x, List<BigInteger> y, BigInteger x0) {
        BigInteger total = BigInteger.ZERO;
        int n = x.size();
        
        for (int i = 0; i < n; i++) {
            BigInteger xi = x.get(i);
            BigInteger yi = y.get(i);
            BigInteger li = BigInteger.ONE;

            for (int j = 0; j < n; j++) {
                if (i != j) {
                    BigInteger xj = x.get(j);
                    li = li.multiply(x0.subtract(xj)).divide(xi.subtract(xj));
                }
            }
            total = total.add(yi.multiply(li));
        }
        return total;
    }
}
