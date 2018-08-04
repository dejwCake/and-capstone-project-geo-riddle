package sk.dejw.android.georiddles.models;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

public class Riddle implements Parcelable {
    private long id;
    private UUID gameUuid;
    private String title;
    private int no;
    private double gpsLat;
    private double gpsLng;
    private String imagePath;
    private String question;
    private String answer;

    private boolean active;
    private boolean locationChecked;
    private boolean riddleSolved;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UUID getGameUuid() {
        return gameUuid;
    }

    public void setGameUuid(UUID gameUuid) {
        this.gameUuid = gameUuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isLocationChecked() {
        return locationChecked;
    }

    public void setLocationChecked(boolean locationChecked) {
        this.locationChecked = locationChecked;
    }

    public boolean isRiddleSolved() {
        return riddleSolved;
    }

    public void setRiddleSolved(boolean riddleSolved) {
        this.riddleSolved = riddleSolved;
    }

    public Riddle(long id, UUID gameUuid, String title, int no, double gpsLat, double gpsLng, String imagePath, String question, String answer, boolean active, boolean locationChecked, boolean riddleSolved) {
        this.id = id;

        this.gameUuid = gameUuid;
        this.title = title;
        this.no = no;
        this.gpsLat = gpsLat;
        this.gpsLng = gpsLng;
        this.imagePath = imagePath;
        this.question = question;
        this.answer = answer;

        this.active = active;
        this.locationChecked = locationChecked;
        this.riddleSolved = riddleSolved;
    }

    public Riddle(UUID gameUuid, String title, int no, double gpsLat, double gpsLng, String imagePath, String question, String answer) {
        this.gameUuid = gameUuid;
        this.title = title;
        this.no = no;
        this.gpsLat = gpsLat;
        this.gpsLng = gpsLng;
        this.imagePath = imagePath;
        this.question = question;
        this.answer = answer;

        this.active = false;
        this.locationChecked = false;
        this.riddleSolved = false;
    }

    protected Riddle(Parcel in) {
        id = in.readLong();

        gameUuid = UUID.fromString(in.readString());
        title = in.readString();
        no = in.readInt();
        gpsLat = in.readDouble();
        gpsLng = in.readDouble();
        imagePath = in.readString();
        question = in.readString();
        answer = in.readString();

        active = in.readByte() != 0;
        locationChecked = in.readByte() != 0;
        riddleSolved = in.readByte() != 0;
    }

    public static final Creator<Riddle> CREATOR = new Creator<Riddle>() {
        @Override
        public Riddle createFromParcel(Parcel in) {
            return new Riddle(in);
        }

        @Override
        public Riddle[] newArray(int size) {
            return new Riddle[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);

        dest.writeString(gameUuid.toString());
        dest.writeString(title);
        dest.writeInt(no);
        dest.writeDouble(gpsLat);
        dest.writeDouble(gpsLng);
        dest.writeString(imagePath);
        dest.writeString(question);
        dest.writeString(answer);

        dest.writeByte((byte) (active ? 1 : 0));
        dest.writeByte((byte) (locationChecked ? 1 : 0));
        dest.writeByte((byte) (riddleSolved ? 1 : 0));
    }

    public Location getLocation() {
        Location location = new Location("");
        location.setLatitude(this.gpsLat);
        location.setLongitude(this.gpsLng);
        return location;
    }
}
