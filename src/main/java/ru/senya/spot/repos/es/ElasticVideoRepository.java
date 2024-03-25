package ru.senya.spot.repos.es;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import ru.senya.spot.models.es.EsVideoModel;

import java.util.List;

@Repository
public interface ElasticVideoRepository extends ElasticsearchRepository<EsVideoModel, String> {

    @Query("{\"bool\": {\"should\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"name\", \"ru_tags\", \"en_tags\", \"description\"], \"fuzziness\": \"AUTO\", \"fuzzy_transpositions\": true }}, {\"regexp\": {\"name\": \".*?0.*\"}}, {\"regexp\": {\"ru_tags\": \".*?0.*\"}}, {\"regexp\": {\"en_tags\": \".*?0.*\"}}, {\"regexp\": {\"description\": \".*?0.*\"}}]}}")
    List<EsVideoModel> find(String text, Pageable pageable);
}
