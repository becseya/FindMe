package edu.upm.findme.model;

import androidx.annotation.NonNull;

public class UserDetails extends User {

    Status status = Status.OFFLINE;

    public UserDetails(int id, String name, String phoneNumber) {
        super(id, name, phoneNumber);
    }

    public UserDetails(User user) {
        super(user.getId(), user.getName(), user.getPhoneNumber());
    }

    public static Status getStatusFromString(String string) throws RuntimeException {
        Status[] everyStatus = {Status.ONLINE, Status.OFFLINE, Status.LIVE};
        for (Status s : everyStatus)
            if (string.equals(s.toString()))
                return s;

        throw new RuntimeException("Unrecognized status: " + string);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        OFFLINE {
            @NonNull
            public String toString() {
                return "offline";
            }
        },
        ONLINE {
            @NonNull
            public String toString() {
                return "online";
            }
        },
        LIVE {
            @NonNull
            public String toString() {
                return "live";
            }
        }
    }
}
