package ir.invoice.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConverterConfig {
    @Value("${converter.json.model}")
    public String model;
    @Value("${converter.csv.headers}")
    public String headers;


}
