package ru.senya.spot.models.es;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import ru.senya.spot.models.jpa.TagModel;

import java.util.Set;


@Data
@Document(indexName = "video")
public class EsVideoModel {

    @Id
    private String id;

    private String vid_uuid;

    @Field(type = FieldType.Text, analyzer = "russian")
    private String name;

    @Field(type = FieldType.Text, analyzer = "russian")
    private String description;

    @Field(type = FieldType.Text, analyzer = "russian")
    private String ru_tags;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String en_tags;

    public void setTags(Set<TagModel> tags) {
        StringBuilder ru_tags = new StringBuilder();
        StringBuilder en_tags = new StringBuilder();
        for (TagModel tagModel : tags) {
            ru_tags.append(tagModel.getRuTag()).append(" ");
            en_tags.append(tagModel.getEnTag()).append(" ");
        }
        this.ru_tags = ru_tags.toString().strip();
        this.en_tags = en_tags.toString().strip();
    }
}
