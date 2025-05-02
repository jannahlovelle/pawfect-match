
package cit.edu.pawfect.match.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

@Configuration
public class MongoConfig {

    @Autowired
    public void configureMongoTemplate(MongoTemplate mongoTemplate) {
        MappingMongoConverter converter = (MappingMongoConverter) mongoTemplate.getConverter();
        converter.setMapKeyDotReplacement("_"); 
    }
}
