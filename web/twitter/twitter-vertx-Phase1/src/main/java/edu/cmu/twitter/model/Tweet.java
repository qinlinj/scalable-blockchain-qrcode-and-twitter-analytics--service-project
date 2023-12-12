package edu.cmu.twitter.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * https://developer.twitter.com/en/docs/twitter-api/v1/data-dictionary/object-model/tweet
 */
public class Tweet {
    Date created_at;
    Long id;
    Long user_id;
    String text;
    String hashtag;
    Long in_reply_to_user_id;
    Long retweeted_status_user_id;

    public Tweet(Date created_at, Long id, Long user_id, String text, String hashtag, Long in_reply_to_user_id, Long retweeted_status_user_id) {
        this.created_at = created_at;
        this.id = id;
        this.user_id = user_id;
        this.text = text;
        this.hashtag = hashtag;
        this.in_reply_to_user_id = in_reply_to_user_id;
        this.retweeted_status_user_id = retweeted_status_user_id;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public Long getIn_reply_to_user_id() {
        return in_reply_to_user_id;
    }

    public void setIn_reply_to_user_id(Long in_reply_to_user_id) {
        this.in_reply_to_user_id = in_reply_to_user_id;
    }

    public Long getRetweeted_status_user_id() {
        return retweeted_status_user_id;
    }

    public void setRetweeted_status_user_id(Long retweeted_status_user_id) {
        this.retweeted_status_user_id = retweeted_status_user_id;
    }

    @Override
    public String toString() {
        return "Tweet{" +
                "created_at=" + created_at +
                ", id=" + id +
                ", user_id=" + user_id +
                ", text='" + text + '\'' +
                ", hashtag='" + hashtag + '\'' +
                ", in_reply_to_user_id=" + in_reply_to_user_id +
                ", retweeted_status_user_id=" + retweeted_status_user_id +
                '}';
    }
}
