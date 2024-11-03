package com.koi_express.controller.translate;

import java.util.HashMap;
import java.util.Map;

import com.koi_express.dto.response.ApiResponse;
import com.koi_express.exception.TranslationException;
import com.koi_express.service.verification.TranslationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/translation")
@RequiredArgsConstructor
public class TranslationController {

    private static final Logger logger = LoggerFactory.getLogger(TranslationController.class);

    private final TranslationService translationService;

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, String>>> translateContent(
            @RequestBody Map<String, String> content, @RequestParam String targetLanguage) {

        Map<String, String> translatedContent = new HashMap<>();

        content.forEach((key, text) -> {
            try {
                translatedContent.put(key, translationService.translateText(text, targetLanguage));
            } catch (TranslationException e) {
                logger.error("Translation error for key '{}': {}", key, e.getMessage());
                translatedContent.put(key, "Error translating text"); // Optional: add an error message for specific keys
            }
        });

        if (translatedContent.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Translation failed", null));
        }

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Translation successful", translatedContent));

    }
}
