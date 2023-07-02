package com.senior.dreamteam.service;

import org.graalvm.polyglot.*;
public class BookService {

    private void test(){
        Context context = Context.create();
        Value value = context.eval("js", "console.log('Hello, JavaScript from Scala!')");
        System.out.println(value.toString());
    }

}
