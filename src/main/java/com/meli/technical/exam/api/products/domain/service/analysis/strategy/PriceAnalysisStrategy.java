package com.meli.technical.exam.api.products.domain.service.analysis.strategy;

import com.meli.technical.exam.api.products.application.dto.response.comparison.PriceAnalysisDto;
import com.meli.technical.exam.api.products.domain.service.analysis.ProductStats;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class PriceAnalysisStrategy implements ProductAnalysisStrategy<PriceAnalysisDto> {
    
    @Override
    public Mono<PriceAnalysisDto> analyze(ProductStats productStats) {
        if (productStats.isEmpty()) {
            return Mono.just(createEmptyPriceAnalysis());
        }
        
        return Mono.fromSupplier(() -> PriceAnalysisDto.builder()
                .cheapestProduct(productStats.getCheapestProduct())
                .mostExpensiveProduct(productStats.getMostExpensiveProduct())
                .priceRange(productStats.getPriceRange())
                .averagePrice(productStats.getAveragePrice())
                .priceDistribution(productStats.getPriceDistribution())
                .build());
    }
    
    @Override
    public String getAnalysisType() {
        return "PRICE_ANALYSIS";
    }
    
    private PriceAnalysisDto createEmptyPriceAnalysis() {
        return PriceAnalysisDto.builder()
                .cheapestProduct(null)
                .mostExpensiveProduct(null)
                .priceRange(null)
                .averagePrice(null)
                .priceDistribution(null)
                .build();
    }
}