package com.android.campusquora.model;

public class Comment {
    String text;
    Long commentTime;
    Long upvotes;
    Long downvotes;
    String author;

    public Comment(String text, Long commentTime, Long upvotes, Long downvotes, String author) {
        this.text = text;
        this.commentTime = commentTime;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public String getCommentTime() {
        return commentTime.toString();
    }

    public Long getUpvotes() {
        return upvotes;
    }

    public Long getDownvotes() {
        return downvotes;
    }

    public String getAuthor() {
        return author;
    }
}
