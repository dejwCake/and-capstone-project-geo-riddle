package sk.dejw.android.georiddles.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

public class Game implements Parcelable {
    private UUID uuid;
    private String title;
    private String code;

    public Game(UUID vUuid, String vTitle, String vCode)
    {
        uuid = vUuid;
        title = vTitle;
        code = vCode;
    }

    public Game(Parcel in) {
        this.uuid = UUID.fromString(in.readString());
        this.title = in.readString();
        this.code = in.readString();
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uuid.toString());
        dest.writeString(title);
        dest.writeString(code);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Game> CREATOR = new Creator<Game>() {
        @Override
        public Game createFromParcel(Parcel in) {
            return new Game(in);
        }

        @Override
        public Game[] newArray(int size) {
            return new Game[size];
        }
    };
}
