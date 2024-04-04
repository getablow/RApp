package org.zerock.recipe.controller;

import lombok.extern.log4j.Log4j2;
import org.eclipse.angus.mail.imap.protocol.Item;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Log4j2
public class SampleJSONController {

    @PostMapping("/api/items")
    public ResponseEntity<String> saveItems(@RequestBody List<Item> items) {
        // 저장 로직
        return ResponseEntity.ok("저장 성공");
    }

    @GetMapping("/api/helloArr")
    public String[] helloArr(){

        log.info("helloArr..................");

        return new String[]{"AAA","BBB","CCC"};
    }

}
