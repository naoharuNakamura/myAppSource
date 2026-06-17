package com.zisakuapp.zisaku_backend.controller;

import com.zisakuapp.zisaku_backend.model.Restaurant;
import com.zisakuapp.zisaku_backend.service.RestaurantService;
import com.zisakuapp.zisaku_backend.dto.RestaurantSearchRequest;
import com.zisakuapp.zisaku_backend.dto.RestaurantResponse;
import com.github.pagehelper.PageInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/m_restaurant")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @GetMapping("/genres")
    public List<String> getGenres() {
        return restaurantService.getGenres();
    }

    @GetMapping("/price-ranges")
    public List<String> getPriceRanges() {
        return restaurantService.getPriceRanges();
    }

    @GetMapping("/areas")
    public List<String> getAreas() {
        return restaurantService.getAreas();
    }

    @GetMapping("/ratings")
    public List<Double> getRatings() {
        return restaurantService.getRatings();
    }

    @GetMapping("/result")
    public List<Restaurant> getAllRestaurants() {
        // 💡 ここにデバッグ追加
        List<Restaurant> list = restaurantService.getAllRestaurants();
        System.out.println(
                "====== [Debug] Controller: size of result = " + (list != null ? list.size() : "null") + " ======");

        return list;
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> getRestaurantDetail(@PathVariable(name = "id") int id) {
        System.out.println("Detail Request ID: " + id);
        return ResponseEntity.ok(restaurantService.getRestaurantDetail(id));
    }

    @GetMapping("/search")
    public ResponseEntity<PageInfo<RestaurantResponse>> searchRestaurants(
            @ModelAttribute RestaurantSearchRequest request,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "50") int size) {

        System.out.println("Search Query: " + request);
        System.out.println("Addapted Page Sorting: " + "page=" + page + ", size=" + size);

        PageInfo<RestaurantResponse> results = restaurantService.searchRestaurants(request, page, size);
        return ResponseEntity.ok(results);
    }
}