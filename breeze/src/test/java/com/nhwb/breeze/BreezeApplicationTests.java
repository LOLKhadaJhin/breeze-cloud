package com.nhwb.breeze;

import com.nhwb.breeze.domain.Avoid;
import com.nhwb.breeze.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class BreezeApplicationTests {

    @Autowired
    AvoidService avoidService;
    @Autowired
    PermissionFileService permissionFileService;
    @Autowired
    PermissionService permissionService;
    @Autowired
    UserPermissionService userPermissionService;
    @Autowired
    UserService userService;
    @Test

    void contextLoads() {
        ergodic(avoidService.list());
        ergodic(permissionFileService.list());
        ergodic(permissionService.list());
        ergodic(userPermissionService.list());
        ergodic(userService.list());
    }
    private void ergodic(List list){
        list.forEach(System.out::println);
    }
}
