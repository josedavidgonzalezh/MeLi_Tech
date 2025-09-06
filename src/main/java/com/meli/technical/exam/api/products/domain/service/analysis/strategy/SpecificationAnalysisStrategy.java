package com.meli.technical.exam.api.products.domain.service.analysis.strategy;

import com.meli.technical.exam.api.products.application.dto.response.comparison.SpecificationAnalysisDto;
import com.meli.technical.exam.api.products.domain.service.analysis.ProductStats;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SpecificationAnalysisStrategy implements ProductAnalysisStrategy<SpecificationAnalysisDto> {
    
    @Override
    public Mono<SpecificationAnalysisDto> analyze(ProductStats productStats) {
        if (productStats.isEmpty()) {
            return Mono.just(createEmptySpecificationAnalysis());
        }
        
        return Mono.fromSupplier(() -> {
            Map<String, Set<String>> uniqueSpecs = calculateUniqueSpecifications(productStats);
            Map<String, Map<String, String>> specComparison = createSpecificationComparison(productStats);
            
            return SpecificationAnalysisDto.builder()
                    .commonSpecifications(productStats.getCommonSpecifications())
                    .uniqueSpecifications(uniqueSpecs)
                    .specificationComparison(specComparison)
                    .mostFeaturedProduct(productStats.getMostFeaturedProduct())
                    .build();
        });
    }
    
    @Override
    public String getAnalysisType() {
        return "SPECIFICATION_ANALYSIS";
    }
    
    private Map<String, Set<String>> calculateUniqueSpecifications(ProductStats productStats) {
        return productStats.getProductSpecifications().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .filter(specKey -> !productStats.getCommonSpecifications().contains(specKey))
                                .collect(Collectors.toSet())
                ));
    }
    
    private Map<String, Map<String, String>> createSpecificationComparison(ProductStats productStats) {
        Map<String, Map<String, String>> specComparison = new HashMap<>();
        
        for (String specKey : productStats.getCommonSpecifications()) {
            Map<String, String> productValues = productStats.getAllProducts().stream()
                    .collect(Collectors.toMap(
                            product -> product.getName(),
                            product -> product.getSpecifications().stream()
                                    .filter(spec -> spec.getKey().equals(specKey))
                                    .map(spec -> spec.getValue())
                                    .findFirst()
                                    .orElse("N/A")
                    ));
            specComparison.put(specKey, productValues);
        }
        
        return specComparison;
    }
    
    private SpecificationAnalysisDto createEmptySpecificationAnalysis() {
        return SpecificationAnalysisDto.builder()
                .commonSpecifications(Collections.emptySet())
                .uniqueSpecifications(Collections.emptyMap())
                .specificationComparison(Collections.emptyMap())
                .mostFeaturedProduct(null)
                .build();
    }
}