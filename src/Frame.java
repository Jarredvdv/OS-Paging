import java.util.ArrayList;

class Frame {

    int frame_id;
    Page page;

    static ArrayList<Frame> frames = new ArrayList<>();

    Frame(int frame_id) {
        this.frame_id = frame_id;
    }

    void evict() {
        page.process.residency += page.residency;
        page.process.evicted_count++;
        page.residency = 0;
        page.frame = null;
        page = null;
    }
}
