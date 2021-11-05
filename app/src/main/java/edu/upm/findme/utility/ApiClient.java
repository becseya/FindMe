package edu.upm.findme.utility;

import edu.upm.findme.model.User;
import okhttp3.FormBody;
import okhttp3.RequestBody;

class ApiClientProtected extends AsyncHttpClient {

    private static final String API_ENDPOINT = "https://findme.becsengo.hu/api.php";

    ApiClient.FailureHandler failureHandler;

    protected ApiClientProtected(ApiClient.FailureHandler failureHandler) {
        this.failureHandler = failureHandler;
    }

    static protected String getUrl(String command) {
        return API_ENDPOINT + "?command=" + command;
    }

    protected ResponseHandler successHandlerBuilder(SuccessHandler handler) {
        return (success, payload) -> {
            if (success) {
                try {
                    handler.onApiSuccess(payload);
                } catch (Exception e) {
                    failureHandler.onApiFailure(e.toString());
                }
            } else
                failureHandler.onApiFailure(payload);
        };
    }

    protected interface SuccessHandler {
        void onApiSuccess(String answer);
    }
}

public class ApiClient extends ApiClientProtected {

    public ApiClient(FailureHandler failureHandler) {
        super(failureHandler);
    }

    public void registerUser(User user, UserRegistrationHandler handler) {
        RequestBody requestBody = new FormBody.Builder()
                .add("name", user.getName())
                .add("phone", user.getPhoneNumber())
                .build();

        post(getUrl("user-add"), requestBody, successHandlerBuilder((answer) -> {
            handler.onUserAdded(Integer.parseInt(answer));
        }));
    }

    public interface UserRegistrationHandler {
        void onUserAdded(int id);
    }

    public interface FailureHandler {
        void onApiFailure(String errorDescription);
    }
}
