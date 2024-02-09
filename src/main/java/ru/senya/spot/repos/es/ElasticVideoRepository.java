package ru.senya.spot.repos.es;

import com.google.gson.Gson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import ru.senya.spot.models.es.EsVideoModel;

@Repository
public interface ElasticVideoRepository extends ElasticsearchRepository<EsVideoModel, String> {

    Gson gson = new Gson();

    @Query("{\n" +
            "    \"multi_match\": {\n" +
            "        \"query\": \"?0\",\n" +
            "        \"fields\": [\n" +
            "            \"name\",\n" +
            "            \"ru_tags\",\n" +
            "            \"en_tags\",\n" +
            "            \"description\"\n" +
            "        ],\n" +
            "        \"fuzziness\": \"AUTO\",\n" +
            "        \"fuzzy_transpositions\": true \n" +
            "    }\n" +
            "}")
    Page<EsVideoModel> find(String text, Pageable pageable);

}
