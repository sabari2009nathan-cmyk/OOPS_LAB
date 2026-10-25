import java.util.*;

public class DocumentVerification {

    // Calculate similarity between two field values
    public static double compareFieldValues(String val1, String val2) {
        if (val1 == null || val2 == null ||
            val1.trim().isEmpty() || val2.trim().isEmpty()) {
            return 0.0;
        }

        String v1 = val1.trim().toLowerCase();
        String v2 = val2.trim().toLowerCase();

        if (v1.equals(v2)) {
            return 100.0;
        }

        return Math.round(similarity(v1, v2) * 1000.0) / 10.0;
    }

    // Calculate string similarity using dynamic programming
    public static double similarity(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j],
                                        dp[i][j - 1]);
                }
            }
        }

        int lcs = dp[s1.length()][s2.length()];
        return (2.0 * lcs) / (s1.length() + s2.length());
    }

    // Compare fields from two documents
    public static Map<String, Object> crossVerifyDocuments(
            Map<String, String> doc1,
            Map<String, String> doc2) {

        List<Map<String, Object>> comparisons = new ArrayList<>();
        List<Double> scores = new ArrayList<>();

        Set<String> allKeys = new TreeSet<>();
        allKeys.addAll(doc1.keySet());
        allKeys.addAll(doc2.keySet());

        for (String key : allKeys) {
            String v1 = doc1.get(key);
            String v2 = doc2.get(key);

            double score;
            String status;

            if (v1 == null || v2 == null) {
                score = 0.0;
                status = "MISSING";
            } else {
                score = compareFieldValues(v1, v2);

                if (score >= 85.0) {
                    status = "MATCH";
                } else if (score >= 50.0) {
                    status = "PARTIAL";
                } else {
                    status = "MISMATCH";
                }

                scores.add(score);
            }

            Map<String, Object> field = new LinkedHashMap<>();
            field.put("field_name", key);
            field.put("doc1_value", v1 == null ? "N/A" : v1);
            field.put("doc2_value", v2 == null ? "N/A" : v2);
            field.put("match_score", score);
            field.put("status", status);

            comparisons.add(field);
        }

        double overallScore = scores.isEmpty()
                ? 50.0
                : Math.round(scores.stream()
                    .mapToDouble(Double::doubleValue)
                    .average().orElse(50.0) * 10.0) / 10.0;

        String overallStatus;

        if (overallScore >= 85.0) {
            overallStatus = "MATCHED";
        } else if (overallScore >= 50.0) {
            overallStatus = "PARTIAL_MISMATCH";
        } else {
            overallStatus = "HIGH_DISCREPANCY";
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("match_score", overallScore);
        result.put("status", overallStatus);
        result.put("field_comparisons", comparisons);

        return result;
    }

    // Main method
    public static void main(String[] args) {

        Map<String, String> doc1 = new TreeMap<>();
        doc1.put("name", "ROSHAN");
        doc1.put("id", "12345");
        doc1.put("address", "Chennai");

        Map<String, String> doc2 = new TreeMap<>();
        doc2.put("name", "Roshan");
        doc2.put("id", "12345");
        doc2.put("address", "Coimbatore");

        Map<String, Object> result =
                crossVerifyDocuments(doc1, doc2);

        System.out.println("Overall Score: "
                + result.get("match_score") + "%");
        System.out.println("Overall Status: "
                + result.get("status"));
        System.out.println("\nField Comparisons:");

        List<Map<String, Object>> fields =
                (List<Map<String, Object>>)
                result.get("field_comparisons");

        for (Map<String, Object> field : fields) {
            System.out.println("\nField: "
                    + field.get("field_name"));
            System.out.println("Document 1: "
                    + field.get("doc1_value"));
            System.out.println("Document 2: "
                    + field.get("doc2_value"));
            System.out.println("Score: "
                    + field.get("match_score") + "%");
            System.out.println("Status: "
                    + field.get("status"));
        }
    }
}

public class Customer {
    private Long customerId;
    private String name;
    private String phone;
    private String email;
    private String licenseNumber;

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        this.name = name;
    }

    public void setPhone(String phone) {
        if (phone == null || phone.replaceAll("\\D", "").length() < 10) {
            throw new IllegalArgumentException("Valid phone is required");
        }
        
