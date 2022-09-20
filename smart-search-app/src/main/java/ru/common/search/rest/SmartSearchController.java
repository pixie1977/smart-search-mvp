package ru.common.search.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.common.config.Constants;
import ru.common.search.dto.SearchRequest;
import ru.common.search.dto.SearchResponse;
import ru.common.search.services.SmartSearchService;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/smart-search")
public class SmartSearchController {

	private static final Logger LOG = LoggerFactory.getLogger(SmartSearchController.class);

	private final SmartSearchService smartSearchService;

	@Autowired
	public SmartSearchController(SmartSearchService smartSearchService){
		this.smartSearchService = smartSearchService;
	}

	@GetMapping("/healthcheck")
	@ResponseBody
	public Object healthcheck() {
		return "OK";
	}

	@GetMapping("/timestamp")
	@ResponseBody
	public ResponseEntity<Long> timestamp() {
		return ResponseEntity.ok(System.currentTimeMillis());
	}

	@GetMapping("/count")
	@ResponseBody
	public ResponseEntity<Long> count() {
		return ResponseEntity.ok(smartSearchService.getCount());
	}

	@CrossOrigin("localhost:3000")
	@PostMapping("/search-cors")
	@ResponseBody
	public ResponseEntity<SearchResponse> searchCors(final @RequestBody SearchRequest searchRequest) {
		return search(searchRequest);
	}

	@PostMapping("/search")
	@ResponseBody
	public ResponseEntity<SearchResponse> search(final @RequestBody SearchRequest searchRequest) {
		final SearchResponse searchResponse = new SearchResponse();
		try{
			if(StringUtils.isEmpty(searchRequest.getQuery())){
				throw new IllegalArgumentException("query cannot be empty");
			}

			if (searchRequest.getCount() == 0){
				searchRequest.setCount(Constants.DEF_PAGE_SIZE);
			}

			List<Map<String, String>> docList;

			docList = smartSearchService.findByFuzzyValue(
					searchRequest.getQuery(),
					searchRequest.getFrom(),
					searchRequest.getCount()
			);

			searchResponse.setFrom(String.valueOf(searchRequest.getFrom()));
			searchResponse.setTotalCount(String.valueOf(docList.size()));
			searchResponse.setItems(docList);
			return ResponseEntity.ok(searchResponse);
		} catch (Exception e) {
			LOG.error("Error searching indexed doc",e);
			searchResponse.setError(e.getMessage());
			return new ResponseEntity<>(searchResponse, HttpStatus.SEE_OTHER);
		}
	}


}