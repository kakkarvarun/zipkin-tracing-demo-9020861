package mypack;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class CurrencyConversionService {

    private final WebClient.Builder webClientBuilder;

    public CurrencyConversionService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public Mono<Map<String, Object>> convert(String from, String to, double amount) {
        return webClientBuilder.build()
                .get()
                .uri("http://exchange-service/exchange/rate/from/{from}/to/{to}", from, to)
                .retrieve()
                .bodyToMono(Map.class)
                .map(rateInfo -> {
                    double rate = ((Number) rateInfo.get("rate")).doubleValue();
                    double converted = amount * rate;
                    rateInfo.put("amount", amount);
                    rateInfo.put("convertedAmount", converted);
                    return rateInfo;
                });
    }
}
