package uz.fb.comparer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uz.fb.comparer.model.RequestDTO;
import uz.fb.comparer.service.ComparingService;

@RestController
public class CheckController {
    private final ComparingService comparingService;

    public CheckController(ComparingService comparingService) {
        this.comparingService = comparingService;
    }

    @PostMapping("/request")
    public ResponseEntity<Void> checkingFilterRequest(@RequestBody RequestDTO requestDTO){
        comparingService.checkFilterRequest(requestDTO);
        return ResponseEntity.noContent().build();
    }
}
