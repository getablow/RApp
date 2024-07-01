package org.zerock.recipe.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@Log4j2
@RequiredArgsConstructor
public class AdminController {

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/home")
    public void adminHome() {

    }

    @PreAuthorize("hasRole('ADMIN')") //사전에 권한 체크 postauthorize-사후
    @GetMapping("/statistics")
    public void statisticsGET(){

    }

    @PreAuthorize("hasRole('ADMIN')") //사전에 권한 체크 postauthorize-사후
    @GetMapping("/inquiry")
    public void inquiryGET(){

    }

    @PreAuthorize("hasRole('ADMIN')") //사전에 권한 체크 postauthorize-사후
    @GetMapping("/managementUser")
    public void managementUserGET(){

    }

    @PreAuthorize("hasRole('ADMIN')") //사전에 권한 체크 postauthorize-사후
    @GetMapping("/managementPost")
    public void managementPostGET(){

    }

}
