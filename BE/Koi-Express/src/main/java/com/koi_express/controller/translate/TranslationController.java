package com.koi_express.controller.translate;

import com.koi_express.exception.TranslationException;
import com.koi_express.service.verification.TranslationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/translation")
@RequiredArgsConstructor
public class TranslationController {

    private final TranslationService translationService;

    @PostMapping
    public Map<String, String> translateContent(@RequestBody Map<String, String> content,
                                                @RequestParam String targetLanguage) {
        Map<String, String> translatedContent = new HashMap<>();

        content.forEach((key, text) -> {
            try {
                translatedContent.put(key, translationService.translateText(text, targetLanguage));
            } catch (TranslationException e) {
                throw new RuntimeException(e);
            }
        });

        return translatedContent;
    }
}
