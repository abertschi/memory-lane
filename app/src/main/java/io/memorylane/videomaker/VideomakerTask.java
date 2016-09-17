package io.memorylane.videomaker;

/**
 * Created by annam on 17.09.2016.
 */
public class VideomakerTask {
    public String task_name;
    public String definition;

    public VideomakerTask() {}

    public VideomakerTask(String task_name, String definition) {
        this.task_name = task_name;
        this.definition = definition;
    }
}
