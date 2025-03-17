package com.nainesh.accounts.service.client;

import com.nainesh.accounts.dto.CardsDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CardsFallback implements CardsFeignClient{

    @Override
    public ResponseEntity<CardsDto> fetchCardsDetails(String correlationId, String mobileNumber) {
        return null;
    }
}
