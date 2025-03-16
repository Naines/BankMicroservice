package com.nainesh.accounts.service.client;

import com.nainesh.accounts.dto.CardsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient("cards")
public interface CardsFeignClient {

    //this method signature should match with cards rest api (fetch cards details)
    //url should also match like /api/fetch
    @GetMapping("/api/fetch")
    public ResponseEntity<CardsDto> fetchCardsDetails(@RequestParam String mobileNumber);
}