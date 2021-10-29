package com.example.habitsmasher;

import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;
import java.util.Date;

/**
 * This is the Habit class
 * Its purpose is to store and retrieve the title, reason, and date of a given habit
 */
public class Habit extends DatabaseEntity implements Serializable {
    private String _title;
    private String _reason;
    private Date _date;

    public Habit () {
        // needed for firestore
    }

    public Habit (String title, String reason, Date date, long habitId) {
        super(habitId);
        _title = title;
        _reason = reason;
        _date = date;
    }

    /**
     *
     * @return _title : The title of the habit
     */
    @PropertyName("title")
    public String getTitle() {
        return _title;
    }

    /**
     *
     * @param title : The new title to be set
     */
    public void setTitle(String title) {
        _title = title;
    }

    /**
     *
     * @return _reason: The reason of the habit
     */
    @PropertyName("reason")
    public String getReason() {
        return _reason;
    }

    /**
     *
     * @param reason : The new reason to be set
     */
    public void setReason(String reason) {
        _reason = reason;
    }

    /**
     *
     * @return _date : The date of the habit
     */
    @PropertyName("date")
    public Date getDate() {
        return _date;
    }

    /**
     *
     * @param date : The new date to be set
     */
    public void setDate(Date date) {
        _date = date;
    }
}
