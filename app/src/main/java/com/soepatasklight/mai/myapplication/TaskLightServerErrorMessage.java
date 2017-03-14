package com.soepatasklight.mai.myapplication;

/**
 * Created by mai on 2017/01/15.
 */

public class TaskLightServerErrorMessage {
    private String title;
    private String messege;

    public TaskLightServerErrorMessage(String tmpTitle,String tmpMessege){
        title = tmpTitle;
        messege = tmpMessege;
    }

    public String getMessege() {
        return messege;
    }


    public String getTitle() {
        return title;
    }

}
