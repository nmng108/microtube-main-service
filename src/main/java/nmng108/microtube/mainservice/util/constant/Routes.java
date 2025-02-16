package nmng108.microtube.mainservice.util.constant;

public interface Routes {
    interface Auth {
        String basePath = "/auth";
        String login = Auth.basePath + "/login";
        String register = Auth.basePath + "/register";
        String forgot = Auth.basePath + "/forgot";
    }

    interface User {
        String basePath = "/users";
    }

    interface Channel {
        String basePath = "/channels";
    }

    interface Video {
        String basePath = "/videos";
    }
}
