# Lab 10 — Security Incident Response: Code Examples

## Secrets Scanner (Pre-commit Hook)

```java
import java.io.*;
import java.nio.file.*;
import java.util.regex.*;

public class SecretsScanner {
    private static final Pattern[] SECRET_PATTERNS = {
        Pattern.compile("AKIA[0-9A-Z]{16}"), // AWS Access Key
        Pattern.compile("-----BEGIN RSA PRIVATE KEY-----"),
        Pattern.compile("-----BEGIN OPENSSH PRIVATE KEY-----"),
        Pattern.compile("ghp_[0-9a-zA-Z]{36}"), // GitHub PAT
        Pattern.compile("sk_live_[0-9a-zA-Z]{24}"), // Stripe live key
        Pattern.compile("xox[baprs]-[0-9a-zA-Z-]+"), // Slack token
        Pattern.compile("(?i)password\\s*[:=]\\s*['\"][^'\"]+['\"]"),
        Pattern.compile("(?i)secret\\s*[:=]\\s*['\"][^'\"]+['\"]"),
    };

    public static ScanResult scanFile(Path file) throws IOException {
        String content = Files.readString(file);
        int lineNum = 1;
        for (String line : content.split("\n")) {
            for (Pattern pattern : SECRET_PATTERNS) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    return new ScanResult(file.toString(), lineNum,
                            matcher.group().substring(0, 8) + "...[REDACTED]");
                }
            }
            lineNum++;
        }
        return null;
    }

    public record ScanResult(String file, int line, String match) {}

    public static void main(String[] args) throws Exception {
        boolean found = false;
        for (String arg : args) {
            Path file = Path.of(arg);
            ScanResult result = scanFile(file);
            if (result != null) {
                System.err.println("SECRET FOUND: " + result.file() + ":" + result.line() + " — " + result.match());
                found = true;
            }
        }
        if (found) {
            System.exit(1); // Block the commit
        }
    }
}
```

## Security Headers Filter

```java
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

public class SecurityHeadersFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-XSS-Protection", "1; mode=block");
        response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        response.setHeader("Content-Security-Policy", "default-src 'self'");
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        response.setHeader("Cache-Control", "no-store, max-age=0");
        chain.doFilter(req, res);
    }
}
```
