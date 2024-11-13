package com.poly.DAO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.poly.model.ContentMap;

@Repository
public class ContenDAO {
	
	RestTemplate rest = new RestTemplate();

	String FeaturedNewsUrl = "https://test-ef186-default-rtdb.firebaseio.com/noibat.json";

	String TravelTipsUrl = "https://poly-java-6-c9b90-default-rtdb.firebaseio.com/tips.json";
	
	public ContentMap findAllFeaturedNews() {

		return rest.getForObject(FeaturedNewsUrl, ContentMap.class);
	}

	public ContentMap findAllTravelTips() {

		return rest.getForObject(TravelTipsUrl, ContentMap.class);
	}
}
