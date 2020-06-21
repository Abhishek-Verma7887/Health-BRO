package com.example.hp.abhishekblogapp;

import java.util.List;

public class TopicsDocument {
    String TopicName;

    public TopicsDocument(){

    }
    public TopicsDocument(String tName){
TopicName=tName;
    }

    public String getTopicName() {
        return TopicName;
    }
    public void setTName(String tName){
        TopicName=tName;
    }
}
