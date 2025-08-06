package com.ayman.searchpropertyservice.model.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "propertyindex")
public class PropertyDocument {
    @Id
    private Long id;
    @Field(type = FieldType.Text, name = "title")

    private String title;
    @Field(type = FieldType.Text, name = "title")
    private String description;

    @Field(type = FieldType.Double, name = "price")
    private double price;
}
