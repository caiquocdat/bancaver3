package bum.keo.bancagame;

public class LevelModel {
    private int id;
    private LevelState state;
    private int imageResource;

    public LevelModel(int id, LevelState state, int imageResource) {
        this.id = id;
        this.state = state;
        this.imageResource = imageResource;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LevelState getState() {
        return state;
    }

    public void setState(LevelState state) {
        this.state = state;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }
}
