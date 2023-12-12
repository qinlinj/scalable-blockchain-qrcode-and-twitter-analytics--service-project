CREATE TABLE CompleteTweets AS (
    select
        Tweets.id,
        Tweets.user_id,
        Tweets.text,
        Tweets.entities_hashtags,
        Tweets.created_at,
        Tweets.in_reply_to_user_id as reply_id,
        Tweets.retweeted_status_user_id as retweet_id,
        Senders.screen_name as sender_screen_name,
        Senders.description as sender_description,
        Senders.hashtag_counts as sender_hashtag_counts,
        Contacts.screen_name as contact_screen_name,
        Contacts.description as contact_description,
        Contacts.hashtag_counts as contact_hashtag_counts,
        (case when Tweets.in_reply_to_user_id is not null then "reply"
              when Tweets.retweeted_status_user_id is not null then "retweet"
            end) as type
    from Tweets
             left join Users as Senders on Tweets.user_id=Senders.id
             left join Users as Contacts on Tweets.in_reply_to_user_id=Contacts.id
        or Tweets.retweeted_status_user_id=Contacts.id
)