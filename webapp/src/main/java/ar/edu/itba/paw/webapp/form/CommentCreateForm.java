package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.Annotations.SpacesNormalization;

import javax.validation.constraints.Size;

public class CommentCreateForm {

    @Size( min = 1, max = 100000)
    @SpacesNormalization
    private String commentBody;

    private long postId;

    private Long parentId;

    private long userId;

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getCommentBody() {
        return commentBody;
    }

    public void setCommentBody(String commentBody) {
        this.commentBody = commentBody;
    }
}