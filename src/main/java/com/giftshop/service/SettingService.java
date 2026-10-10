package com.giftshop.service;

import com.giftshop.model.Setting;
import com.giftshop.repository.SettingRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class SettingService {

    private final SettingRepository settingRepository;

    public SettingService(SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    // Default values used the first time the app runs and as a fallback.
    public static Map<String, String> defaults() {
        Map<String, String> d = new LinkedHashMap<>();
        d.put("shopName", "Wanderlust Souvenirs");
        d.put("logoEmoji", "🧳");
        d.put("tagline", "Bring the world home");
        d.put("heroTitle", "Authentic souvenirs from around the world");
        d.put("heroSubtitle", "Handpicked treasures and traditional crafts — from the bazaars of Iran to markets across the globe. Beautifully packed and shipped to your door.");
        d.put("aboutText", "We travel the world so you can bring a piece of it home. Every item in our shop is a genuine, handpicked souvenir — traditional crafts, local flavors, and keepsakes that carry the story of the place they came from. From Persian saffron and handwoven rugs to treasures from Turkey, Italy, Japan and beyond, each piece is chosen with care and packaged to arrive safely.");
        d.put("currency", "$");
        d.put("phone", "+1 (555) 123-4567");
        d.put("email", "hello@wanderlustsouvenirs.com");
        d.put("address", "123 Market Street, Anytown");
        d.put("workingHours", "Mon–Sat, 9am – 8pm");
        d.put("instagram", "");
        d.put("telegram", "");
        d.put("whatsapp", "");
        d.put("footerText", "Handpicked souvenirs, delivered with care.");
        d.put("shippingNote", "Worldwide shipping | Carefully packaged");
        d.put("adminPassword", "admin123");
        return d;
    }

    public String get(String key) {
        return settingRepository.findById(key)
                .map(Setting::getValue)
                .orElse(defaults().getOrDefault(key, ""));
    }

    public void set(String key, String value) {
        Setting s = settingRepository.findById(key).orElse(new Setting());
        s.setKey(key);
        s.setValue(value == null ? "" : value);
        settingRepository.save(s);
    }

    // A snapshot map of every setting (stored value or default), for templates.
    public Map<String, String> all() {
        Map<String, String> result = new LinkedHashMap<>(defaults());
        for (Setting s : settingRepository.findAll()) {
            result.put(s.getKey(), s.getValue());
        }
        return result;
    }

    // Make sure every default key exists in the database (called on startup).
    public void initDefaults() {
        Map<String, String> d = defaults();
        for (Map.Entry<String, String> e : d.entrySet()) {
            if (settingRepository.findById(e.getKey()).isEmpty()) {
                settingRepository.save(new Setting(e.getKey(), e.getValue()));
            }
        }
    }
}
