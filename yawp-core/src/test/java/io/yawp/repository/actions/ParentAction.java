package io.yawp.repository.actions;

import io.yawp.commons.http.HttpException;
import io.yawp.commons.http.annotation.*;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.basic.Pojo;
import io.yawp.repository.models.parents.Parent;

import java.util.List;
import java.util.Map;

public class ParentAction extends AbstractAction<Parent> {

    @PUT("touched")
    public Parent touch(IdRef<Parent> id) {
        Parent parent = id.fetch();
        parent.setName("touched " + parent.getName());
        return parent;
    }

    @PUT("touchedParams")
    public Parent touchedParams(IdRef<Parent> id, Map<String, String> params) {
        Parent parent = id.fetch();
        parent.setName("touched " + parent.getName() + " by " + params.get("arg"));
        return parent;
    }

    @PUT("touched")
    public List<Parent> touch() {
        List<Parent> parents = yawp(Parent.class).order("name").list();
        for (Parent parent : parents) {
            parent.setName("touched " + parent.getName());
        }
        return parents;
    }

    @GET("something")
    public String something() {
        return "touched";
    }

    @PUT("touched_with_params")
    public Parent touchWithParams(IdRef<Parent> id, Map<String, String> params) {
        Parent parent = id.fetch();
        parent.setName("touched " + parent.getName() + " " + params.get("x"));
        return parent;
    }

    @PUT("touched_with_params")
    public List<Parent> touchWithParams(Map<String, String> params) {
        List<Parent> parents = yawp(Parent.class).list();
        for (Parent parent : parents) {
            parent.setName("touched " + parent.getName() + " " + params.get("x"));
        }
        return parents;
    }

    @GET("echo")
    public Parent echo(IdRef<Parent> id) {
        return id.fetch();
    }

    @GET("echo")
    public List<Parent> echoAll() {
        return yawp(Parent.class).list();
    }

    @Atomic
    @PUT("atomic_rollback")
    public void atomicRollback() {
        yawp.save(new Parent("xpto"));
        throw new FakeException();
    }

    @GET("all-http-verbs")
    @POST("all-http-verbs")
    @PUT("all-http-verbs")
    @PATCH("all-http-verbs")
    @DELETE("all-http-verbs")
    public String allHttpVerbs(IdRef<Parent> id) {
        return "ok";
    }

    @POST("with-json-string")
    public String withJsonString(IdRef<Parent> id, String json) {
        Pojo pojo = from(json, Pojo.class);
        return pojo.getStringValue();
    }

    @POST
    public String collectionWithJsonObject(Pojo pojo) {
        return pojo.getStringValue();
    }

    @POST("with-json-object")
    public String withJsonObject(IdRef<Parent> id, Pojo pojo) {
        return pojo.getStringValue();
    }

    @POST("with-json-list")
    public String withJsonList(IdRef<Parent> id, List<Pojo> pojos) {
        return pojos.get(0).getStringValue() + ' ' + pojos.get(1).getStringValue();
    }

    @PUT
    public void withException() {
        throw new HttpException(400, json(new Message("sample json exception body")));
    }

    @GET
    @POST
    @PUT
    public void withVoidReturn() {
    }

    @GET
    public String checkIfFixturesServletDisableShields() {
        return "xpto";
    }

    class Message {
        private String title;

        public Message(String title) {
            this.title = title;
        }
    }

}
