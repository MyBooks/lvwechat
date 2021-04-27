package com.lv.study.lvwechat.controller;

import com.lv.study.lvwechat.model.User;
import com.lv.study.lvwechat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.ws.Action;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/allUser")
    public Iterable<User> getUser(){
        Iterable<User> all = userRepository.findAll();
        return all;
    }

}
