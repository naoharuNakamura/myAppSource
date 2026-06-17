package com.zisakuapp.zisaku_backend.service;

import com.zisakuapp.zisaku_backend.model.Restaurant;
import com.zisakuapp.zisaku_backend.dto.RestaurantSearchRequest;
import com.zisakuapp.zisaku_backend.mapper.RestaurantMapper; // 💡 名前をMapperに変更
import com.zisakuapp.zisaku_backend.dto.RestaurantResponse;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantMapper restaurantMapper; // 💡 MyBatisのMapperをインジェクション

    @Autowired
    private DataSource dataSource;

    public List<String> getGenres() {
        return restaurantMapper.findDistinctGenres();
    }

    public List<String> getPriceRanges() {
        return restaurantMapper.findDistinctPriceRanges();
    }

    public List<String> getAreas() {
        return restaurantMapper.findDistinctAreas();
    }

    public List<Double> getRatings() {
        return restaurantMapper.findDistinctRatings();
    }

    public List<Restaurant> getAllRestaurants() {
        // 💡 ここにデバッグ追加
        List<Restaurant> list = restaurantMapper.findAll();
        System.out.println(
                "====== [Debug] Service: from Mapper = " + (list != null ? list.size() : "null") + " ======");

        return list;
    }

    public RestaurantResponse getRestaurantDetail(int restaurantId) {
        // Optionalではなく直で返すか、MyBatis側で返却型を調整
        Restaurant restaurant = restaurantMapper.findByRestaurantId(restaurantId);
        if (restaurant == null) {
            throw new RuntimeException("Restaurant not found with id: " + restaurantId);
        }
        return new RestaurantResponse(restaurant);
    }

    public PageInfo<RestaurantResponse> searchRestaurants(RestaurantSearchRequest request, int page, int size) {

        // =========================
        // 0. DB確認（デバッグ用）
        // =========================
        try (java.sql.Connection conn = dataSource.getConnection();
                java.sql.Statement stmt = conn.createStatement();
                java.sql.ResultSet rs = stmt.executeQuery("SELECT count(*) FROM m_restaurant")) {

            if (rs.next()) {
                System.out.println("====== [Debug] !! all records: " + rs.getInt(1) + " !! ======");
                System.out.println("====== [Debug] File Path: " + conn.getMetaData().getURL() + " ======");
            }
        } catch (Exception e) {
            System.out.println("====== [Debug] DB確認エラー: " + e.getMessage() + " ======");
        }

        // =========================
        // 1. リクエスト整形
        // =========================
        System.out.println("====== [Debug Start] Search Request: " + request + " ======");
        parseRequestParams(request);
        System.out.println("====== [Debug] Parsed Request: " + request + " ======");

        // =========================
        // 2. page補正（超重要）
        // =========================
        if (page <= 0) {
            page = 1;
        }

        System.out.println("[Debug] page=" + page + ", size=" + size);

        // =========================
        // 3. PageHelper開始
        // =========================
        String orderBy = null;

        if (request.getSort() != null && !request.getSort().isEmpty()) {
            // フロントエンドの SEARCH_CONFIG の value と完全に一致させる
            switch (request.getSort()) {
                case "restaurantRating,desc":
                    orderBy = "restaurant_rating DESC";
                    break;
                case "restaurantRating,asc":
                    orderBy = "restaurant_rating ASC";
                    break;
                case "restaurantId,asc":
                    orderBy = "restaurant_id ASC";
                    break;
                default:
                    // 想定外の文字列が来た場合はデフォルトソート
                    orderBy = "restaurant_id ASC";
                    break;
            }
        } else {
            // ソート指定が空(nullまたは"")の場合のデフォルト
            orderBy = "restaurant_id ASC";
        }

        // 第3引数に orderBy を渡すことで、MyBatisが自動的に ORDER BY句 を生成します
        PageHelper.startPage(page, size, orderBy);
        // =========================
        // 4. DB検索
        // =========================
        List<Restaurant> restaurants = restaurantMapper.searchRestaurants(request);
        if (restaurants == null) {
            restaurants = new ArrayList<>();
        }
        // 💡 【追加】PageHelperが取得した本来の総件数やページ情報をPageInfoで取り出す
        PageInfo<Restaurant> originalPageInfo = new PageInfo<>(restaurants);
        long totalRecords = originalPageInfo.getTotal();
        int totalPagesCount = originalPageInfo.getPages();

        System.out.println("====== [Debug] mapper result size: " + restaurants.size() + " ======");
        System.out.println("====== [Debug] real total records: " + totalRecords + " ======");
        // =========================
        // 6. DTO変換（詳細デバッグ用）
        // =========================
        List<RestaurantResponse> dtoList = new ArrayList<>();
        int errorCount = 0;

        for (int i = 0; i < restaurants.size(); i++) {
            Restaurant r = restaurants.get(i);

            // 1. オブジェクトそのもののチェック
            if (r == null) {
                System.out.println("====== [Debug] Index " + i + ": Object is NULL");
                continue;
            }

            // 2. IDのチェック (NullPointerExceptionの元凶を探る)
            System.out.println("====== [Debug] Index " + i + ": ID=" + r.getRestaurantId() + ", Name="
                    + (r.getRestaurantName() != null ? r.getRestaurantName() : "null"));

            try {
                RestaurantResponse response = new RestaurantResponse(r);
                dtoList.add(response);
            } catch (Exception e) {
                errorCount++;
                System.err.println("====== [Debug] Index " + i + " Failed: " + e.getMessage());
            }
        }

        System.out.println(
                "====== [Debug] Mapping complete. Success: " + dtoList.size() + ", Errors: " + errorCount + " ======");

        // =========================
        // 7. PageInfoの作成と手動修正
        // =========================
        PageInfo<RestaurantResponse> result = new PageInfo<>();
        result.setList(dtoList);
        // 💡 【修正】restaurants.size() ではなく、PageHelperが裏で計算した本当の総件数をセット
        result.setTotal(totalRecords);

        result.setPageNum(page);
        result.setPageSize(size);
        // 💡 【修正】全ページ数もPageHelperの計算結果をそのまま利用する
        result.setPages(totalPagesCount);
        result.setSize(dtoList.size());

        System.out.println("====== [Debug] final result size: " + dtoList.size() + " ======");

        return result;
    }

    /**
     * JPAの時にCriteria内部でやっていた文字列処理や数値変換を、
     * MyBatisのSQLに渡しやすくするためにDTOを前処理するメソッド
     */
    private void parseRequestParams(RestaurantSearchRequest request) {
        // レーティングのパース (例: "3.5~4.5" -> min=3.5, max=4.5)
        if (request.getRestaurantRating() != null && request.getRestaurantRating().contains("~")) {
            try {
                String[] range = request.getRestaurantRating().split("~");
                request.setMinRating(Double.parseDouble(range[0]));
                request.setMaxRating(Double.parseDouble(range[1]));
            } catch (NumberFormatException e) {
                System.err.println("レーティングの数値変換エラー: " + request.getRestaurantRating());
            }
        }
    }
}