package org.zerock.recipe.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@Log4j2
@RequiredArgsConstructor
public class VideoController {

    private List<String> videoLinks = new ArrayList<>();

    @GetMapping("/index1")
    public String index(Model model) {
        model.addAttribute("videoLinks", videoLinks);
        return "recipe/index1";
    }

    @PostMapping("/addVideo")
    public String addVideo(@RequestParam String videoLink) {
        videoLinks.add(videoLink);
        return "redirect:/index1";
    }

    public String extractVideoId(String videoLink) {
        String videoId = null;
        String regex = "^(?:https?://)?(?:www\\.)?(?:youtube\\.com(?:/embed/|/v/|/watch\\?v=)|youtu\\.be/)([a-zA-Z0-9_-]{11})";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(videoLink);
        if (matcher.find()) {
            videoId = matcher.group(1);
        }
        return videoId;
    }
}
