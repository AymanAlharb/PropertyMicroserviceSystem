package com.ayman.searchpropertyservice.controller;

import com.ayman.searchpropertyservice.constant.ApiRoutes;
import com.ayman.searchpropertyservice.model.document.PropertyDocument;
import com.ayman.searchpropertyservice.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(ApiRoutes.SEARCH_PROPERTY)
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/get-all")
    public Iterable<PropertyDocument> getAll(){
        return searchService.getAll();
    }
    @GetMapping("/by-price")
    public List<PropertyDocument> searchByPrice(
            @RequestParam("min") double min,
            @RequestParam("max") double max) {
        return searchService.searchByPrice(min, max);
    }
}


