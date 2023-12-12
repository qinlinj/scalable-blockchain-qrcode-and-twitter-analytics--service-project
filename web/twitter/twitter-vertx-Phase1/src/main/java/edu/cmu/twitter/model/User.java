package edu.cmu.twitter.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class User {
    Long userId;
    String screenName;
    String description;
    String contactTweetText;
    Date created_at;
    double finalScore;

    public User(Date created_at, Long userId, String screenName, String description, String contactTweetText, double finalScore) {
        this.created_at = created_at;
        this.userId = userId;
        this.screenName = screenName;
        this.description = description;
        this.contactTweetText = contactTweetText;
        this.finalScore = finalScore;
    }
    public static Date parseDate(String dateStr) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return format.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Long getUserId() {
        return userId;
    }

    public double getFinalScore() {
        return finalScore;
    }
    public void setFinalScore(double finalScore) {
        this.finalScore = finalScore;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getDescription() {
        return description;
    }

    public String getContactTweetText() {
        return contactTweetText;
    }

    public void setContactTweetText(String contactTweetText) {
        this.contactTweetText = contactTweetText;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", screenName='" + screenName + '\'' +
                ", description='" + description + '\'' +
                ", contactTweetText='" + contactTweetText + '\'' +
                ", created_at=" + created_at +
                ", finalScore=" + finalScore +
                '}';
    }
}
