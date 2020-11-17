package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.Size;

public class PostEditForm {

    @Size(min = 1, max = 100000)
    private String body;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

}
