package edu.upm.findme.utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
        void onApiSuccess(String answer) throws JSONException;
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

    public void listUsers(UserListHandler handler) {
        get(getUrl("user-list"), successHandlerBuilder((answer) -> {
            List<User> users = new ArrayList<>();
            JSONArray array = new JSONArray(answer);

            for (int i = 0; i < array.length(); i++) {
                JSONObject u = array.getJSONObject(i);
                users.add(new User(u.getInt("id"), u.getString("name"), u.getString("phone")));
            }

            handler.onUsersListed(users);
        }));
    }

    public interface UserRegistrationHandler {
        void onUserAdded(int id);
    }

    public interface UserListHandler {
        void onUsersListed(List<User> users);
    }

    public interface FailureHandler {
        void onApiFailure(String errorDescription);
    }
}
