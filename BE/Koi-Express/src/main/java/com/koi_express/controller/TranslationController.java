package com.koi_express.controller;

import com.koi_express.service.verification.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/translation")
public class TranslationController {

    @Autowired
    private TranslationService translationService;

    @PostMapping
    public Map<String, String> translateContent(@RequestBody Map<String, String> content,
                                                @RequestParam String targetLanguage) {
        Map<String, String> translatedContent = new HashMap<>();

        content.forEach((key, text) -> {
            translatedContent.put(key, translationService.translateText(text, targetLanguage));
        });

        return translatedContent;
    }
}
