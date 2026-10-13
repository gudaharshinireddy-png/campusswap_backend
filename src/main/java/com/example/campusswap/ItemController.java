package com.example.campusswap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class ItemController {

    @Autowired
    private ItemRepository itemRepository;

    @PostMapping
    public ResponseEntity<?> addItem(@RequestBody Item item) {

        if (item.getTitle() == null || item.getTitle().trim().isEmpty())
            return ResponseEntity.badRequest().body("Title is required");

        if (item.getDescription() == null || item.getDescription().trim().isEmpty())
            return ResponseEntity.badRequest().body("Description is required");

        if (item.getCategory() == null || item.getCategory().trim().isEmpty())
            return ResponseEntity.badRequest().body("Category is required");

        if (item.getItemType() == null || item.getItemType().trim().isEmpty())
            return ResponseEntity.badRequest().body("Item type is required");

        if (!item.getItemType().equalsIgnoreCase("DONATION"))
            return ResponseEntity.badRequest().body("Only donation items are allowed");

        if (item.getOwnerId() == null)
            return ResponseEntity.badRequest().body("Owner ID is required");

        if (item.getStatus() == null || item.getStatus().trim().isEmpty())
            item.setStatus("AVAILABLE");

        item.setItemType("DONATION");
        item.setCreatedAt(LocalDateTime.now());

        Item savedItem = itemRepository.save(item);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
    }

    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        return ResponseEntity.ok(itemRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getItemById(@PathVariable Long id) {

        Optional<Item> item = itemRepository.findById(id);

        if (item.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found");

        return ResponseEntity.ok(item.get());
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Item>> getItemsByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(itemRepository.findByOwnerId(ownerId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(
            @PathVariable Long id,
            @RequestBody Item updatedItem) {

        Optional<Item> existingItemOptional = itemRepository.findById(id);

        if (existingItemOptional.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found");

        Item existingItem = existingItemOptional.get();

        if (updatedItem.getTitle() != null &&
                !updatedItem.getTitle().trim().isEmpty())
            existingItem.setTitle(updatedItem.getTitle());

        if (updatedItem.getDescription() != null)
            existingItem.setDescription(updatedItem.getDescription());

        if (updatedItem.getCategory() != null)
            existingItem.setCategory(updatedItem.getCategory());

        // Item type is always DONATION
        existingItem.setItemType("DONATION");

        if (updatedItem.getCondition() != null)
            existingItem.setCondition(updatedItem.getCondition());

        if (updatedItem.getLocation() != null)
            existingItem.setLocation(updatedItem.getLocation());

        if (updatedItem.getStatus() != null)
            existingItem.setStatus(updatedItem.getStatus());

        if (updatedItem.getImageUrl() != null)
            existingItem.setImageUrl(updatedItem.getImageUrl());

        Item savedItem = itemRepository.save(existingItem);

        return ResponseEntity.ok(savedItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id) {

        if (!itemRepository.existsById(id))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found");

        itemRepository.deleteById(id);

        return ResponseEntity.ok("Item deleted successfully");
    }
}