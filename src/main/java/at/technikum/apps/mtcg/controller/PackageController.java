package at.technikum.apps.mtcg.controller;

import at.technikum.server.http.Request;
import at.technikum.server.http.Response;

public class PackageController implements Controller {
    @Override
    public boolean supports(String route) {
        return false;
    }

    @Override
    public Response handle(Request request) {
        return null;
    }
}
