package com.RadioSfax.acer.radiosfax;

/**
 * Created by lenovo on 20/07/2017.
 */

public class NewsValues {

    public String title;
    public String image;
    public String isDone;
    public String isSeen;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIsDone() {
        return isDone;
    }

    public void setIsDone(String isDone) {
        this.isDone = isDone;
    }

    public String getIsSeen() {
        return isSeen;
    }

    public void setIsSeen(String isSeen) {
        this.isSeen = isSeen;
    }

    public NewsValues(String title, String image, String isDone, String isSeen) {
        this.title = title;
        this.image = image;
        this.isDone = isDone;
        this.isSeen = isSeen;
    }

    public NewsValues(){

    }


}
