package edu.upm.findme.model;

import androidx.annotation.NonNull;

public class UserDetails {

    public static Status getStatusFromString(String string) throws RuntimeException {
        Status[] everyStatus = {Status.ONLINE, Status.OFFLINE, Status.LIVE};
        for (Status s : everyStatus)
            if (string.equals(s.toString()))
                return s;

        throw new RuntimeException("Unrecognized status: " + string);
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
