package org.zerock.recipe.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.recipe.service.MemberService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberApiController {

    private final MemberService memberService;
    @DeleteMapping("/{mid}")
    public ResponseEntity<Void> deleteMember(@PathVariable String mid) {
        memberService.deleteMember(mid);
        return ResponseEntity.noContent().build();
    }
}
