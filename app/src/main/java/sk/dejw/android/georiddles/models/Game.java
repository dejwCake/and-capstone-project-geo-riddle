package sk.dejw.android.georiddles.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class Game implements Parcelable {
    private int id;
    private UUID uuid;
    private String title;
    private String code;
    @SerializedName("gps_lat")
    private double gpsLat;
    @SerializedName("gps_lng")
    private double gpsLng;

    public Game(int id, UUID uuid, String title, String code, double gpsLat, double gpsLng) {
        this.id = id;
        this.uuid = uuid;
        this.title = title;
        this.code = code;
        this.gpsLat = gpsLat;
        this.gpsLng = gpsLng;
    }

    public Game(Parcel in) {
        this.id = in.readInt();
        this.uuid = UUID.fromString(in.readString());
        this.title = in.readString();
        this.code = in.readString();
        this.gpsLat = in.readDouble();
        this.gpsLng = in.readDouble();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public double getGpsLat() {
        return gpsLat;
    }

    public void setGpsLat(double gpsLat) {
        this.gpsLat = gpsLat;
    }

    public double getGpsLng() {
        return gpsLng;
    }

    public void setGpsLng(double gpsLng) {
        this.gpsLng = gpsLng;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(uuid.toString());
        dest.writeString(title);
        dest.writeString(code);
        dest.writeDouble(gpsLat);
        dest.writeDouble(gpsLng);
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
