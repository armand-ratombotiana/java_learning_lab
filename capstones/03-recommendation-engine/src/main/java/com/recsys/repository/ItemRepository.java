package com.recsys.repository;

import com.recsys.model.Item;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends MongoRepository<Item, String> {
    List<Item> findByCategory(String category);
    List<Item> findByTagsContaining(String tag);
}