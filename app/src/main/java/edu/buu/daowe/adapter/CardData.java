package edu.buu.daowe.adapter;

import java.io.Serializable;

public class CardData implements Serializable {
    String date;

    String teachername;
    String courseposition;
    String coursetime;
    String coursenum;

    public CardData(String date, String teachername, String courseposition, String coursetime, String coursenum, boolean coursestatus, String coursename) {
        this.date = date;

        this.teachername = teachername;
        this.courseposition = courseposition;
        this.coursetime = coursetime;
        this.coursenum = coursenum;
        this.coursestatus = coursestatus;
        this.coursename = coursename;
    }

    public String getCoursenum() {
        return coursenum;
    }

    public void setCoursenum(String coursenum) {
        this.coursenum = coursenum;
    }

    public boolean isCoursestatus() {
        return coursestatus;
    }

    public void setCoursestatus(boolean coursestatus) {
        this.coursestatus = coursestatus;
    }

    boolean coursestatus;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getTeachername() {
        return teachername;
    }

    public void setTeachername(String teachername) {
        this.teachername = teachername;
    }

    public String getCourseposition() {
        return courseposition;
    }

    public void setCourseposition(String courseposition) {
        this.courseposition = courseposition;
    }

    public String getCoursetime() {
        return coursetime;
    }

    public void setCoursetime(String coursetime) {
        this.coursetime = coursetime;
    }

    public String getCoursename() {
        return coursename;
    }

    public void setCoursename(String coursename) {
        this.coursename = coursename;
    }

    String coursename;

    @Override
    public String toString() {
        return "CardData{" +
                "date='" + date + '\'' +
                ", teachername='" + teachername + '\'' +
                ", courseposition='" + courseposition + '\'' +
                ", coursetime='" + coursetime + '\'' +
                ", coursename='" + coursename + '\'' +
                '}';
    }
}
