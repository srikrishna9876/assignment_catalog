import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class SecretSharing {
    
    // Function to decode a value from a given base to decimal
    public static BigInteger decodeValue(int base, String value) {
        return new BigInteger(value, base);
    }

    // Function to perform Lagrange interpolation and find the constant term
    public static BigInteger interpolate(BigInteger[][] points, int count) {
        BigInteger constantTerm = BigInteger.ZERO;

        for (int i = 0; i < count; i++) {
            BigInteger x = points[i][0];
            BigInteger y = points[i][1];

            BigInteger li = BigInteger.ONE;
            for (int j = 0; j < count; j++) {
                if (i != j) {
                    BigInteger xj = points[j][0];
                    li = li.multiply(BigInteger.ZERO.subtract(xj)).divide(x.subtract(xj));
                }
            }
            constantTerm = constantTerm.add(y.multiply(li));
        }
        return constantTerm;
    }

    // Main function to read JSON input and calculate the constant term
    public void calculateConstantTerm(String filePath) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonData = (JSONObject) parser.parse(new FileReader(filePath));
            System.out.println("Parsed JSON: " + jsonData);  // Debug line

            // Check if "keys" exists
            JSONObject keys = (JSONObject) jsonData.get("keys");
            if (keys == null) {
                System.out.println("Keys not found in JSON.");
                return;
            }

            // Use safe casting and parsing
            long totalPoints = Long.parseLong(keys.get("n").toString());
            long requiredPoints = Long.parseLong(keys.get("k").toString());

            // Decode the points
            List<BigInteger[]> decodedPoints = new ArrayList<>();
            for (long i = 1; i <= totalPoints; i++) {
                JSONObject point = (JSONObject) jsonData.get(String.valueOf(i));
                if (point != null) {
                    BigInteger x = BigInteger.valueOf(i);
                    long base = Long.parseLong(point.get("base").toString());
                    String value = (String) point.get("value");
                    BigInteger y = decodeValue((int) base, value);
                    decodedPoints.add(new BigInteger[]{x, y});
                } else {
                    System.out.println("Point " + i + " not found.");
                }
            }

            // Convert List to BigInteger[][]
            BigInteger[][] selectedPoints = new BigInteger[(int) requiredPoints][2];
            for (int i = 0; i < requiredPoints; i++) {
                selectedPoints[i] = decodedPoints.get(i);
            }

            BigInteger constant = interpolate(selectedPoints, (int) requiredPoints);
            System.out.println("Constant term (c): " + constant);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Example usage
        SecretSharing secretSharing = new SecretSharing();
        secretSharing.calculateConstantTerm("input.json");
    }
}
