package com.example.campusswap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/requests")
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class RequestController {

    @Autowired
    private RequestRepository requestRepository;

    @PostMapping
    public ResponseEntity<?> createRequest(@RequestBody Request request) {

        if (request.getItemId() == null)
            return ResponseEntity.badRequest().body("Item ID is required");

        if (request.getRequesterId() == null)
            return ResponseEntity.badRequest().body("Requester ID is required");

        if (request.getOwnerId() == null)
            return ResponseEntity.badRequest().body("Owner ID is required");

        request.setStatus("REQUESTED");
        request.setCreatedAt(LocalDateTime.now());

        Request savedRequest = requestRepository.save(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedRequest);
    }

    @GetMapping
    public ResponseEntity<List<Request>> getAllRequests() {
        return ResponseEntity.ok(requestRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRequestById(@PathVariable Long id) {

        Optional<Request> request = requestRepository.findById(id);

        if (request.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Request not found");

        return ResponseEntity.ok(request.get());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Request>> getRequestsByUser(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                requestRepository.findByRequesterId(userId)
        );
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Request>> getRequestsByOwner(
            @PathVariable Long ownerId) {

        return ResponseEntity.ok(
                requestRepository.findByOwnerId(ownerId)
        );
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<Request>> getRequestsByItem(
            @PathVariable Long itemId) {

        return ResponseEntity.ok(
                requestRepository.findByItemId(itemId)
        );
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<?> acceptRequest(@PathVariable Long id) {

        Optional<Request> requestOptional = requestRepository.findById(id);

        if (requestOptional.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Request not found");

        Request request = requestOptional.get();

        request.setStatus("ACCEPTED");

        Request savedRequest = requestRepository.save(request);

        return ResponseEntity.ok(savedRequest);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectRequest(@PathVariable Long id) {

        Optional<Request> requestOptional = requestRepository.findById(id);

        if (requestOptional.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Request not found");

        Request request = requestOptional.get();

        request.setStatus("REJECTED");

        Request savedRequest = requestRepository.save(request);

        return ResponseEntity.ok(savedRequest);
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completeRequest(@PathVariable Long id) {

        Optional<Request> requestOptional = requestRepository.findById(id);

        if (requestOptional.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Request not found");

        Request request = requestOptional.get();

        request.setStatus("COMPLETED");

        Request savedRequest = requestRepository.save(request);

        return ResponseEntity.ok(savedRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRequest(@PathVariable Long id) {

        if (!requestRepository.existsById(id))
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Request not found");

        requestRepository.deleteById(id);

        return ResponseEntity.ok("Request deleted successfully");
    }
}